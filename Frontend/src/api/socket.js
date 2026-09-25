import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { API_BASE_URL } from './axios'

let client = null

/**
 * Opens a STOMP-over-SockJS connection authenticated with the current JWT and
 * subscribes to the user's private notification queue, invoking onNotification
 * for every push. Returns a disconnect function.
 */
export function connectNotifications(token, onNotification) {
  if (!token) return () => {}

  client = new Client({
    webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    reconnectDelay: 5000,
    onConnect: () => {
      client.subscribe('/user/queue/notifications', (message) => {
        try {
          const notification = JSON.parse(message.body)
          onNotification(notification)
        } catch (e) {
          console.error('Failed to parse notification', e)
        }
      })
    },
  })

  client.activate()

  return () => {
    if (client) {
      client.deactivate()
      client = null
    }
  }
}

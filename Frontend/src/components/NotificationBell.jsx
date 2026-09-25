import React, { useEffect, useState } from 'react'
import api from '../api/axios.js'
import { useAuth } from '../context/AuthContext.jsx'

export default function NotificationBell() {
  const { notifications, setNotifications } = useAuth()
  const [open, setOpen] = useState(false)
  const [loaded, setLoaded] = useState(false)

  useEffect(() => {
    api.get('/notifications').then(({ data }) => {
      setNotifications(data)
      setLoaded(true)
    }).catch(() => setLoaded(true))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const unreadCount = notifications.filter((n) => !n.read).length

  const markRead = async (id) => {
    try {
      await api.post(`/notifications/${id}/read`)
      setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, read: true } : n)))
    } catch (e) {
      // no-op
    }
  }

  return (
    <div className="position-relative">
      <button className="btn btn-outline-light btn-sm position-relative" onClick={() => setOpen((o) => !o)}>
        <i className="fa-solid fa-bell"></i>
        {unreadCount > 0 && (
          <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
            {unreadCount}
          </span>
        )}
      </button>
      {open && (
        <div className="notification-dropdown shadow">
          <div className="notification-header">Notifications</div>
          {!loaded && <div className="p-3 text-muted small">Loading...</div>}
          {loaded && notifications.length === 0 && (
            <div className="p-3 text-muted small">No notifications yet</div>
          )}
          {notifications.slice(0, 15).map((n) => (
            <div key={n.id} className={`notification-item ${n.read ? '' : 'unread'}`} onClick={() => !n.read && markRead(n.id)}>
              <div className="small">{n.message}</div>
              <div className="text-muted" style={{ fontSize: '0.72rem' }}>
                {n.createdAt ? new Date(n.createdAt).toLocaleString() : ''}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

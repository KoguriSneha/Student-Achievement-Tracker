import React, { createContext, useContext, useEffect, useState, useCallback } from 'react'
import api from '../api/axios'
import { connectNotifications } from '../api/socket'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('sat_user')
    return stored ? JSON.parse(stored) : null
  })
  const [notifications, setNotifications] = useState([])
  const [loading, setLoading] = useState(false)

  const pushNotification = useCallback((notification) => {
    setNotifications((prev) => [notification, ...prev])
  }, [])

  useEffect(() => {
    const token = localStorage.getItem('sat_token')
    if (!token || !user) return
    const disconnect = connectNotifications(token, pushNotification)
    return disconnect
  }, [user, pushNotification])

  const login = async (username, password) => {
    setLoading(true)
    try {
      const { data } = await api.post('/auth/login', { username, password })
      localStorage.setItem('sat_token', data.token)
      localStorage.setItem('sat_user', JSON.stringify(data))
      setUser(data)
      return data
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    localStorage.removeItem('sat_token')
    localStorage.removeItem('sat_user')
    setUser(null)
    setNotifications([])
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, loading, notifications, setNotifications }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

import React, { useState } from 'react'
import api from '../api/axios.js'

export default function ChangePasswordForm() {
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [message, setMessage] = useState(null)
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setMessage(null)
    setError('')
    try {
      await api.post('/auth/change-password', { currentPassword, newPassword })
      setMessage('Password changed successfully')
      setCurrentPassword('')
      setNewPassword('')
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not change password')
    }
  }

  return (
    <div className="card shadow-sm p-4">
      <div className="fw-semibold mb-3">Change Password</div>
      {message && <div className="alert alert-success py-2">{message}</div>}
      {error && <div className="alert alert-danger py-2">{error}</div>}
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Current Password</label>
          <input type="password" className="form-control" value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} required />
        </div>
        <div className="mb-3">
          <label className="form-label">New Password</label>
          <input type="password" className="form-control" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required minLength={4} />
        </div>
        <button className="btn btn-primary" type="submit">Update Password</button>
      </form>
    </div>
  )
}

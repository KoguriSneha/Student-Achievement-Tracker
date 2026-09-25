import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Login() {
  const { login, loading } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const data = await login(username, password)
      navigate(`/${data.role.toLowerCase()}/dashboard`)
    } catch (err) {
      setError(err?.response?.data?.message || 'Invalid username or password')
    }
  }

  return (
    <div className="login-page d-flex align-items-center justify-content-center">
      <div className="login-card shadow">
        <div className="text-center mb-4">
          <i className="fa-solid fa-award fa-2x text-primary mb-2"></i>
          <h4 className="fw-bold mb-0">Student Achievement Tracker</h4>
          <div className="text-muted small">Sign in to continue</div>
        </div>

        {error && <div className="alert alert-danger py-2">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Username</label>
            <input className="form-control" value={username} onChange={(e) => setUsername(e.target.value)} required />
          </div>
          <div className="mb-3">
            <label className="form-label">Password</label>
            <input type="password" className="form-control" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </div>
          <button className="btn btn-primary w-100" type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Login'}
          </button>
        </form>

        <div className="mt-3 text-center small">
          Don't have an account? <Link to="/register">Sign up</Link>
        </div>

        <div className="mt-4 small text-muted">
          <div className="fw-semibold mb-1">Demo accounts (seeded on first run):</div>
          <div>Student: <code>student1 / student1</code></div>
          <div>Staff (class teacher): <code>staff_cse / staff_cse</code></div>
          <div>HOD: <code>hod_cse / hod_cse</code></div>
        </div>
      </div>
    </div>
  )
}

import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import NotificationBell from './NotificationBell.jsx'

const LINKS = {
  STUDENT: [
    { to: '/student/dashboard', label: 'Dashboard' },
    { to: '/student/upload', label: 'Upload' },
    { to: '/student/certificates', label: 'My Certificates' },
    { to: '/student/analytics', label: 'Analytics' },
    { to: '/student/profile', label: 'Profile' },
  ],
  STAFF: [
    { to: '/staff/dashboard', label: 'Dashboard' },
    { to: '/staff/approvals', label: 'Approvals' },
    { to: '/staff/profile', label: 'Profile' },
  ],
  HOD: [
    { to: '/hod/dashboard', label: 'Dashboard' },
    { to: '/hod/approvals', label: 'Approvals' },
    { to: '/hod/analytics', label: 'Analytics' },
    { to: '/hod/profile', label: 'Profile' },
  ],
}

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  if (!user) return null

  const links = LINKS[user.role] || []

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-dark sat-navbar shadow-sm">
      <div className="container-fluid">
        <Link className="navbar-brand fw-bold" to={`/${user.role.toLowerCase()}/dashboard`}>
          <i className="fa-solid fa-award me-2"></i>
          Achievement Tracker
        </Link>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#satNav">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="satNav">
          <ul className="navbar-nav me-auto">
            {links.map((l) => (
              <li className="nav-item" key={l.to}>
                <Link className="nav-link" to={l.to}>{l.label}</Link>
              </li>
            ))}
          </ul>
          <div className="d-flex align-items-center gap-3">
            <NotificationBell />
            <span className="text-light small">
              {user.name} <span className="badge bg-light text-dark ms-1">{user.role}</span>
            </span>
            <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
              <i className="fa-solid fa-right-from-bracket me-1"></i>Logout
            </button>
          </div>
        </div>
      </div>
    </nav>
  )
}

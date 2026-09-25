import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext.jsx'
import Navbar from './components/Navbar.jsx'
import PrivateRoute from './components/PrivateRoute.jsx'

import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'

import StudentDashboard from './pages/student/Dashboard.jsx'
import StudentUpload from './pages/student/Upload.jsx'
import StudentCertificates from './pages/student/Certificates.jsx'
import StudentAnalytics from './pages/student/Analytics.jsx'
import StudentProfile from './pages/student/Profile.jsx'

import StaffDashboard from './pages/staff/Dashboard.jsx'
import StaffApprovals from './pages/staff/Approvals.jsx'
import StaffProfile from './pages/staff/Profile.jsx'

import HodDashboard from './pages/hod/Dashboard.jsx'
import HodApprovals from './pages/hod/Approvals.jsx'
import HodAnalytics from './pages/hod/Analytics.jsx'
import HodProfile from './pages/hod/Profile.jsx'

export default function App() {
  const { user } = useAuth()

  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/login" element={user ? <Navigate to={`/${user.role.toLowerCase()}/dashboard`} /> : <Login />} />
        <Route path="/register" element={user ? <Navigate to={`/${user.role.toLowerCase()}/dashboard`} /> : <Register />} />

        <Route path="/student/dashboard" element={<PrivateRoute role="STUDENT"><StudentDashboard /></PrivateRoute>} />
        <Route path="/student/upload" element={<PrivateRoute role="STUDENT"><StudentUpload /></PrivateRoute>} />
        <Route path="/student/certificates" element={<PrivateRoute role="STUDENT"><StudentCertificates /></PrivateRoute>} />
        <Route path="/student/analytics" element={<PrivateRoute role="STUDENT"><StudentAnalytics /></PrivateRoute>} />
        <Route path="/student/profile" element={<PrivateRoute role="STUDENT"><StudentProfile /></PrivateRoute>} />

        <Route path="/staff/dashboard" element={<PrivateRoute role="STAFF"><StaffDashboard /></PrivateRoute>} />
        <Route path="/staff/approvals" element={<PrivateRoute role="STAFF"><StaffApprovals /></PrivateRoute>} />
        <Route path="/staff/profile" element={<PrivateRoute role="STAFF"><StaffProfile /></PrivateRoute>} />

        <Route path="/hod/dashboard" element={<PrivateRoute role="HOD"><HodDashboard /></PrivateRoute>} />
        <Route path="/hod/approvals" element={<PrivateRoute role="HOD"><HodApprovals /></PrivateRoute>} />
        <Route path="/hod/analytics" element={<PrivateRoute role="HOD"><HodAnalytics /></PrivateRoute>} />
        <Route path="/hod/profile" element={<PrivateRoute role="HOD"><HodProfile /></PrivateRoute>} />

        <Route path="*" element={<Navigate to={user ? `/${user.role.toLowerCase()}/dashboard` : '/login'} />} />
      </Routes>
    </>
  )
}

import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import ChangePasswordForm from '../../components/ChangePasswordForm.jsx'

export default function StaffProfile() {
  const [staff, setStaff] = useState(null)

  useEffect(() => {
    api.get('/staff/profile').then(({ data }) => setStaff(data))
  }, [])

  if (!staff) return <div className="p-5 text-center text-muted">Loading...</div>

  return (
    <div className="container py-4" style={{ maxWidth: 640 }}>
      <h4 className="fw-bold mb-4">My Profile</h4>
      <div className="card shadow-sm p-4 mb-4">
        <dl className="row mb-0">
          <dt className="col-sm-4">Name</dt><dd className="col-sm-8">{staff.name}</dd>
          <dt className="col-sm-4">Staff ID</dt><dd className="col-sm-8">{staff.staffId}</dd>
          <dt className="col-sm-4">Branch</dt><dd className="col-sm-8">{staff.branch}</dd>
          <dt className="col-sm-4">Section</dt><dd className="col-sm-8">{staff.section}</dd>
          <dt className="col-sm-4">Year</dt><dd className="col-sm-8">{staff.year}</dd>
          <dt className="col-sm-4">Email</dt><dd className="col-sm-8">{staff.email}</dd>
          <dt className="col-sm-4">Username</dt><dd className="col-sm-8">{staff.username}</dd>
        </dl>
      </div>
      <ChangePasswordForm />
    </div>
  )
}

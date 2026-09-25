import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import ChangePasswordForm from '../../components/ChangePasswordForm.jsx'

export default function HodProfile() {
  const [hod, setHod] = useState(null)

  useEffect(() => {
    api.get('/hod/profile').then(({ data }) => setHod(data))
  }, [])

  if (!hod) return <div className="p-5 text-center text-muted">Loading...</div>

  return (
    <div className="container py-4" style={{ maxWidth: 640 }}>
      <h4 className="fw-bold mb-4">My Profile</h4>
      <div className="card shadow-sm p-4 mb-4">
        <dl className="row mb-0">
          <dt className="col-sm-4">Name</dt><dd className="col-sm-8">{hod.name}</dd>
          <dt className="col-sm-4">HOD ID</dt><dd className="col-sm-8">{hod.hodId}</dd>
          <dt className="col-sm-4">Branch</dt><dd className="col-sm-8">{hod.branch}</dd>
          <dt className="col-sm-4">Email</dt><dd className="col-sm-8">{hod.email}</dd>
          <dt className="col-sm-4">Username</dt><dd className="col-sm-8">{hod.username}</dd>
        </dl>
      </div>
      <ChangePasswordForm />
    </div>
  )
}

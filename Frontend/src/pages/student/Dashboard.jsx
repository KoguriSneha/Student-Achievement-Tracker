import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import StatCard from '../../components/StatCard.jsx'
import CertificateTable from '../../components/CertificateTable.jsx'

export default function StudentDashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/student/dashboard').then(({ data }) => setData(data)).finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="p-5 text-center text-muted">Loading dashboard...</div>
  if (!data) return null

  const { student, certificates, stats } = data

  return (
    <div className="container-fluid py-4">
      <h4 className="fw-bold mb-1">Welcome back, {student.name.split(' ')[0]}!</h4>
      <div className="text-muted mb-4">{student.branch} - {student.section} | Year {student.year} | Class Teacher: {student.classTeacherName || 'Not assigned'}</div>

      <div className="row g-3 mb-4">
        <StatCard label="Total" value={stats.total} icon="fa-solid fa-file-lines" color="primary" />
        <StatCard label="Approved" value={stats.approved} icon="fa-solid fa-circle-check" color="success" />
        <StatCard label="Pending" value={stats.pending} icon="fa-solid fa-hourglass-half" color="warning" />
        <StatCard label="Rejected" value={stats.rejected} icon="fa-solid fa-circle-xmark" color="danger" />
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-white fw-semibold">Recent Certificates</div>
        <CertificateTable certificates={certificates.slice(0, 8)} />
      </div>
    </div>
  )
}

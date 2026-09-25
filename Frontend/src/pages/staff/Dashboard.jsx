import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import StatCard from '../../components/StatCard.jsx'

export default function StaffDashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/staff/dashboard').then(({ data }) => setData(data)).finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="p-5 text-center text-muted">Loading dashboard...</div>
  if (!data) return null

  const { staff, students, pendingCount } = data

  return (
    <div className="container-fluid py-4">
      <h4 className="fw-bold mb-1">Welcome, {staff.name}</h4>
      <div className="text-muted mb-4">Class Teacher - {staff.branch} {staff.section}, Year {staff.year}</div>

      <div className="row g-3 mb-4">
        <StatCard label="Students" value={students.length} icon="fa-solid fa-user-graduate" color="primary" />
        <StatCard label="Pending Reviews" value={pendingCount} icon="fa-solid fa-hourglass-half" color="warning" />
      </div>

      <div className="card shadow-sm">
        <div className="card-header bg-white fw-semibold">My Students</div>
        <div className="table-responsive">
          <table className="table table-hover align-middle mb-0">
            <thead><tr><th>Name</th><th>Student ID</th><th>Branch</th><th>Section</th><th>Year</th></tr></thead>
            <tbody>
              {students.map((s) => (
                <tr key={s.id}>
                  <td>{s.name}</td><td>{s.studentId}</td><td>{s.branch}</td><td>{s.section}</td><td>{s.year}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import StatCard from '../../components/StatCard.jsx'
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts'

const COLORS = ['#4f46e5', '#22c55e', '#f59e0b', '#ef4444', '#06b6d4', '#a855f7']

function toChartData(obj) {
  return Object.entries(obj || {}).map(([name, value]) => ({ name, value }))
}

export default function HodDashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/hod/dashboard').then(({ data }) => setData(data)).finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="p-5 text-center text-muted">Loading dashboard...</div>
  if (!data) return null

  const { hod, pendingCount, analytics } = data
  const eventData = toChartData(analytics?.eventTypeData)

  return (
    <div className="container-fluid py-4">
      <h4 className="fw-bold mb-1">Welcome, {hod.name}</h4>
      <div className="text-muted mb-4">Head of Department - {hod.branch}</div>

      <div className="row g-3 mb-4">
        <StatCard label="Students" value={analytics?.totalStudents ?? 0} icon="fa-solid fa-user-graduate" color="primary" />
        <StatCard label="Approved Certificates" value={analytics?.totalCertificates ?? 0} icon="fa-solid fa-circle-check" color="success" />
        <StatCard label="Pending Reviews" value={pendingCount} icon="fa-solid fa-hourglass-half" color="warning" />
        <StatCard label="Winning Entries" value={analytics?.totalWinning ?? 0} icon="fa-solid fa-trophy" color="danger" />
      </div>

      <div className="row g-4">
        <div className="col-md-5">
          <div className="card shadow-sm p-3">
            <div className="fw-semibold mb-2">Certificates by Event Type</div>
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie data={eventData} dataKey="value" nameKey="name" outerRadius={90} label>
                  {eventData.map((entry, index) => <Cell key={index} fill={COLORS[index % COLORS.length]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
        <div className="col-md-7">
          <div className="card shadow-sm">
            <div className="card-header bg-white fw-semibold">Top Performers</div>
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead><tr><th>Name</th><th>Section</th><th>Certificates</th><th>Winning</th></tr></thead>
                <tbody>
                  {(analytics?.topPerformers || []).map((s) => (
                    <tr key={s.studentId}>
                      <td>{s.name}</td><td>{s.section}</td><td>{s.certificateCount}</td><td>{s.winningCount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

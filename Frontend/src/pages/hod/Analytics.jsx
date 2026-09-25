import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Legend } from 'recharts'
import StatCard from '../../components/StatCard.jsx'

function toChartData(obj) {
  return Object.entries(obj || {}).map(([name, value]) => ({ name, value }))
}

export default function HodAnalytics() {
  const [analytics, setAnalytics] = useState(null)
  const [year, setYear] = useState('')
  const [section, setSection] = useState('')
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    const params = {}
    if (year) params.year = year
    if (section) params.section = section
    api.get('/hod/analytics', { params }).then(({ data }) => setAnalytics(data)).finally(() => setLoading(false))
  }

  useEffect(() => { load() }, []) // eslint-disable-line react-hooks/exhaustive-deps

  const handleFilter = (e) => {
    e.preventDefault()
    load()
  }

  const sectionData = toChartData(analytics?.sectionPerformance)

  return (
    <div className="container-fluid py-4">
      <h4 className="fw-bold mb-4">Department Analytics</h4>

      <form className="row g-2 mb-4" onSubmit={handleFilter}>
        <div className="col-auto">
          <select className="form-select" value={year} onChange={(e) => setYear(e.target.value)}>
            <option value="">All Years</option>
            {[1, 2, 3, 4].map((y) => <option key={y} value={y}>Year {y}</option>)}
          </select>
        </div>
        <div className="col-auto">
          <input className="form-control" placeholder="Section (e.g. A)" value={section} onChange={(e) => setSection(e.target.value)} />
        </div>
        <div className="col-auto">
          <button className="btn btn-primary" type="submit">Apply Filters</button>
        </div>
      </form>

      {loading ? (
        <div className="p-5 text-center text-muted">Loading analytics...</div>
      ) : (
        <>
          <div className="row g-3 mb-4">
            <StatCard label="Students" value={analytics.totalStudents} icon="fa-solid fa-user-graduate" color="primary" />
            <StatCard label="Certificates" value={analytics.totalCertificates} icon="fa-solid fa-file-lines" color="success" />
            <StatCard label="Winning" value={analytics.totalWinning} icon="fa-solid fa-trophy" color="warning" />
            <StatCard label="Participation" value={analytics.totalParticipation} icon="fa-solid fa-people-group" color="secondary" />
          </div>

          <div className="row g-4">
            <div className="col-md-6">
              <div className="card shadow-sm p-3">
                <div className="fw-semibold mb-2">Certificates by Section</div>
                <ResponsiveContainer width="100%" height={280}>
                  <BarChart data={sectionData}>
                    <XAxis dataKey="name" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Legend />
                    <Bar dataKey="value" fill="#4f46e5" />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
            <div className="col-md-6">
              <div className="card shadow-sm">
                <div className="card-header bg-white fw-semibold">Student Standings</div>
                <div className="table-responsive" style={{ maxHeight: 320, overflowY: 'auto' }}>
                  <table className="table table-hover align-middle mb-0">
                    <thead><tr><th>Name</th><th>Section</th><th>Certs</th><th>Winning</th></tr></thead>
                    <tbody>
                      {analytics.studentStats.map((s) => (
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
        </>
      )}
    </div>
  )
}

import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Legend } from 'recharts'

const COLORS = ['#4f46e5', '#22c55e', '#f59e0b', '#ef4444', '#06b6d4', '#a855f7']

function toChartData(obj) {
  return Object.entries(obj || {}).map(([name, value]) => ({ name, value }))
}

export default function StudentAnalytics() {
  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/student/analytics').then(({ data }) => setAnalytics(data)).finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="p-5 text-center text-muted">Loading analytics...</div>
  if (!analytics) return null

  const eventData = toChartData(analytics.eventTypeData)
  const achievementData = toChartData(analytics.achievementData)
  const statusData = toChartData(analytics.statusData)

  return (
    <div className="container-fluid py-4">
      <h4 className="fw-bold mb-4">My Analytics</h4>
      <div className="text-muted mb-4">Total certificates uploaded: <strong>{analytics.totalCertificates}</strong></div>

      <div className="row g-4">
        <div className="col-md-4">
          <div className="card shadow-sm p-3">
            <div className="fw-semibold mb-2">By Event Type</div>
            <ResponsiveContainer width="100%" height={240}>
              <PieChart>
                <Pie data={eventData} dataKey="value" nameKey="name" outerRadius={80} label>
                  {eventData.map((entry, index) => <Cell key={index} fill={COLORS[index % COLORS.length]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card shadow-sm p-3">
            <div className="fw-semibold mb-2">Winning vs Participation</div>
            <ResponsiveContainer width="100%" height={240}>
              <PieChart>
                <Pie data={achievementData} dataKey="value" nameKey="name" outerRadius={80} label>
                  {achievementData.map((entry, index) => <Cell key={index} fill={COLORS[index % COLORS.length]} />)}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card shadow-sm p-3">
            <div className="fw-semibold mb-2">Approval Status</div>
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={statusData}>
                <XAxis dataKey="name" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Legend />
                <Bar dataKey="value" fill="#4f46e5" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  )
}

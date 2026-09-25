import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import ApprovalTable from '../../components/ApprovalTable.jsx'

const TABS = ['pending', 'approved', 'rejected']

export default function StaffApprovals() {
  const [grouped, setGrouped] = useState({ pending: [], approved: [], rejected: [] })
  const [tab, setTab] = useState('pending')
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    api.get('/staff/approvals').then(({ data }) => setGrouped(data)).finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleReview = async (certificateId, status, feedback) => {
    await api.post(`/staff/certificates/${certificateId}/review`, { status, feedback })
    load()
  }

  return (
    <div className="container-fluid py-4">
      <h4 className="fw-bold mb-4">Certificate Approvals</h4>

      <ul className="nav nav-tabs mb-3">
        {TABS.map((t) => (
          <li className="nav-item" key={t}>
            <button className={`nav-link ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>
              <span className="text-capitalize">{t}</span>
              <span className="badge bg-secondary ms-2">{grouped[t]?.length || 0}</span>
            </button>
          </li>
        ))}
      </ul>

      <div className="card shadow-sm">
        {loading ? (
          <div className="p-5 text-center text-muted">Loading...</div>
        ) : (
          <ApprovalTable
            certificates={grouped[tab]}
            statusField="staffStatus"
            onReview={tab === 'pending' ? handleReview : undefined}
          />
        )}
      </div>
    </div>
  )
}

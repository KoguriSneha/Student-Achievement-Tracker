import React, { useState } from 'react'
import StatusBadge from './StatusBadge.jsx'
import { API_BASE_URL } from '../api/axios.js'

export default function ApprovalTable({ certificates, statusField, onReview, showStudent = true }) {
  const [feedback, setFeedback] = useState({})

  const fileUrl = (id) => {
    const token = localStorage.getItem('sat_token')
    return `${API_BASE_URL}/api/certificates/${id}/file?download=false&token=${encodeURIComponent(token || '')}`
  }

  if (!certificates || certificates.length === 0) {
    return <div className="text-muted p-4 text-center">Nothing here right now.</div>
  }

  return (
    <div className="table-responsive">
      <table className="table table-hover align-middle">
        <thead>
          <tr>
            {showStudent && <th>Student</th>}
            <th>Title</th>
            <th>Type</th>
            <th>Status</th>
            <th style={{ minWidth: 240 }}>Feedback</th>
            <th>File</th>
            {onReview && <th>Action</th>}
          </tr>
        </thead>
        <tbody>
          {certificates.map((c) => (
            <tr key={c.id}>
              {showStudent && (
                <td>
                  <div className="fw-semibold">{c.studentName}</div>
                  <div className="text-muted small">{c.studentIdCode}</div>
                </td>
              )}
              <td>
                <div className="fw-semibold">{c.title}</div>
                <div className="text-muted small text-capitalize">{c.achievementType}</div>
              </td>
              <td className="text-capitalize">{c.eventType}</td>
              <td><StatusBadge status={c[statusField]} /></td>
              <td>
                {onReview && c[statusField] === 'pending' ? (
                  <input
                    className="form-control form-control-sm"
                    placeholder="Optional feedback"
                    value={feedback[c.id] || ''}
                    onChange={(e) => setFeedback((f) => ({ ...f, [c.id]: e.target.value }))}
                  />
                ) : (
                  <span className="text-muted small">{c[statusField === 'staffStatus' ? 'staffFeedback' : 'hodFeedback'] || '-'}</span>
                )}
              </td>
              <td>
                <a className="btn btn-sm btn-outline-primary" href={fileUrl(c.id)} target="_blank" rel="noreferrer">
                  <i className="fa-solid fa-eye"></i>
                </a>
              </td>
              {onReview && (
                <td>
                  {c[statusField] === 'pending' ? (
                    <div className="d-flex gap-1">
                      <button
                        className="btn btn-sm btn-success"
                        onClick={() => onReview(c.id, 'approved', feedback[c.id] || '')}
                      >
                        <i className="fa-solid fa-check"></i>
                      </button>
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => onReview(c.id, 'rejected', feedback[c.id] || '')}
                      >
                        <i className="fa-solid fa-xmark"></i>
                      </button>
                    </div>
                  ) : (
                    <span className="text-muted small">Reviewed</span>
                  )}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

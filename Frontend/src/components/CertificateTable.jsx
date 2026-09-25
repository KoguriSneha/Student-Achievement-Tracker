import React from 'react'
import StatusBadge from './StatusBadge.jsx'
import { API_BASE_URL } from '../api/axios.js'

export default function CertificateTable({ certificates }) {
  const fileUrl = (id, download) => {
    const token = localStorage.getItem('sat_token')
    return `${API_BASE_URL}/api/certificates/${id}/file?download=${download}&token=${encodeURIComponent(token || '')}`
  }

  if (!certificates || certificates.length === 0) {
    return <div className="text-muted p-4 text-center">No certificates uploaded yet.</div>
  }

  return (
    <div className="table-responsive">
      <table className="table table-hover align-middle">
        <thead>
          <tr>
            <th>Title</th>
            <th>Event Type</th>
            <th>Achievement</th>
            <th>Uploaded</th>
            <th>Staff Status</th>
            <th>HOD Status</th>
            <th>File</th>
          </tr>
        </thead>
        <tbody>
          {certificates.map((c) => (
            <tr key={c.id}>
              <td>
                <div className="fw-semibold">{c.title}</div>
                {c.description && <div className="text-muted small">{c.description}</div>}
                {c.staffFeedback && <div className="text-muted small">Staff: {c.staffFeedback}</div>}
                {c.hodFeedback && <div className="text-muted small">HOD: {c.hodFeedback}</div>}
              </td>
              <td className="text-capitalize">{c.eventType}</td>
              <td className="text-capitalize">{c.achievementType}</td>
              <td>{c.uploadDate ? new Date(c.uploadDate).toLocaleDateString() : '-'}</td>
              <td><StatusBadge status={c.staffStatus} /></td>
              <td><StatusBadge status={c.hodStatus} /></td>
              <td>
                <a className="btn btn-sm btn-outline-primary me-1" href={fileUrl(c.id, false)} target="_blank" rel="noreferrer">
                  <i className="fa-solid fa-eye"></i>
                </a>
                <a className="btn btn-sm btn-outline-secondary" href={fileUrl(c.id, true)}>
                  <i className="fa-solid fa-download"></i>
                </a>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

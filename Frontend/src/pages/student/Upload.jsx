import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../../api/axios.js'

const EVENT_TYPES = ['workshop', 'hackathon', 'technical', 'cultural']
const ACHIEVEMENT_TYPES = ['winning', 'participation']

export default function StudentUpload() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ title: '', description: '', eventType: '', achievementType: '' })
  const [file, setFile] = useState(null)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const update = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    if (!file) {
      setError('Please attach a PDF or JPEG certificate file')
      return
    }
    const fd = new FormData()
    fd.append('title', form.title)
    fd.append('description', form.description)
    fd.append('eventType', form.eventType)
    fd.append('achievementType', form.achievementType)
    fd.append('file', file)

    setSubmitting(true)
    try {
      await api.post('/student/certificates', fd, { headers: { 'Content-Type': 'multipart/form-data' } })
      navigate('/student/certificates')
    } catch (err) {
      setError(err?.response?.data?.message || 'Upload failed. Please check the form and try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="container py-4" style={{ maxWidth: 640 }}>
      <h4 className="fw-bold mb-4">Upload Certificate</h4>

      {error && <div className="alert alert-danger py-2">{error}</div>}

      <form onSubmit={handleSubmit} className="card shadow-sm p-4">
        <div className="mb-3">
          <label className="form-label">Title</label>
          <input className="form-control" value={form.title} onChange={update('title')} required />
        </div>
        <div className="mb-3">
          <label className="form-label">Description</label>
          <textarea className="form-control" rows={3} value={form.description} onChange={update('description')} />
        </div>
        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label">Event Type</label>
            <select className="form-select" value={form.eventType} onChange={update('eventType')} required>
              <option value="">Select...</option>
              {EVENT_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
          <div className="col-md-6 mb-3">
            <label className="form-label">Achievement Type</label>
            <select className="form-select" value={form.achievementType} onChange={update('achievementType')} required>
              <option value="">Select...</option>
              {ACHIEVEMENT_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
        </div>
        <div className="mb-4">
          <label className="form-label">Certificate File (PDF or JPEG)</label>
          <input type="file" className="form-control" accept=".pdf,.jpg,.jpeg" onChange={(e) => setFile(e.target.files[0])} required />
        </div>
        <button className="btn btn-primary" type="submit" disabled={submitting}>
          {submitting ? 'Uploading...' : 'Submit for Approval'}
        </button>
      </form>
    </div>
  )
}

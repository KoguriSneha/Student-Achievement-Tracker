import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api/axios.js'

const ROLES = [
  { value: 'student', label: 'Student' },
  { value: 'staff', label: 'Staff (Class Teacher)' },
  { value: 'hod', label: 'HOD' },
]

const EMPTY_FORM = {
  username: '',
  password: '',
  email: '',
  name: '',
  studentId: '',
  staffId: '',
  hodId: '',
  branch: '',
  section: '',
  year: '',
}

export default function Register() {
  const navigate = useNavigate()
  const [role, setRole] = useState('student')
  const [form, setForm] = useState(EMPTY_FORM)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const update = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }))

  const handleRoleChange = (newRole) => {
    setRole(newRole)
    setError('')
  }

  const buildPayload = () => {
    const base = {
      username: form.username,
      password: form.password,
      email: form.email,
      name: form.name,
    }
    if (role === 'student') {
      return { ...base, studentId: form.studentId, branch: form.branch, section: form.section, year: Number(form.year) }
    }
    if (role === 'staff') {
      return { ...base, staffId: form.staffId, branch: form.branch, section: form.section, year: form.year ? Number(form.year) : null }
    }
    return { ...base, hodId: form.hodId, branch: form.branch }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setSubmitting(true)
    try {
      await api.post(`/auth/register/${role}`, buildPayload())
      setSuccess('Account created! You can now log in.')
      setForm(EMPTY_FORM)
      setTimeout(() => navigate('/login'), 1500)
    } catch (err) {
      setError(err?.response?.data?.message || 'Registration failed. Please check the form and try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="login-page d-flex align-items-center justify-content-center py-5">
      <div className="login-card shadow" style={{ maxWidth: 460 }}>
        <div className="text-center mb-4">
          <i className="fa-solid fa-user-plus fa-2x text-primary mb-2"></i>
          <h4 className="fw-bold mb-0">Create an Account</h4>
          <div className="text-muted small">Sign up for the Achievement Tracker</div>
        </div>

        {error && <div className="alert alert-danger py-2">{error}</div>}
        {success && <div className="alert alert-success py-2">{success}</div>}

        <div className="mb-3">
          <label className="form-label">I am a</label>
          <div className="btn-group w-100" role="group">
            {ROLES.map((r) => (
              <button
                type="button"
                key={r.value}
                className={`btn btn-sm ${role === r.value ? 'btn-primary' : 'btn-outline-primary'}`}
                onClick={() => handleRoleChange(r.value)}
              >
                {r.label}
              </button>
            ))}
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Full Name</label>
            <input className="form-control" value={form.name} onChange={update('name')} required />
          </div>

          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Username</label>
              <input className="form-control" value={form.username} onChange={update('username')} required />
            </div>
            <div className="col-md-6 mb-3">
              <label className="form-label">Password</label>
              <input type="password" className="form-control" value={form.password} onChange={update('password')} required minLength={4} />
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label">Email</label>
            <input type="email" className="form-control" value={form.email} onChange={update('email')} required />
          </div>

          {role === 'student' && (
            <>
              <div className="mb-3">
                <label className="form-label">Student ID</label>
                <input className="form-control" value={form.studentId} onChange={update('studentId')} required />
              </div>
              <div className="row">
                <div className="col-md-4 mb-3">
                  <label className="form-label">Branch</label>
                  <input className="form-control" value={form.branch} onChange={update('branch')} placeholder="CSE" required />
                </div>
                <div className="col-md-4 mb-3">
                  <label className="form-label">Section</label>
                  <input className="form-control" value={form.section} onChange={update('section')} placeholder="A" required />
                </div>
                <div className="col-md-4 mb-3">
                  <label className="form-label">Year</label>
                  <select className="form-select" value={form.year} onChange={update('year')} required>
                    <option value="">-</option>
                    {[1, 2, 3, 4].map((y) => <option key={y} value={y}>{y}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-text mb-3">
                Tip: use Branch <code>CSE</code>, Section <code>A</code>, Year <code>2</code> to be assigned to the
                seeded demo class teacher (<code>staff_cse</code>) automatically.
              </div>
            </>
          )}

          {role === 'staff' && (
            <>
              <div className="mb-3">
                <label className="form-label">Staff ID</label>
                <input className="form-control" value={form.staffId} onChange={update('staffId')} required />
              </div>
              <div className="row">
                <div className="col-md-4 mb-3">
                  <label className="form-label">Branch</label>
                  <input className="form-control" value={form.branch} onChange={update('branch')} placeholder="CSE" required />
                </div>
                <div className="col-md-4 mb-3">
                  <label className="form-label">Section</label>
                  <input className="form-control" value={form.section} onChange={update('section')} placeholder="A" required />
                </div>
                <div className="col-md-4 mb-3">
                  <label className="form-label">Year (optional)</label>
                  <select className="form-select" value={form.year} onChange={update('year')}>
                    <option value="">-</option>
                    {[1, 2, 3, 4].map((y) => <option key={y} value={y}>{y}</option>)}
                  </select>
                </div>
              </div>
            </>
          )}

          {role === 'hod' && (
            <>
              <div className="mb-3">
                <label className="form-label">HOD ID</label>
                <input className="form-control" value={form.hodId} onChange={update('hodId')} required />
              </div>
              <div className="mb-3">
                <label className="form-label">Branch</label>
                <input className="form-control" value={form.branch} onChange={update('branch')} placeholder="CSE" required />
              </div>
            </>
          )}

          <button className="btn btn-primary w-100 mt-2" type="submit" disabled={submitting}>
            {submitting ? 'Creating account...' : 'Create Account'}
          </button>
        </form>

        <div className="mt-3 text-center small">
          Already have an account? <Link to="/login">Log in</Link>
        </div>
      </div>
    </div>
  )
}

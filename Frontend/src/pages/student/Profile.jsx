import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import ChangePasswordForm from '../../components/ChangePasswordForm.jsx'

export default function StudentProfile() {
  const [student, setStudent] = useState(null)

  useEffect(() => {
    api.get('/student/profile').then(({ data }) => setStudent(data))
  }, [])

  if (!student) return <div className="p-5 text-center text-muted">Loading...</div>

  return (
    <div className="container py-4" style={{ maxWidth: 640 }}>
      <h4 className="fw-bold mb-4">My Profile</h4>
      <div className="card shadow-sm p-4 mb-4">
        <dl className="row mb-0">
          <dt className="col-sm-4">Name</dt><dd className="col-sm-8">{student.name}</dd>
          <dt className="col-sm-4">Student ID</dt><dd className="col-sm-8">{student.studentId}</dd>
          <dt className="col-sm-4">Branch</dt><dd className="col-sm-8">{student.branch}</dd>
          <dt className="col-sm-4">Section</dt><dd className="col-sm-8">{student.section}</dd>
          <dt className="col-sm-4">Year</dt><dd className="col-sm-8">{student.year}</dd>
          <dt className="col-sm-4">Class Teacher</dt><dd className="col-sm-8">{student.classTeacherName || 'Not assigned'}</dd>
          <dt className="col-sm-4">Email</dt><dd className="col-sm-8">{student.email}</dd>
          <dt className="col-sm-4">Username</dt><dd className="col-sm-8">{student.username}</dd>
        </dl>
      </div>
      <ChangePasswordForm />
    </div>
  )
}

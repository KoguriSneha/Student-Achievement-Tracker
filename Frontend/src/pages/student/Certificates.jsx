import React, { useEffect, useState } from 'react'
import api from '../../api/axios.js'
import CertificateTable from '../../components/CertificateTable.jsx'

export default function StudentCertificates() {
  const [certificates, setCertificates] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/student/certificates').then(({ data }) => setCertificates(data)).finally(() => setLoading(false))
  }, [])

  return (
    <div className="container-fluid py-4">
      <h4 className="fw-bold mb-4">My Certificates</h4>
      <div className="card shadow-sm">
        {loading ? <div className="p-5 text-center text-muted">Loading...</div> : <CertificateTable certificates={certificates} />}
      </div>
    </div>
  )
}

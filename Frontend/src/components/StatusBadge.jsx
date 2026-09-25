import React from 'react'

const COLORS = {
  approved: 'success',
  pending: 'warning',
  rejected: 'danger',
}

export default function StatusBadge({ status }) {
  const color = COLORS[status] || 'secondary'
  return <span className={`badge bg-${color} text-uppercase`}>{status}</span>
}

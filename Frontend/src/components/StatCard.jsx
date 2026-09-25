import React from 'react'

export default function StatCard({ label, value, icon, color = 'primary' }) {
  return (
    <div className="col-6 col-md-3">
      <div className={`stat-card border-${color}`}>
        <div className={`stat-icon text-${color}`}>
          <i className={icon}></i>
        </div>
        <div>
          <div className="stat-value">{value}</div>
          <div className="stat-label">{label}</div>
        </div>
      </div>
    </div>
  )
}

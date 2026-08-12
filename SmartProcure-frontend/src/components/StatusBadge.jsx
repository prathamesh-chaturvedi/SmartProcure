import React from 'react';
import { STATUS_BADGES } from '../utils/constants';

const StatusBadge = ({ status }) => {
  const badgeConfig = STATUS_BADGES[status] || {
    label: status || 'Unknown',
    bgClass: 'bg-secondary',
  };

  return (
    <span className={`badge ${badgeConfig.bgClass} px-2.5 py-1.5 fw-medium`}>
      {badgeConfig.label}
    </span>
  );
};

export default StatusBadge;

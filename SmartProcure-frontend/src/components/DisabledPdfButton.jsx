import React from 'react';

const DisabledPdfButton = ({ label = 'Download PDF', className = 'btn btn-outline-secondary btn-sm' }) => {
  return (
    <button
      className={`${className} opacity-50`}
      disabled
      title="PDF generation is currently disabled"
      type="button"
    >
      <i className="bi bi-file-earmark-pdf me-1"></i>
      {label}
    </button>
  );
};

export default DisabledPdfButton;

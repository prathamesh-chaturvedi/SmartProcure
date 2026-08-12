import React from 'react';
import { Link } from 'react-router-dom';

const UnauthorizedPage = () => {
  return (
    <div className="d-flex flex-column align-items-center justify-content-center py-5 text-center">
      <div
        className="rounded-circle bg-danger bg-opacity-10 text-danger d-flex align-items-center justify-content-center mb-3"
        style={{ width: '80px', height: '80px' }}
      >
        <i className="bi bi-shield-lock fs-1"></i>
      </div>
      <h3 className="fw-bold text-slate-800">403 - Access Denied</h3>
      <p className="text-secondary max-w-md mb-4">
        You do not have permission to view this page or perform this action with your current role.
      </p>
      <Link to="/dashboard" className="btn btn-sp-primary px-4 fw-medium">
        Back to Dashboard
      </Link>
    </div>
  );
};

export default UnauthorizedPage;

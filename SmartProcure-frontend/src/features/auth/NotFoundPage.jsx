import React from 'react';
import { Link } from 'react-router-dom';

const NotFoundPage = () => {
  return (
    <div className="d-flex flex-column align-items-center justify-content-center py-5 text-center">
      <div
        className="rounded-circle bg-secondary bg-opacity-10 text-secondary d-flex align-items-center justify-content-center mb-3"
        style={{ width: '80px', height: '80px' }}
      >
        <i className="bi bi-compass fs-1"></i>
      </div>
      <h3 className="fw-bold text-slate-800">404 - Page Not Found</h3>
      <p className="text-secondary max-w-md mb-4">
        The page you are looking for does not exist or has been moved.
      </p>
      <Link to="/dashboard" className="btn btn-sp-primary px-4 fw-medium">
        Back to Dashboard
      </Link>
    </div>
  );
};

export default NotFoundPage;

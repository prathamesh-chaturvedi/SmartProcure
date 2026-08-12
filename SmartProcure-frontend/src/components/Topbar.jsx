import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import { formatRole } from '../utils/formatters';

const Topbar = ({ onToggleSidebar }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sp-topbar">
      <div className="d-flex align-items-center">
        <button
          className="btn btn-light d-lg-none me-2 px-2 py-1 border"
          onClick={onToggleSidebar}
          aria-label="Toggle Sidebar"
          type="button"
        >
          <i className="bi bi-list fs-5"></i>
        </button>
        <span className="sp-topbar-title">SmartProcure Management Portal</span>
      </div>

      <div className="d-flex align-items-center">
        <div className="dropdown">
          <button
            className="btn btn-light dropdown-toggle d-flex align-items-center border px-3 py-1.5"
            type="button"
            id="userDropdown"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            <div
              className="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center me-2 font-monospace fw-bold"
              style={{ width: '32px', height: '32px', fontSize: '0.85rem' }}
            >
              {user?.firstName ? user.firstName.charAt(0).toUpperCase() : 'U'}
            </div>
            <div className="text-start d-none d-sm-block me-2">
              <div className="fw-semibold lh-1 text-slate-800" style={{ fontSize: '0.875rem' }}>
                {user?.firstName || 'User'}
              </div>
              <small className="text-muted" style={{ fontSize: '0.725rem' }}>
                {formatRole(user?.userRole)}
              </small>
            </div>
          </button>
          <ul className="dropdown-menu dropdown-menu-end shadow-sm border-0 mt-2" aria-labelledby="userDropdown">
            <li>
              <div className="dropdown-header px-3 py-2">
                <div className="fw-bold">{user?.firstName}</div>
                <div className="text-muted small">{formatRole(user?.userRole)}</div>
              </div>
            </li>
            <li><hr className="dropdown-divider my-1" /></li>
            <li>
              <Link className="dropdown-item py-2" to="/profile">
                <i className="bi bi-person me-2"></i> My Profile
              </Link>
            </li>
            <li>
              <Link className="dropdown-item py-2" to="/change-password">
                <i className="bi bi-key me-2"></i> Change Password
              </Link>
            </li>
            <li><hr className="dropdown-divider my-1" /></li>
            <li>
              <button className="dropdown-item py-2 text-danger" onClick={handleLogout} type="button">
                <i className="bi bi-box-arrow-right me-2"></i> Logout
              </button>
            </li>
          </ul>
        </div>
      </div>
    </header>
  );
};

export default Topbar;

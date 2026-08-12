import React from 'react';
import { NavLink } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import { ROLES } from '../utils/constants';

const Sidebar = ({ isOpen, onCloseMobile }) => {
  const { user } = useAuth();
  const role = user?.userRole;

  const isMasterAdmin = role === ROLES.MASTER_ADMIN;
  const isAdmin = role === ROLES.ADMIN;
  const isManager = role === ROLES.MANAGER;
  const isEmployee = role === ROLES.EMPLOYEE;

  return (
    <aside className={`sp-sidebar ${isOpen ? 'show' : ''}`}>
      <NavLink to="/dashboard" className="sp-sidebar-brand" onClick={onCloseMobile}>
        <i className="bi bi-shield-check text-white me-2"></i>
        <span>SmartProcure</span>
      </NavLink>

      <div className="sp-sidebar-menu">
        <div className="sp-nav-section-title">Main</div>
        
        <NavLink
          to="/dashboard"
          className={({ isActive }) => `sp-nav-item ${isActive ? 'active' : ''}`}
          onClick={onCloseMobile}
        >
          <i className="bi bi-speedometer2"></i>
          <span>Dashboard</span>
        </NavLink>

        {/* Procurement Cases - Executive, Manager, Admin, Master Admin */}
        <NavLink
          to="/procurement-cases"
          className={({ isActive }) => `sp-nav-item ${isActive ? 'active' : ''}`}
          onClick={onCloseMobile}
        >
          <i className="bi bi-box-seam"></i>
          <span>Procurement Cases</span>
        </NavLink>

        {/* Pending Approvals Inbox - Manager, Master Admin */}
        {(isManager || isMasterAdmin) && (
          <NavLink
            to="/pending-approvals"
            className={({ isActive }) => `sp-nav-item ${isActive ? 'active' : ''}`}
            onClick={onCloseMobile}
          >
            <i className="bi bi-check2-square"></i>
            <span>Pending Approvals</span>
          </NavLink>
        )}

        {/* Administration Section */}
        {(isAdmin || isMasterAdmin) && (
          <>
            <div className="sp-nav-section-title mt-3">Administration</div>

            {/* User Management - Admin, Master Admin */}
            <NavLink
              to="/users"
              className={({ isActive }) => `sp-nav-item ${isActive ? 'active' : ''}`}
              onClick={onCloseMobile}
            >
              <i className="bi bi-people"></i>
              <span>User Management</span>
            </NavLink>

            {/* Approval Matrix - Admin, Master Admin */}
            <NavLink
              to="/approval-matrix"
              className={({ isActive }) => `sp-nav-item ${isActive ? 'active' : ''}`}
              onClick={onCloseMobile}
            >
              <i className="bi bi-diagram-3"></i>
              <span>Approval Matrix</span>
            </NavLink>
          </>
        )}

        {/* Platform Level Tenant Management - Master Admin only */}
        {isMasterAdmin && (
          <>
            <div className="sp-nav-section-title mt-3">Platform</div>
            <NavLink
              to="/companies"
              className={({ isActive }) => `sp-nav-item ${isActive ? 'active' : ''}`}
              onClick={onCloseMobile}
            >
              <i className="bi bi-building"></i>
              <span>Tenant Companies</span>
            </NavLink>
          </>
        )}

        <div className="sp-nav-section-title mt-3">Account</div>
        <NavLink
          to="/profile"
          className={({ isActive }) => `sp-nav-item ${isActive ? 'active' : ''}`}
          onClick={onCloseMobile}
        >
          <i className="bi bi-person-circle"></i>
          <span>My Profile</span>
        </NavLink>
        <NavLink
          to="/change-password"
          className={({ isActive }) => `sp-nav-item ${isActive ? 'active' : ''}`}
          onClick={onCloseMobile}
        >
          <i className="bi bi-key"></i>
          <span>Change Password</span>
        </NavLink>
      </div>
    </aside>
  );
};

export default Sidebar;

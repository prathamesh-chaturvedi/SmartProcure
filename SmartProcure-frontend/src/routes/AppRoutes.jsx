import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import RoleGuard from './RoleGuard';
import MainLayout from '../components/MainLayout';

// Auth & System Pages
import LoginPage from '../features/auth/LoginPage';
import UnauthorizedPage from '../features/auth/UnauthorizedPage';
import NotFoundPage from '../features/auth/NotFoundPage';

// Business Modules
import DashboardPage from '../features/dashboard/DashboardPage';
import ProcurementListPage from '../features/procurementCases/ProcurementListPage';
import ProcurementDetailPage from '../features/procurementCases/ProcurementDetailPage';
import PendingApprovalsPage from '../features/approvals/PendingApprovalsPage';
import UserListPage from '../features/users/UserListPage';
import ApprovalMatrixPage from '../features/approvalMatrix/ApprovalMatrixPage';
import CompanyListPage from '../features/companies/CompanyListPage';
import ProfilePage from '../features/profile/ProfilePage';
import ChangePasswordPage from '../features/profile/ChangePasswordPage';

import { ROLES } from '../utils/constants';

const AppRoutes = () => {
  return (
    <Routes>
      {/* Public Route */}
      <Route path="/login" element={<LoginPage />} />

      {/* Protected Layout Routes */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />

        {/* Procurement Cases */}
        <Route path="procurement-cases" element={<ProcurementListPage />} />
        <Route path="procurement-cases/:csId" element={<ProcurementDetailPage />} />

        {/* Approver Pending Inbox - Managers & Master Admins */}
        <Route
          path="pending-approvals"
          element={
            <RoleGuard allowedRoles={[ROLES.MANAGER, ROLES.MASTER_ADMIN]}>
              <PendingApprovalsPage />
            </RoleGuard>
          }
        />

        {/* Administration - Admins & Master Admins */}
        <Route
          path="users"
          element={
            <RoleGuard allowedRoles={[ROLES.ADMIN, ROLES.MASTER_ADMIN]}>
              <UserListPage />
            </RoleGuard>
          }
        />
        <Route
          path="approval-matrix"
          element={
            <RoleGuard allowedRoles={[ROLES.ADMIN, ROLES.MASTER_ADMIN]}>
              <ApprovalMatrixPage />
            </RoleGuard>
          }
        />

        {/* Platform Level Tenant Management - Master Admin only */}
        <Route
          path="companies"
          element={
            <RoleGuard allowedRoles={[ROLES.MASTER_ADMIN]}>
              <CompanyListPage />
            </RoleGuard>
          }
        />

        {/* User Account */}
        <Route path="profile" element={<ProfilePage />} />
        <Route path="change-password" element={<ChangePasswordPage />} />

        {/* System Error Pages */}
        <Route path="unauthorized" element={<UnauthorizedPage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;

import React, { useEffect, useState } from 'react';
import useAuth from '../../hooks/useAuth';
import { userApi } from '../../api/userApi';
import Pagination from '../../components/Pagination';
import ConfirmModal from '../../components/ConfirmModal';
import UserFormModal from './UserFormModal';
import { formatRole, formatDesignation, formatDate } from '../../utils/formatters';
import { ROLES, ROLE_LABELS, DESIGNATIONS } from '../../utils/constants';

const UserListPage = () => {
  const { isMasterAdmin, isAdmin } = useAuth();

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);

  // Filters
  const [searchName, setSearchName] = useState('');
  const [roleFilter, setRoleFilter] = useState('');

  // Modals
  const [showModal, setShowModal] = useState(false);
  const [editUser, setEditUser] = useState(null);
  const [deactivateTarget, setDeactivateTarget] = useState(null);
  const [deactivateLoading, setDeactivateLoading] = useState(false);

  useEffect(() => {
    fetchUsers();
  }, [page, roleFilter]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: pageSize,
      };
      if (searchName) params.name = searchName;
      if (roleFilter) params.userRole = roleFilter;

      const res = await userApi.getUsers(params);
      setUsers(res.data.content || []);
      setTotalPages(res.data.totalPages || 1);
    } catch (err) {
      console.error('Error loading users:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchUsers();
  };

  const handleDeactivateConfirm = async () => {
    if (!deactivateTarget) return;
    setDeactivateLoading(true);
    try {
      await userApi.deleteUser(deactivateTarget.userId);
      setDeactivateTarget(null);
      fetchUsers();
    } catch (err) {
      console.error('Deactivate user error:', err);
      alert(err.response?.data?.message || 'Could not deactivate user.');
    } finally {
      setDeactivateLoading(false);
    }
  };

  return (
    <div>
      {/* Page Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4">
        <div>
          <h4 className="fw-bold text-slate-900 mb-1">User Management</h4>
          <p className="text-secondary small mb-0">
            Manage company users, assign role-based permissions, and designation levels.
          </p>
        </div>
        {(isAdmin || isMasterAdmin) && (
          <div className="mt-3 mt-md-0">
            <button
              className="btn btn-sp-primary fw-medium"
              onClick={() => {
                setEditUser(null);
                setShowModal(true);
              }}
            >
              <i className="bi bi-person-plus me-1"></i> Add User
            </button>
          </div>
        )}
      </div>

      {/* Filter Card */}
      <div className="sp-card p-3 mb-4">
        <form onSubmit={handleSearchSubmit} className="row g-2 align-items-center">
          <div className="col-12 col-md-5">
            <input
              type="text"
              className="form-control form-control-sm"
              placeholder="Search by User Name..."
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
            />
          </div>

          <div className="col-12 col-md-4">
            <select
              className="form-select form-select-sm"
              value={roleFilter}
              onChange={(e) => {
                setRoleFilter(e.target.value);
                setPage(0);
              }}
            >
              <option value="">All System Roles</option>
              {Object.keys(ROLES).map((r) => (
                <option key={r} value={r}>
                  {ROLE_LABELS[r]}
                </option>
              ))}
            </select>
          </div>

          <div className="col-12 col-md-3 d-flex gap-2">
            <button type="submit" className="btn btn-sm btn-sp-primary flex-grow-1 fw-medium">
              <i className="bi bi-search me-1"></i> Search
            </button>
            <button
              type="button"
              className="btn btn-sm btn-outline-secondary"
              onClick={() => {
                setSearchName('');
                setRoleFilter('');
                setPage(0);
              }}
            >
              Reset
            </button>
          </div>
        </form>
      </div>

      {/* Main Users Table */}
      <div className="sp-card">
        <div className="sp-card-body p-0">
          {loading ? (
            <div className="p-5 text-center text-muted">
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Loading user directory...
            </div>
          ) : users.length === 0 ? (
            <div className="p-5 text-center text-muted">
              <i className="bi bi-person-x fs-1 d-block text-secondary mb-2"></i>
              No users found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table sp-table mb-0 align-middle">
                <thead>
                  <tr>
                    <th>User ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Designation</th>
                    <th>Company</th>
                    <th>Status</th>
                    <th className="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.userId}>
                      <td className="font-monospace text-muted">#{u.userId}</td>
                      <td className="fw-bold text-dark">
                        {u.firstName} {u.lastName}
                      </td>
                      <td>{u.email}</td>
                      <td>
                        <span className="badge bg-light text-dark border">
                          {formatRole(u.userRole)}
                        </span>
                      </td>
                      <td className="small text-secondary">{formatDesignation(u.designation)}</td>
                      <td className="small text-secondary">{u.companyName || 'N/A'}</td>
                      <td>
                        <span
                          className={`badge ${
                            u.active ? 'bg-success' : 'bg-secondary'
                          } px-2 py-1`}
                        >
                          {u.active ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="text-end">
                        <div className="btn-group btn-group-sm">
                          <button
                            className="btn btn-light border text-secondary me-1"
                            onClick={() => {
                              setEditUser(u);
                              setShowModal(true);
                            }}
                            title="Edit User"
                          >
                            <i className="bi bi-pencil"></i>
                          </button>
                          {u.active && (
                            <button
                              className="btn btn-light border text-danger"
                              onClick={() => setDeactivateTarget(u)}
                              title="Deactivate User"
                            >
                              <i className="bi bi-person-x"></i>
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Pagination */}
      <Pagination page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)} />

      {/* Add / Edit User Modal */}
      <UserFormModal
        show={showModal}
        editUser={editUser}
        onClose={() => setShowModal(false)}
        onSuccess={() => {
          setShowModal(false);
          fetchUsers();
        }}
      />

      {/* Deactivate Confirm Modal */}
      <ConfirmModal
        show={!!deactivateTarget}
        title="Deactivate User Account"
        message={`Are you sure you want to deactivate account for "${deactivateTarget?.firstName} ${deactivateTarget?.lastName}" (${deactivateTarget?.email})?`}
        confirmText="Deactivate"
        confirmVariant="danger"
        loading={deactivateLoading}
        onConfirm={handleDeactivateConfirm}
        onCancel={() => setDeactivateTarget(null)}
      />
    </div>
  );
};

export default UserListPage;

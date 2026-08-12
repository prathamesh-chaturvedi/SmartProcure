import React, { useState, useEffect } from 'react';
import { userApi } from '../../api/userApi';
import { ROLES, ROLE_LABELS, DESIGNATIONS, DESIGNATION_LABELS } from '../../utils/constants';

const UserFormModal = ({ show, editUser = null, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    dob: '',
    email: '',
    password: '',
    userRole: ROLES.EMPLOYEE,
    designation: DESIGNATIONS.PROCUREMENT_EXECUTIVE,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (editUser) {
      setFormData({
        firstName: editUser.firstName || '',
        lastName: editUser.lastName || '',
        dob: editUser.dob || '',
        email: editUser.email || '',
        password: '', // password updated separately or ignored on edit
        userRole: editUser.userRole || ROLES.EMPLOYEE,
        designation: editUser.designation || DESIGNATIONS.PROCUREMENT_EXECUTIVE,
      });
    } else {
      setFormData({
        firstName: '',
        lastName: '',
        dob: '',
        email: '',
        password: '',
        userRole: ROLES.EMPLOYEE,
        designation: DESIGNATIONS.PROCUREMENT_EXECUTIVE,
      });
    }
    setError(null);
  }, [editUser, show]);

  if (!show) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      if (editUser) {
        // Update user endpoint
        const updatePayload = {
          firstName: formData.firstName,
          lastName: formData.lastName,
          dob: formData.dob,
          email: formData.email,
        };
        await userApi.updateUser(editUser.userId, updatePayload);
      } else {
        // Add user endpoint
        await userApi.addUser(formData);
      }
      onSuccess();
    } catch (err) {
      console.error('User submit error:', err);
      const errMsg = err.response?.data?.message || err.response?.data || 'Failed to save user.';
      setError(typeof errMsg === 'string' ? errMsg : 'Operation failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal fade show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }}>
      <div className="modal-dialog modal-dialog-centered modal-lg">
        <div className="modal-content border-0 shadow">
          <div className="modal-header border-bottom">
            <h5 className="modal-title fw-bold text-slate-800">
              {editUser ? 'Edit User Profile' : 'Add New User'}
            </h5>
            <button type="button" className="btn-close" onClick={onClose} disabled={loading}></button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="modal-body p-4">
              {error && (
                <div className="alert alert-danger py-2 px-3 small mb-3">
                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                  {error}
                </div>
              )}

              <div className="row g-3">
                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">First Name *</label>
                  <input
                    type="text"
                    name="firstName"
                    className="form-control"
                    placeholder="e.g. John"
                    value={formData.firstName}
                    onChange={handleChange}
                    maxLength={50}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Last Name *</label>
                  <input
                    type="text"
                    name="lastName"
                    className="form-control"
                    placeholder="e.g. Doe"
                    value={formData.lastName}
                    onChange={handleChange}
                    maxLength={50}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Email Address *</label>
                  <input
                    type="email"
                    name="email"
                    className="form-control"
                    placeholder="name@company.com"
                    value={formData.email}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Date of Birth *</label>
                  <input
                    type="date"
                    name="dob"
                    className="form-control"
                    value={formData.dob}
                    onChange={handleChange}
                    required
                  />
                </div>

                {!editUser && (
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Initial Password *</label>
                    <input
                      type="password"
                      name="password"
                      className="form-control"
                      placeholder="Min 8 characters"
                      value={formData.password}
                      onChange={handleChange}
                      minLength={8}
                      maxLength={30}
                      required
                    />
                  </div>
                )}

                {!editUser && (
                  <>
                    <div className="col-12 col-md-6">
                      <label className="form-label fw-medium text-secondary small">System Role *</label>
                      <select
                        name="userRole"
                        className="form-select"
                        value={formData.userRole}
                        onChange={handleChange}
                        required
                      >
                        {Object.keys(ROLES).map((r) => (
                          <option key={r} value={r}>
                            {ROLE_LABELS[r]} ({r})
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label fw-medium text-secondary small">Designation *</label>
                      <select
                        name="designation"
                        className="form-select"
                        value={formData.designation}
                        onChange={handleChange}
                        required
                      >
                        {Object.keys(DESIGNATIONS).map((d) => (
                          <option key={d} value={d}>
                            {DESIGNATION_LABELS[d]}
                          </option>
                        ))}
                      </select>
                    </div>
                  </>
                )}
              </div>
            </div>

            <div className="modal-footer border-top bg-light">
              <button
                type="button"
                className="btn btn-light text-secondary fw-medium"
                onClick={onClose}
                disabled={loading}
              >
                Cancel
              </button>
              <button type="submit" className="btn btn-sp-primary fw-medium px-4" disabled={loading}>
                {loading ? 'Saving...' : editUser ? 'Update Profile' : 'Add User'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default UserFormModal;

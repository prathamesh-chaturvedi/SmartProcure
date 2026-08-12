import React, { useState, useEffect } from 'react';
import { companyApi } from '../../api/companyApi';
import { ROLES, DESIGNATIONS } from '../../utils/constants';

const CompanyFormModal = ({ show, editCompany = null, isAdminMode = false, onClose, onSuccess }) => {
  // Mode 1: Create/Edit Company
  const [companyForm, setCompanyForm] = useState({
    companyName: '',
    address: '',
    email: '',
    phone: '',
  });

  // Mode 2: Create Admin for existing company
  const [adminForm, setAdminForm] = useState({
    firstName: '',
    lastName: '',
    dob: '',
    email: '',
    password: '',
    userRole: ROLES.ADMIN,
    designation: DESIGNATIONS.SYSTEM_ADMINISTRATOR,
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (editCompany && !isAdminMode) {
      setCompanyForm({
        companyName: editCompany.companyName || '',
        address: editCompany.address || '',
        email: editCompany.email || '',
        phone: editCompany.phone || '',
      });
    } else {
      setCompanyForm({
        companyName: '',
        address: '',
        email: '',
        phone: '',
      });
      setAdminForm({
        firstName: '',
        lastName: '',
        dob: '',
        email: '',
        password: '',
        userRole: ROLES.ADMIN,
        designation: DESIGNATIONS.SYSTEM_ADMINISTRATOR,
      });
    }
    setError(null);
  }, [editCompany, isAdminMode, show]);

  if (!show) return null;

  const handleCompanyChange = (e) => {
    const { name, value } = e.target;
    setCompanyForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleAdminChange = (e) => {
    const { name, value } = e.target;
    setAdminForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      if (isAdminMode) {
        await companyApi.createCompanyAdmin(editCompany.companyId, adminForm);
      } else if (editCompany) {
        await companyApi.updateCompany(editCompany.companyId, companyForm);
      } else {
        await companyApi.addCompany(companyForm);
      }
      onSuccess();
    } catch (err) {
      console.error('Company form submit error:', err);
      const errMsg = err.response?.data?.message || err.response?.data || 'Operation failed.';
      setError(typeof errMsg === 'string' ? errMsg : 'Save failed');
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
              {isAdminMode
                ? `Create Admin for ${editCompany?.companyName}`
                : editCompany
                ? 'Edit Tenant Company'
                : 'Add Tenant Company'}
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

              {isAdminMode ? (
                /* Admin User Creation Form */
                <div className="row g-3">
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Admin First Name *</label>
                    <input
                      type="text"
                      name="firstName"
                      className="form-control"
                      value={adminForm.firstName}
                      onChange={handleAdminChange}
                      required
                    />
                  </div>
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Admin Last Name *</label>
                    <input
                      type="text"
                      name="lastName"
                      className="form-control"
                      value={adminForm.lastName}
                      onChange={handleAdminChange}
                      required
                    />
                  </div>
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Admin Email *</label>
                    <input
                      type="email"
                      name="email"
                      className="form-control"
                      value={adminForm.email}
                      onChange={handleAdminChange}
                      required
                    />
                  </div>
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Date of Birth *</label>
                    <input
                      type="date"
                      name="dob"
                      className="form-control"
                      value={adminForm.dob}
                      onChange={handleAdminChange}
                      required
                    />
                  </div>
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Password *</label>
                    <input
                      type="password"
                      name="password"
                      className="form-control"
                      value={adminForm.password}
                      onChange={handleAdminChange}
                      minLength={8}
                      required
                    />
                  </div>
                </div>
              ) : (
                /* Company Details Form */
                <div className="row g-3">
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Company Name *</label>
                    <input
                      type="text"
                      name="companyName"
                      className="form-control"
                      placeholder="e.g. Acme Enterprise Pvt Ltd"
                      value={companyForm.companyName}
                      onChange={handleCompanyChange}
                      maxLength={50}
                      required
                    />
                  </div>
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Official Email *</label>
                    <input
                      type="email"
                      name="email"
                      className="form-control"
                      placeholder="contact@company.com"
                      value={companyForm.email}
                      onChange={handleCompanyChange}
                      required
                    />
                  </div>
                  <div className="col-12 col-md-6">
                    <label className="form-label fw-medium text-secondary small">Phone Number *</label>
                    <input
                      type="text"
                      name="phone"
                      className="form-control"
                      placeholder="10 to 14 digits"
                      value={companyForm.phone}
                      onChange={handleCompanyChange}
                      required
                    />
                  </div>
                  <div className="col-12">
                    <label className="form-label fw-medium text-secondary small">Company Address *</label>
                    <textarea
                      name="address"
                      className="form-control"
                      rows="2"
                      placeholder="Headquarters address..."
                      value={companyForm.address}
                      onChange={handleCompanyChange}
                      maxLength={255}
                      required
                    ></textarea>
                  </div>
                </div>
              )}
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
                {loading
                  ? 'Processing...'
                  : isAdminMode
                  ? 'Create Admin'
                  : editCompany
                  ? 'Update Company'
                  : 'Add Company'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CompanyFormModal;

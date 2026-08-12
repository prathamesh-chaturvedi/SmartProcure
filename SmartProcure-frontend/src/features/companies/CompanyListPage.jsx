import React, { useEffect, useState } from 'react';
import { companyApi } from '../../api/companyApi';
import Pagination from '../../components/Pagination';
import ConfirmModal from '../../components/ConfirmModal';
import CompanyFormModal from './CompanyFormModal';

const CompanyListPage = () => {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);

  // Search filter
  const [searchName, setSearchName] = useState('');

  // Modals
  const [showModal, setShowModal] = useState(false);
  const [editCompany, setEditCompany] = useState(null);
  const [isAdminMode, setIsAdminMode] = useState(false);

  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  useEffect(() => {
    fetchCompanies();
  }, [page]);

  const fetchCompanies = async () => {
    setLoading(true);
    try {
      const params = { page, size: pageSize };
      if (searchName) params.name = searchName;
      const res = await companyApi.getCompanies(params);
      setCompanies(res.data.content || []);
      setTotalPages(res.data.totalPages || 1);
    } catch (err) {
      console.error('Error fetching companies:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchCompanies();
  };

  const handleDeleteConfirm = async () => {
    if (!deleteTarget) return;
    setDeleteLoading(true);
    try {
      await companyApi.deleteCompany(deleteTarget.companyId);
      setDeleteTarget(null);
      fetchCompanies();
    } catch (err) {
      console.error('Delete company error:', err);
      alert(err.response?.data?.message || 'Could not delete company.');
    } finally {
      setDeleteLoading(false);
    }
  };

  return (
    <div>
      {/* Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4">
        <div>
          <h4 className="fw-bold text-slate-900 mb-1">Tenant Companies</h4>
          <p className="text-secondary small mb-0">
            Platform tenant management: onboarding companies, updating corporate profiles, and assigning Company Admins.
          </p>
        </div>
        <div className="mt-3 mt-md-0">
          <button
            className="btn btn-sp-primary fw-medium"
            onClick={() => {
              setEditCompany(null);
              setIsAdminMode(false);
              setShowModal(true);
            }}
          >
            <i className="bi bi-building-add me-1"></i> Add Tenant Company
          </button>
        </div>
      </div>

      {/* Filter */}
      <div className="sp-card p-3 mb-4">
        <form onSubmit={handleSearchSubmit} className="row g-2 align-items-center">
          <div className="col-12 col-md-8">
            <input
              type="text"
              className="form-control form-control-sm"
              placeholder="Search Company Name..."
              value={searchName}
              onChange={(e) => setSearchName(e.target.value)}
            />
          </div>
          <div className="col-12 col-md-4 d-flex gap-2">
            <button type="submit" className="btn btn-sm btn-sp-primary flex-grow-1 fw-medium">
              <i className="bi bi-search me-1"></i> Search
            </button>
            <button
              type="button"
              className="btn btn-sm btn-outline-secondary"
              onClick={() => {
                setSearchName('');
                setPage(0);
              }}
            >
              Reset
            </button>
          </div>
        </form>
      </div>

      {/* Table */}
      <div className="sp-card">
        <div className="sp-card-body p-0">
          {loading ? (
            <div className="p-5 text-center text-muted">
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Loading tenant companies...
            </div>
          ) : companies.length === 0 ? (
            <div className="p-5 text-center text-muted">
              <i className="bi bi-building-dash fs-1 d-block text-secondary mb-2"></i>
              No tenant companies found.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table sp-table mb-0 align-middle">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Company Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Address</th>
                    <th>Status</th>
                    <th className="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {companies.map((comp) => (
                    <tr key={comp.companyId}>
                      <td className="font-monospace text-muted">#{comp.companyId}</td>
                      <td className="fw-bold text-dark">{comp.companyName}</td>
                      <td>{comp.email}</td>
                      <td>{comp.phone}</td>
                      <td className="small text-secondary">{comp.address}</td>
                      <td>
                        <span
                          className={`badge ${
                            comp.active ? 'bg-success' : 'bg-secondary'
                          } px-2 py-1`}
                        >
                          {comp.active ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td className="text-end">
                        <div className="btn-group btn-group-sm">
                          <button
                            className="btn btn-light border text-primary me-1"
                            onClick={() => {
                              setEditCompany(comp);
                              setIsAdminMode(true);
                              setShowModal(true);
                            }}
                            title="Create Company Admin"
                          >
                            <i className="bi bi-person-plus me-1"></i> Admin
                          </button>
                          <button
                            className="btn btn-light border text-secondary me-1"
                            onClick={() => {
                              setEditCompany(comp);
                              setIsAdminMode(false);
                              setShowModal(true);
                            }}
                            title="Edit Company"
                          >
                            <i className="bi bi-pencil"></i>
                          </button>
                          <button
                            className="btn btn-light border text-danger"
                            onClick={() => setDeleteTarget(comp)}
                            title="Delete Company"
                          >
                            <i className="bi bi-trash"></i>
                          </button>
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

      <Pagination page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)} />

      {/* Modal */}
      <CompanyFormModal
        show={showModal}
        editCompany={editCompany}
        isAdminMode={isAdminMode}
        onClose={() => setShowModal(false)}
        onSuccess={() => {
          setShowModal(false);
          fetchCompanies();
        }}
      />

      {/* Delete Confirm */}
      <ConfirmModal
        show={!!deleteTarget}
        title="Delete Tenant Company"
        message={`Are you sure you want to delete company "${deleteTarget?.companyName}"? This action affects all tenant data.`}
        confirmText="Delete Company"
        confirmVariant="danger"
        loading={deleteLoading}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
};

export default CompanyListPage;

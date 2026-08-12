import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { procurementApi } from '../../api/procurementApi';
import StatusBadge from '../../components/StatusBadge';
import Pagination from '../../components/Pagination';
import ConfirmModal from '../../components/ConfirmModal';
import ProcurementFormModal from './ProcurementFormModal';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { PROCUREMENT_STATUS } from '../../utils/constants';

const ProcurementListPage = () => {
  const { isEmployee, isMasterAdmin } = useAuth();

  const [cases, setCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Filters
  const [statusFilter, setStatusFilter] = useState('');
  const [searchTitle, setSearchTitle] = useState('');
  const [searchCode, setSearchCode] = useState('');

  // Modals
  const [showModal, setShowModal] = useState(false);
  const [editCase, setEditCase] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  useEffect(() => {
    fetchCases();
  }, [page, statusFilter]);

  const fetchCases = async () => {
    setLoading(true);
    try {
      const params = {
        page,
        size: pageSize,
      };
      if (statusFilter) params.status = statusFilter;
      if (searchTitle) params.title = searchTitle;
      if (searchCode) params.procurementCode = searchCode;

      const res = await procurementApi.getProcurementCases(params);
      setCases(res.data.content || []);
      setTotalPages(res.data.totalPages || 1);
      setTotalElements(res.data.totalElements || 0);
    } catch (err) {
      console.error('Failed to load procurement cases:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchCases();
  };

  const handleClearFilters = () => {
    setStatusFilter('');
    setSearchTitle('');
    setSearchCode('');
    setPage(0);
  };

  const handleCreate = () => {
    setEditCase(null);
    setShowModal(true);
  };

  const handleEdit = (cs) => {
    setEditCase(cs);
    setShowModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!deleteTarget) return;
    setDeleteLoading(true);
    try {
      await procurementApi.deleteProcurementCase(deleteTarget.procurementCaseId);
      setDeleteTarget(null);
      fetchCases();
    } catch (err) {
      console.error('Delete case failed:', err);
      alert(err.response?.data?.message || 'Could not delete case.');
    } finally {
      setDeleteLoading(false);
    }
  };

  return (
    <div>
      {/* Page Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4">
        <div>
          <h4 className="fw-bold text-slate-900 mb-1">Procurement Cases</h4>
          <p className="text-secondary small mb-0">
            Manage procurement requests, vendor quotes, and approval workflows.
          </p>
        </div>
        {(isEmployee || isMasterAdmin) && (
          <div className="mt-3 mt-md-0">
            <button className="btn btn-sp-primary fw-medium" onClick={handleCreate}>
              <i className="bi bi-plus-lg me-1"></i> New Case
            </button>
          </div>
        )}
      </div>

      {/* Filter Card */}
      <div className="sp-card p-3 mb-4">
        <form onSubmit={handleSearchSubmit} className="row g-2 align-items-center">
          <div className="col-12 col-md-3">
            <input
              type="text"
              className="form-control form-control-sm"
              placeholder="Search Title..."
              value={searchTitle}
              onChange={(e) => setSearchTitle(e.target.value)}
            />
          </div>

          <div className="col-12 col-md-3">
            <input
              type="text"
              className="form-control form-control-sm"
              placeholder="Filter Code (e.g. PR-001)..."
              value={searchCode}
              onChange={(e) => setSearchCode(e.target.value)}
            />
          </div>

          <div className="col-12 col-md-3">
            <select
              className="form-select form-select-sm"
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value);
                setPage(0);
              }}
            >
              <option value="">All Statuses</option>
              {Object.keys(PROCUREMENT_STATUS).map((st) => (
                <option key={st} value={st}>
                  {st}
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
              onClick={handleClearFilters}
            >
              Reset
            </button>
          </div>
        </form>
      </div>

      {/* Main Table Card */}
      <div className="sp-card">
        <div className="sp-card-body p-0">
          {loading ? (
            <div className="p-5 text-center text-muted">
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Loading procurement cases...
            </div>
          ) : cases.length === 0 ? (
            <div className="p-5 text-center text-muted">
              <i className="bi bi-folder-x fs-1 d-block text-secondary mb-2"></i>
              No procurement cases found matching parameters.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table sp-table mb-0">
                <thead>
                  <tr>
                    <th>Code</th>
                    <th>Draft #</th>
                    <th>Title</th>
                    <th>Quantity</th>
                    <th>Package Amount</th>
                    <th>Status</th>
                    <th className="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {cases.map((cs) => (
                    <tr key={cs.procurementCaseId}>
                      <td className="fw-semibold text-primary">{cs.procurementCode}</td>
                      <td>
                        <span className="badge bg-light text-dark border">
                          v{cs.draftNumber || 1}
                        </span>
                      </td>
                      <td className="fw-medium text-dark">{cs.title}</td>
                      <td>
                        {cs.quantity} {cs.unit}
                      </td>
                      <td className="fw-semibold">{formatCurrency(cs.packageAmount)}</td>
                      <td>
                        <StatusBadge status={cs.status} />
                      </td>
                      <td className="text-end">
                        <div className="btn-group btn-group-sm">
                          <Link
                            to={`/procurement-cases/${cs.procurementCaseId}`}
                            className="btn btn-light text-primary border me-1"
                            title="View Case Details"
                          >
                            <i className="bi bi-eye"></i>
                          </Link>

                          {cs.status === PROCUREMENT_STATUS.DRAFT && (isEmployee || isMasterAdmin) && (
                            <>
                              <button
                                className="btn btn-light text-secondary border me-1"
                                onClick={() => handleEdit(cs)}
                                title="Edit Case"
                              >
                                <i className="bi bi-pencil"></i>
                              </button>
                              <button
                                className="btn btn-light text-danger border"
                                onClick={() => setDeleteTarget(cs)}
                                title="Delete Case"
                              >
                                <i className="bi bi-trash"></i>
                              </button>
                            </>
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

      {/* Pagination Controls */}
      <Pagination page={page} totalPages={totalPages} onPageChange={(p) => setPage(p)} />

      {/* Form Modal */}
      <ProcurementFormModal
        show={showModal}
        editCase={editCase}
        onClose={() => setShowModal(false)}
        onSuccess={() => {
          setShowModal(false);
          fetchCases();
        }}
      />

      {/* Delete Confirmation Modal */}
      <ConfirmModal
        show={!!deleteTarget}
        title="Delete Procurement Case"
        message={`Are you sure you want to delete case "${deleteTarget?.title}" (${deleteTarget?.procurementCode})? This action cannot be undone.`}
        confirmText="Delete Case"
        confirmVariant="danger"
        loading={deleteLoading}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
};

export default ProcurementListPage;

import React, { useEffect, useState } from 'react';
import useAuth from '../../hooks/useAuth';
import { matrixApi } from '../../api/matrixApi';
import ConfirmModal from '../../components/ConfirmModal';
import ApprovalMatrixFormModal from './ApprovalMatrixFormModal';
import { formatCurrency } from '../../utils/formatters';

const ApprovalMatrixPage = () => {
  const { user } = useAuth();
  const companyId = user?.companyId || 1;

  const [matrices, setMatrices] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modals
  const [showModal, setShowModal] = useState(false);
  const [editMatrix, setEditMatrix] = useState(null);

  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  useEffect(() => {
    fetchMatrices();
  }, [companyId]);

  const fetchMatrices = async () => {
    setLoading(true);
    try {
      const res = await matrixApi.getApprovalMatricesByCompany(companyId);
      setMatrices(res.data || []);
    } catch (err) {
      console.error('Error fetching approval matrix rules:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deleteTarget) return;
    setDeleteLoading(true);
    try {
      await matrixApi.deleteApprovalMatrix(deleteTarget.matrixId);
      setDeleteTarget(null);
      fetchMatrices();
    } catch (err) {
      console.error('Delete matrix rule error:', err);
      alert(err.response?.data?.message || 'Could not delete rule.');
    } finally {
      setDeleteLoading(false);
    }
  };

  return (
    <div>
      {/* Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4">
        <div>
          <h4 className="fw-bold text-slate-900 mb-1">Approval Matrix Configuration</h4>
          <p className="text-secondary small mb-0">
            Define corporate procurement thresholds, approval levels, and designated approvers.
          </p>
        </div>
        <div className="mt-3 mt-md-0">
          <button
            className="btn btn-sp-primary fw-medium"
            onClick={() => {
              setEditMatrix(null);
              setShowModal(true);
            }}
          >
            <i className="bi bi-diagram-3 me-1"></i> Add Rule Range
          </button>
        </div>
      </div>

      {/* Rules Table Card */}
      <div className="sp-card">
        <div className="sp-card-body p-0">
          {loading ? (
            <div className="p-5 text-center text-muted">
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Loading approval matrix configuration...
            </div>
          ) : matrices.length === 0 ? (
            <div className="p-5 text-center text-muted">
              <i className="bi bi-diagram-2 fs-1 d-block text-secondary mb-2"></i>
              No approval matrix rules configured for your company yet.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table sp-table mb-0 align-middle">
                <thead>
                  <tr>
                    <th>Level</th>
                    <th>Min Amount</th>
                    <th>Max Amount</th>
                    <th>Assigned Approver</th>
                    <th>Company</th>
                    <th className="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {matrices.map((m) => (
                    <tr key={m.matrixId}>
                      <td>
                        <span className="badge bg-primary px-2.5 py-1.5 font-monospace">
                          Level {m.approvalLevel}
                        </span>
                      </td>
                      <td className="fw-medium text-dark">{formatCurrency(m.minAmount)}</td>
                      <td className="fw-bold text-dark">{formatCurrency(m.maxAmount)}</td>
                      <td>
                        <div className="fw-semibold text-slate-900">{m.approverName || 'N/A'}</div>
                        <small className="text-muted font-monospace">ID: #{m.approverId}</small>
                      </td>
                      <td className="small text-secondary">{m.companyName || 'Corporate'}</td>
                      <td className="text-end">
                        <div className="btn-group btn-group-sm">
                          <button
                            className="btn btn-light border text-secondary me-1"
                            onClick={() => {
                              setEditMatrix(m);
                              setShowModal(true);
                            }}
                            title="Edit Rule"
                          >
                            <i className="bi bi-pencil"></i>
                          </button>
                          <button
                            className="btn btn-light border text-danger"
                            onClick={() => setDeleteTarget(m)}
                            title="Delete Rule"
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

      {/* Form Modal */}
      <ApprovalMatrixFormModal
        show={showModal}
        companyId={companyId}
        editMatrix={editMatrix}
        onClose={() => setShowModal(false)}
        onSuccess={() => {
          setShowModal(false);
          fetchMatrices();
        }}
      />

      {/* Delete Confirm */}
      <ConfirmModal
        show={!!deleteTarget}
        title="Delete Approval Rule"
        message={`Are you sure you want to delete Level ${deleteTarget?.approvalLevel} approval threshold range (${formatCurrency(
          deleteTarget?.minAmount
        )} - ${formatCurrency(deleteTarget?.maxAmount)})?`}
        confirmText="Delete Rule"
        confirmVariant="danger"
        loading={deleteLoading}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
};

export default ApprovalMatrixPage;

import React, { useState, useEffect } from 'react';
import { matrixApi } from '../../api/matrixApi';
import { userApi } from '../../api/userApi';
import useAuth from '../../hooks/useAuth';

const ApprovalMatrixFormModal = ({ show, companyId, editMatrix = null, onClose, onSuccess }) => {
  const { user } = useAuth();
  const targetCompanyId = companyId || user?.companyId || 1;

  const [formData, setFormData] = useState({
    minAmount: '0.00',
    maxAmount: '100000.00',
    approvalLevel: 1,
    approverId: '',
  });
  const [approvers, setApprovers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (show) {
      fetchApproverUsers();
    }
  }, [show]);

  useEffect(() => {
    if (editMatrix) {
      setFormData({
        minAmount: editMatrix.minAmount || '0.00',
        maxAmount: editMatrix.maxAmount || '100000.00',
        approvalLevel: editMatrix.approvalLevel || 1,
        approverId: editMatrix.approverId || '',
      });
    } else {
      setFormData({
        minAmount: '0.00',
        maxAmount: '100000.00',
        approvalLevel: 1,
        approverId: '',
      });
    }
    setError(null);
  }, [editMatrix, show]);

  const fetchApproverUsers = async () => {
    try {
      const res = await userApi.getUsers({ page: 0, size: 50 });
      const userList = res.data.content || [];
      // Filter users with MANAGER or MASTER_ADMIN role
      const eligible = userList.filter(
        (u) => u.userRole === 'MANAGER' || u.userRole === 'MASTER_ADMIN'
      );
      setApprovers(eligible.length > 0 ? eligible : userList);
      if (eligible.length > 0 && !formData.approverId) {
        setFormData((prev) => ({ ...prev, approverId: eligible[0].userId }));
      }
    } catch (err) {
      console.error('Error fetching approvers:', err);
    }
  };

  if (!show) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const minVal = parseFloat(formData.minAmount);
    const maxVal = parseFloat(formData.maxAmount);
    if (isNaN(minVal) || isNaN(maxVal) || minVal >= maxVal) {
      setError('Maximum amount must be strictly greater than Minimum amount.');
      return;
    }

    setLoading(true);

    try {
      const payload = {
        minAmount: minVal,
        maxAmount: maxVal,
        approvalLevel: parseInt(formData.approvalLevel, 10),
        approverId: parseInt(formData.approverId, 10),
        companyId: parseInt(targetCompanyId, 10),
      };

      if (editMatrix) {
        await matrixApi.updateApprovalMatrix(editMatrix.matrixId, payload);
      } else {
        await matrixApi.addApprovalMatrix(payload);
      }
      onSuccess();
    } catch (err) {
      console.error('Approval matrix submit error:', err);
      const errMsg = err.response?.data?.message || err.response?.data || 'Failed to save approval rule.';
      setError(typeof errMsg === 'string' ? errMsg : 'Rule submission failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal fade show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content border-0 shadow">
          <div className="modal-header border-bottom">
            <h5 className="modal-title fw-bold text-slate-800">
              {editMatrix ? 'Edit Approval Rule' : 'New Approval Rule'}
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
                  <label className="form-label fw-medium text-secondary small">Minimum Amount (₹) *</label>
                  <input
                    type="number"
                    step="0.01"
                    name="minAmount"
                    className="form-control"
                    value={formData.minAmount}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Maximum Amount (₹) *</label>
                  <input
                    type="number"
                    step="0.01"
                    name="maxAmount"
                    className="form-control"
                    value={formData.maxAmount}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Approval Level *</label>
                  <input
                    type="number"
                    name="approvalLevel"
                    className="form-control"
                    min="1"
                    value={formData.approvalLevel}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Assigned Approver *</label>
                  <select
                    name="approverId"
                    className="form-select"
                    value={formData.approverId}
                    onChange={handleChange}
                    required
                  >
                    <option value="">Select Approver User</option>
                    {approvers.map((u) => (
                      <option key={u.userId} value={u.userId}>
                        {u.firstName} {u.lastName} ({u.userRole})
                      </option>
                    ))}
                  </select>
                </div>
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
                {loading ? 'Saving...' : editMatrix ? 'Update Rule' : 'Add Rule'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ApprovalMatrixFormModal;

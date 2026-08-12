import React, { useState } from 'react';
import { approvalApi } from '../../api/approvalApi';

const ApprovalReviewModal = ({ show, csId, actionType, onClose, onSuccess }) => {
  const [remarks, setRemarks] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  if (!show) return null;

  const isReject = actionType === 'reject';
  const title = isReject ? 'Reject Procurement Case' : 'Approve Procurement Case';
  const confirmVariant = isReject ? 'danger' : 'success';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (isReject && !remarks.trim()) {
      setError('Remarks are mandatory when rejecting a case.');
      return;
    }

    setLoading(true);

    try {
      const payload = { remarks };
      if (isReject) {
        await approvalApi.rejectProcurementCase(csId, payload);
      } else {
        await approvalApi.approveProcurementCase(csId, payload);
      }
      onSuccess();
    } catch (err) {
      console.error('Approval decision error:', err);
      const errMsg = err.response?.data?.message || err.response?.data || 'Failed to submit decision.';
      setError(typeof errMsg === 'string' ? errMsg : 'Decision failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal fade show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(15, 23, 42, 0.5)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content border-0 shadow">
          <div className="modal-header border-bottom">
            <h5 className="modal-title fw-bold text-slate-800">{title}</h5>
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

              <p className="text-secondary small mb-3">
                {isReject
                  ? 'Rejecting will revert case status to DRAFT, increment the draft cycle number, and notify the author to modify quotes.'
                  : 'Approving will advance the case to the next level in the approval matrix or complete final approval.'}
              </p>

              <div className="mb-3">
                <label className="form-label fw-medium text-secondary small">
                  Decision Remarks {isReject ? '*' : '(Optional)'}
                </label>
                <textarea
                  className="form-control"
                  rows="3"
                  placeholder={
                    isReject
                      ? 'Specify reasons for rejection or required quote adjustments...'
                      : 'Add any optional approval comments...'
                  }
                  value={remarks}
                  onChange={(e) => setRemarks(e.target.value)}
                  required={isReject}
                ></textarea>
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
              <button type="submit" className={`btn btn-${confirmVariant} fw-medium px-4`} disabled={loading}>
                {loading ? 'Processing...' : isReject ? 'Reject Case' : 'Approve Case'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ApprovalReviewModal;

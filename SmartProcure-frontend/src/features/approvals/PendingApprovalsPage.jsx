import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { approvalApi } from '../../api/approvalApi';
import StatusBadge from '../../components/StatusBadge';
import ApprovalReviewModal from './ApprovalReviewModal';
import { formatCurrency, formatDate } from '../../utils/formatters';

const PendingApprovalsPage = () => {
  const { user } = useAuth();
  const [pendingCases, setPendingCases] = useState([]);
  const [loading, setLoading] = useState(true);

  // Review modal state
  const [selectedCsId, setSelectedCsId] = useState(null);
  const [reviewAction, setReviewAction] = useState(null);

  useEffect(() => {
    fetchPendingCases();
  }, []);

  const fetchPendingCases = async () => {
    setLoading(true);
    try {
      const res = await approvalApi.getPendingProcurementCases(user?.userId);
      setPendingCases(res.data || []);
    } catch (err) {
      console.error('Error fetching pending approvals:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenReview = (csId, action) => {
    setSelectedCsId(csId);
    setReviewAction(action);
  };

  return (
    <div>
      {/* Header */}
      <div className="mb-4">
        <h4 className="fw-bold text-slate-900 mb-1">Pending Approvals Inbox</h4>
        <p className="text-secondary small mb-0">
          Procurement cases awaiting your review and approval based on matrix thresholds.
        </p>
      </div>

      {/* Main Table */}
      <div className="sp-card">
        <div className="sp-card-body p-0">
          {loading ? (
            <div className="p-5 text-center text-muted">
              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
              Checking pending approval inbox...
            </div>
          ) : pendingCases.length === 0 ? (
            <div className="p-5 text-center text-muted">
              <i className="bi bi-check2-circle fs-1 d-block text-success mb-2"></i>
              Your approval inbox is completely clear! No pending requests.
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table sp-table mb-0 align-middle">
                <thead>
                  <tr>
                    <th>Code</th>
                    <th>Title</th>
                    <th>Quantity</th>
                    <th>Package Amount</th>
                    <th>Recommended Vendor</th>
                    <th>Status</th>
                    <th className="text-end">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {pendingCases.map((cs) => (
                    <tr key={cs.procurementCaseId}>
                      <td className="fw-semibold text-primary">{cs.procurementCode}</td>
                      <td className="fw-medium text-dark">{cs.title}</td>
                      <td>
                        {cs.quantity} {cs.unit}
                      </td>
                      <td className="fw-bold">{formatCurrency(cs.packageAmount)}</td>
                      <td>{cs.recommendedVendor || 'Not Specified'}</td>
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
                            <i className="bi bi-eye me-1"></i> View
                          </Link>
                          <button
                            className="btn btn-success me-1"
                            onClick={() => handleOpenReview(cs.procurementCaseId, 'approve')}
                          >
                            Approve
                          </button>
                          <button
                            className="btn btn-danger"
                            onClick={() => handleOpenReview(cs.procurementCaseId, 'reject')}
                          >
                            Reject
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

      {/* Decision Modal */}
      <ApprovalReviewModal
        show={!!reviewAction}
        csId={selectedCsId}
        actionType={reviewAction}
        onClose={() => {
          setReviewAction(null);
          setSelectedCsId(null);
        }}
        onSuccess={() => {
          setReviewAction(null);
          setSelectedCsId(null);
          fetchPendingCases();
        }}
      />
    </div>
  );
};

export default PendingApprovalsPage;

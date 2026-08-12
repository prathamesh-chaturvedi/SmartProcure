import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { procurementApi } from '../../api/procurementApi';
import { vendorQuoteApi } from '../../api/vendorQuoteApi';
import { approvalApi } from '../../api/approvalApi';
import StatusBadge from '../../components/StatusBadge';
import VendorQuoteList from '../vendorQuotes/VendorQuoteList';
import VendorQuoteFormModal from '../vendorQuotes/VendorQuoteFormModal';
import ApprovalReviewModal from '../approvals/ApprovalReviewModal';
import ApprovalHistoryTimeline from '../approvals/ApprovalHistoryTimeline';
import ConfirmModal from '../../components/ConfirmModal';
import { formatCurrency, formatDate } from '../../utils/formatters';
import { PROCUREMENT_STATUS, ROLES } from '../../utils/constants';

const ProcurementDetailPage = () => {
  const { csId } = useParams();
  const navigate = useNavigate();
  const { user, isEmployee, isManager, isMasterAdmin } = useAuth();

  const [procCase, setProcCase] = useState(null);
  const [quotes, setQuotes] = useState([]);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [downloadingCsPdf, setDownloadingCsPdf] = useState(false);

  // Quote Modals
  const [showQuoteModal, setShowQuoteModal] = useState(false);
  const [editQuote, setEditQuote] = useState(null);
  const [deleteQuoteTarget, setDeleteQuoteTarget] = useState(null);
  const [deleteQuoteLoading, setDeleteQuoteLoading] = useState(false);

  // Submit confirmation modal
  const [showSubmitConfirm, setShowSubmitConfirm] = useState(false);

  // Approval review modal
  const [reviewAction, setReviewAction] = useState(null); // 'approve' | 'reject' | null

  useEffect(() => {
    fetchCaseDetails();
  }, [csId]);

  const fetchCaseDetails = async () => {
    setLoading(true);
    try {
      const caseRes = await procurementApi.getProcurementCaseById(csId);
      setProcCase(caseRes.data);

      const quotesRes = await vendorQuoteApi.getRankedVendorQuotes(csId);
      setQuotes(quotesRes.data || []);

      const historyRes = await approvalApi.getApprovalHistory(csId);
      setHistory(historyRes.data || []);
    } catch (err) {
      console.error('Error fetching case details:', err);
    } finally {
      setLoading(false);
    }
  };

  // Download CS PDF Handler
  const handleDownloadCsPdf = async () => {
    setDownloadingCsPdf(true);
    try {
      const res = await procurementApi.downloadCsPdf(csId);
      const blob = new Blob([res.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;

      const contentDisposition = res.headers['content-disposition'];
      let filename = `CS_${procCase?.procurementCode || csId}.pdf`;
      if (contentDisposition) {
        const match = contentDisposition.match(/filename="?([^"]+)"?/);
        if (match && match[1]) filename = match[1];
      }
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Failed to download CS PDF:', err);
      alert(
        err.response?.data?.message ||
          'Comparative Statement PDF has not yet been generated or the request failed.'
      );
    } finally {
      setDownloadingCsPdf(false);
    }
  };

  // Handle single quote state update without full page refresh
  const handleSingleQuoteUpdated = (updatedQuote) => {
    setQuotes((prevQuotes) =>
      prevQuotes.map((q) => (q.quoteId === updatedQuote.quoteId ? updatedQuote : q))
    );
  };

  const isDraft = procCase?.status === PROCUREMENT_STATUS.DRAFT;
  const isEditable = isDraft && (isEmployee || isMasterAdmin);

  // Submit case for approval
  const handleSubmitCase = async () => {
    setActionLoading(true);
    try {
      await approvalApi.submitProcurementCase(csId);
      setShowSubmitConfirm(false);
      fetchCaseDetails();
    } catch (err) {
      console.error('Submit case error:', err);
      alert(err.response?.data?.message || 'Failed to submit procurement case.');
    } finally {
      setActionLoading(false);
    }
  };

  // Quote Delete Confirm
  const handleDeleteQuoteConfirm = async () => {
    if (!deleteQuoteTarget) return;
    setDeleteQuoteLoading(true);
    try {
      await vendorQuoteApi.deleteVendorQuote(deleteQuoteTarget.quoteId);
      setDeleteQuoteTarget(null);
      fetchCaseDetails();
    } catch (err) {
      console.error('Delete quote error:', err);
      alert(err.response?.data?.message || 'Failed to delete vendor quote.');
    } finally {
      setDeleteQuoteLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="p-5 text-center text-muted">
        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        Loading procurement case details...
      </div>
    );
  }

  if (!procCase) {
    return (
      <div className="p-5 text-center text-muted">
        <i className="bi bi-exclamation-circle fs-1 d-block mb-2 text-danger"></i>
        Procurement case not found.
        <div className="mt-3">
          <Link to="/procurement-cases" className="btn btn-sp-primary btn-sm">
            Back to List
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div>
      {/* Back Header & Download CS PDF Button */}
      <div className="d-flex align-items-center justify-content-between mb-3">
        <Link to="/procurement-cases" className="text-decoration-none text-secondary small fw-medium">
          <i className="bi bi-arrow-left me-1"></i> Back to Procurement Cases
        </Link>

        {/* Functional Download Case Summary PDF Button */}
        <button
          type="button"
          className="btn btn-outline-primary btn-sm fw-medium d-inline-flex align-items-center gap-2 shadow-sm"
          onClick={handleDownloadCsPdf}
          disabled={downloadingCsPdf}
        >
          {downloadingCsPdf ? (
            <>
              <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              Downloading CS PDF...
            </>
          ) : (
            <>
              <i className="bi bi-file-earmark-pdf-fill text-danger fs-6"></i>
              Download Case Summary PDF
            </>
          )}
        </button>
      </div>

      {/* Case Header Card */}
      <div className="sp-card mb-4">
        <div className="sp-card-body p-4">
          <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-start">
            <div>
              <div className="d-flex align-items-center gap-2 mb-2">
                <span className="badge bg-primary fs-6 px-3 py-1.5 font-monospace">
                  {procCase.procurementCode}
                </span>
                <span className="badge bg-light text-dark border px-2.5 py-1.5">
                  Draft #{procCase.draftNumber || 1}
                </span>
                <StatusBadge status={procCase.status} />
              </div>
              <h3 className="fw-bold text-slate-900 mb-2">{procCase.title}</h3>
              <p className="text-secondary mb-0 max-w-2xl">{procCase.description}</p>
            </div>

            <div className="mt-3 mt-md-0 text-md-end">
              <div className="small text-uppercase text-secondary fw-semibold">Package Total Amount</div>
              <div className="fs-3 fw-bold text-slate-900">
                {formatCurrency(procCase.packageAmount)}
              </div>
              {procCase.recommendedVendor && (
                <div className="small text-success fw-medium mt-1">
                  <i className="bi bi-award me-1"></i>
                  Recommended: <strong>{procCase.recommendedVendor}</strong>
                </div>
              )}
            </div>
          </div>

          {/* Action Bar */}
          <div className="mt-4 pt-3 border-top d-flex flex-wrap align-items-center justify-content-between gap-2">
            <div className="d-flex align-items-center gap-3">
              <span className="small text-secondary">
                <strong>Quantity:</strong> {procCase.quantity} {procCase.unit}
              </span>
            </div>

            <div className="d-flex gap-2">
              {/* Case Owner Submit Button */}
              {isDraft && (isEmployee || isMasterAdmin) && (
                <button
                  type="button"
                  className="btn btn-success fw-medium"
                  onClick={() => setShowSubmitConfirm(true)}
                  disabled={quotes.length === 0}
                >
                  <i className="bi bi-send me-1"></i> Submit for Approval
                </button>
              )}

              {/* Manager Approver Action Buttons */}
              {(procCase.status === PROCUREMENT_STATUS.SUBMITTED ||
                procCase.status === PROCUREMENT_STATUS.UNDER_REVIEW) &&
                (isManager || isMasterAdmin) && (
                  <>
                    <button
                      type="button"
                      className="btn btn-success fw-medium"
                      onClick={() => setReviewAction('approve')}
                    >
                      <i className="bi bi-check-lg me-1"></i> Approve
                    </button>
                    <button
                      type="button"
                      className="btn btn-danger fw-medium"
                      onClick={() => setReviewAction('reject')}
                    >
                      <i className="bi bi-x-lg me-1"></i> Reject
                    </button>
                  </>
                )}
            </div>
          </div>
        </div>
      </div>

      {/* Vendor Quotes Section */}
      <div className="sp-card mb-4">
        <div className="sp-card-header">
          <h5 className="sp-card-title">
            <i className="bi bi-calculator me-2"></i> Vendor Quotes &amp; L1 Cost Evaluation
          </h5>
          {isEditable && (
            <button
              className="btn btn-sm btn-sp-primary fw-medium"
              onClick={() => {
                setEditQuote(null);
                setShowQuoteModal(true);
              }}
            >
              <i className="bi bi-plus-lg me-1"></i> Add Quote
            </button>
          )}
        </div>
        <div className="sp-card-body p-0">
          <VendorQuoteList
            quotes={quotes}
            csId={csId}
            isEditable={isEditable}
            onEditQuote={(q) => {
              setEditQuote(q);
              setShowQuoteModal(true);
            }}
            onDeleteQuote={(q) => setDeleteQuoteTarget(q)}
            onQuoteUpdated={handleSingleQuoteUpdated}
          />
        </div>
      </div>

      {/* Approval Audit History Section */}
      <div className="sp-card">
        <div className="sp-card-header">
          <h5 className="sp-card-title">
            <i className="bi bi-journal-text me-2"></i> Approval Audit History (Multi-Cycle)
          </h5>
        </div>
        <div className="sp-card-body">
          <ApprovalHistoryTimeline history={history} />
        </div>
      </div>

      {/* Vendor Quote Form Modal */}
      <VendorQuoteFormModal
        show={showQuoteModal}
        csId={csId}
        editQuote={editQuote}
        onClose={() => setShowQuoteModal(false)}
        onSuccess={() => {
          setShowQuoteModal(false);
          fetchCaseDetails();
        }}
      />

      {/* Delete Quote Confirm Modal */}
      <ConfirmModal
        show={!!deleteQuoteTarget}
        title="Delete Vendor Quote"
        message={`Are you sure you want to remove quote from "${deleteQuoteTarget?.vendorName}"?`}
        confirmText="Delete Quote"
        confirmVariant="danger"
        loading={deleteQuoteLoading}
        onConfirm={handleDeleteQuoteConfirm}
        onCancel={() => setDeleteQuoteTarget(null)}
      />

      {/* Submit Case Confirm Modal */}
      <ConfirmModal
        show={showSubmitConfirm}
        title="Submit Case for Approval"
        message="Submitting this case will lock vendor quotes and trigger the approval matrix workflow. Are you sure?"
        confirmText="Submit Now"
        confirmVariant="success"
        loading={actionLoading}
        onConfirm={handleSubmitCase}
        onCancel={() => setShowSubmitConfirm(false)}
      />

      {/* Approve / Reject Modal */}
      <ApprovalReviewModal
        show={!!reviewAction}
        csId={csId}
        actionType={reviewAction}
        onClose={() => setReviewAction(null)}
        onSuccess={() => {
          setReviewAction(null);
          fetchCaseDetails();
        }}
      />
    </div>
  );
};

export default ProcurementDetailPage;

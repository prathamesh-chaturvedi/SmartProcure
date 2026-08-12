import React, { useState, useRef } from 'react';
import { vendorQuoteApi } from '../../api/vendorQuoteApi';
import PdfViewerModal from '../../components/PdfViewerModal';
import { formatCurrency } from '../../utils/formatters';

const VendorQuoteList = ({
  quotes = [],
  csId,
  isEditable = false,
  onEditQuote,
  onDeleteQuote,
  onQuoteUpdated,
}) => {
  // Modal PDF Viewer state
  const [showPdfModal, setShowPdfModal] = useState(false);
  const [pdfModalTitle, setPdfModalTitle] = useState('');
  const [pdfUrl, setPdfUrl] = useState(null);
  const [pdfLoading, setPdfLoading] = useState(false);
  const [pdfError, setPdfError] = useState(null);

  // Uploading State
  const [uploadingQuoteId, setUploadingQuoteId] = useState(null);
  const [activeUploadQuote, setActiveUploadQuote] = useState(null);
  const fileInputRef = useRef(null);

  // Handle View PDF inside Modal
  const handleViewPdf = async (quote) => {
    setPdfModalTitle(`Vendor Quotation PDF — ${quote.vendorName}`);
    setShowPdfModal(true);
    setPdfLoading(true);
    setPdfError(null);
    setPdfUrl(null);

    try {
      const res = await vendorQuoteApi.getQuotePdf(quote.quoteId);
      const blob = new Blob([res.data], { type: 'application/pdf' });
      const objectUrl = window.URL.createObjectURL(blob);
      setPdfUrl(objectUrl);
    } catch (err) {
      console.error('Fetch quote PDF error:', err);
      setPdfError(
        err.response?.status === 404
          ? `No quotation PDF document has been uploaded for ${quote.vendorName}.`
          : err.response?.data?.message || 'Failed to fetch quotation PDF from server.'
      );
    } finally {
      setPdfLoading(false);
    }
  };

  // Close PDF Modal
  const handleClosePdfModal = () => {
    setShowPdfModal(false);
    if (pdfUrl) {
      window.URL.revokeObjectURL(pdfUrl);
      setPdfUrl(null);
    }
    setPdfError(null);
  };

  // Trigger File Input Selector
  const handleTriggerUpload = (quote) => {
    setActiveUploadQuote(quote);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
      fileInputRef.current.click();
    }
  };

  // Handle File Selection and Upload
  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file || !activeUploadQuote) return;

    if (file.type !== 'application/pdf') {
      alert('Please select a valid PDF document (.pdf).');
      return;
    }

    const quoteId = activeUploadQuote.quoteId;
    setUploadingQuoteId(quoteId);

    const formData = new FormData();
    formData.append('file', file);

    try {
      await vendorQuoteApi.uploadQuotePdf(quoteId, formData);

      // Refresh ONLY the affected vendor quote
      if (csId && onQuoteUpdated) {
        const updatedQuoteRes = await vendorQuoteApi.getVendorQuote(quoteId, csId);
        onQuoteUpdated(updatedQuoteRes.data);
      }
    } catch (err) {
      console.error('Upload quote PDF error:', err);
      alert(err.response?.data?.message || 'Failed to upload vendor quote PDF.');
    } finally {
      setUploadingQuoteId(null);
      setActiveUploadQuote(null);
    }
  };

  if (quotes.length === 0) {
    return (
      <div className="text-center py-4 text-muted bg-light rounded border border-dashed m-3">
        <i className="bi bi-card-list fs-2 d-block mb-1 text-secondary"></i>
        No vendor quotes uploaded yet. Add at least 1 vendor quote to compare.
      </div>
    );
  }

  return (
    <>
      {/* Hidden File Input for PDF Upload */}
      <input
        type="file"
        ref={fileInputRef}
        accept="application/pdf"
        style={{ display: 'none' }}
        onChange={handleFileChange}
      />

      <div className="table-responsive">
        <table className="table sp-table align-middle mb-0">
          <thead>
            <tr>
              <th>Rank</th>
              <th>Vendor Name</th>
              <th>Quoted Rate</th>
              <th>Quoted Amount</th>
              <th>Transport Cost</th>
              <th>Effective Cost</th>
              <th>Terms &amp; Delivery</th>
              <th>Quotation PDF</th>
              {isEditable && <th className="text-end">Actions</th>}
            </tr>
          </thead>
          <tbody>
            {quotes.map((quote, idx) => {
              const isL1 = idx === 0;
              const hasPdf = Boolean(quote.quotePdfPath);
              const isUploading = uploadingQuoteId === quote.quoteId;

              return (
                <tr key={quote.quoteId} className={isL1 ? 'table-success bg-opacity-10' : ''}>
                  <td>
                    <span
                      className={`badge ${
                        isL1 ? 'bg-success' : 'bg-secondary'
                      } rounded-pill font-monospace`}
                    >
                      {isL1 ? 'L1 (Recommended)' : `L${idx + 1}`}
                    </span>
                  </td>
                  <td className="fw-semibold text-dark">
                    {quote.vendorName}
                    {isL1 && <i className="bi bi-star-fill text-warning ms-1" title="Lowest Cost Vendor"></i>}
                  </td>
                  <td>{formatCurrency(quote.quotedRate)}</td>
                  <td>{formatCurrency(quote.quotedAmount)}</td>
                  <td>{formatCurrency(quote.transportationCost)}</td>
                  <td className="fw-bold text-slate-900">{formatCurrency(quote.effectiveCost)}</td>
                  <td className="small">
                    <div><strong>Payment:</strong> {quote.paymentTerms || 'N/A'}</div>
                    <div><strong>Delivery:</strong> {quote.deliveryPeriod || 'N/A'}</div>
                  </td>
                  <td>
                    <div className="d-flex align-items-center gap-1">
                      {/* View PDF Button */}
                      <button
                        type="button"
                        className={`btn btn-sm ${hasPdf ? 'btn-outline-danger' : 'btn-outline-secondary'} py-0 px-2 fw-medium`}
                        onClick={() => handleViewPdf(quote)}
                        title={hasPdf ? 'View Vendor Quotation PDF' : 'No PDF uploaded'}
                      >
                        <i className={`bi ${hasPdf ? 'bi-file-earmark-pdf-fill' : 'bi-file-earmark-pdf'} me-1`}></i>
                        {hasPdf ? 'View PDF' : 'PDF'}
                      </button>

                      {/* Upload / Replace PDF Button */}
                      {isEditable && (
                        <button
                          type="button"
                          className="btn btn-sm btn-outline-primary py-0 px-2 fw-medium"
                          onClick={() => handleTriggerUpload(quote)}
                          disabled={isUploading}
                          title="Upload/Save Vendor Quote PDF"
                        >
                          {isUploading ? (
                            <>
                              <span className="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                              Uploading...
                            </>
                          ) : (
                            <>
                              <i className="bi bi-upload me-1"></i>
                              {hasPdf ? 'Replace' : 'Upload'}
                            </>
                          )}
                        </button>
                      )}
                    </div>
                  </td>
                  {isEditable && (
                    <td className="text-end">
                      <div className="btn-group btn-group-sm">
                        <button
                          className="btn btn-light border text-secondary me-1"
                          onClick={() => onEditQuote(quote)}
                          title="Edit Quote"
                        >
                          <i className="bi bi-pencil"></i>
                        </button>
                        <button
                          className="btn btn-light border text-danger"
                          onClick={() => onDeleteQuote(quote)}
                          title="Delete Quote"
                        >
                          <i className="bi bi-trash"></i>
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Modal PDF Viewer */}
      <PdfViewerModal
        show={showPdfModal}
        title={pdfModalTitle}
        pdfUrl={pdfUrl}
        loading={pdfLoading}
        error={pdfError}
        onClose={handleClosePdfModal}
      />
    </>
  );
};

export default VendorQuoteList;

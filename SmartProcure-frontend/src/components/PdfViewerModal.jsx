import React from 'react';

const PdfViewerModal = ({ show, title, pdfUrl, loading, error, onClose }) => {
  if (!show) return null;

  return (
    <div
      className="modal fade show d-block"
      tabIndex="-1"
      style={{ backgroundColor: 'rgba(0, 0, 0, 0.65)', zIndex: 1060 }}
    >
      <div className="modal-dialog modal-xl modal-dialog-centered" style={{ height: '90vh', maxHeight: '90vh' }}>
        <div className="modal-content h-100 shadow-lg border-0">
          <div className="modal-header py-2.5 bg-dark text-white border-bottom border-secondary">
            <h6 className="modal-title fw-bold d-flex align-items-center gap-2 mb-0">
              <i className="bi bi-file-earmark-pdf-fill text-danger fs-5"></i>
              {title || 'PDF Document Viewer'}
            </h6>
            <button
              type="button"
              className="btn-close btn-close-white"
              onClick={onClose}
              aria-label="Close"
            ></button>
          </div>

          <div className="modal-body p-0 d-flex flex-column justify-content-center align-items-center bg-secondary bg-opacity-10 position-relative overflow-hidden h-100">
            {loading && (
              <div className="text-center p-4">
                <div className="spinner-border text-primary mb-3" role="status" style={{ width: '3rem', height: '3rem' }}>
                  <span className="visually-hidden">Loading...</span>
                </div>
                <div className="fw-medium text-secondary">Fetching PDF document from server...</div>
              </div>
            )}

            {!loading && error && (
              <div className="alert alert-warning m-4 max-w-md text-center shadow-sm border border-warning">
                <i className="bi bi-exclamation-triangle-fill fs-1 text-warning mb-2 d-block"></i>
                <h6 className="fw-bold text-dark mb-1">Quotation PDF Unavailable</h6>
                <p className="small text-secondary mb-0">{error}</p>
              </div>
            )}

            {!loading && !error && pdfUrl && (
              <iframe
                src={pdfUrl}
                title={title || 'PDF Viewer'}
                className="w-100 h-100 border-0"
                style={{ minHeight: '450px' }}
              />
            )}
          </div>

          <div className="modal-footer py-2 bg-light border-top d-flex justify-content-between">
            {pdfUrl && !error ? (
              <a
                href={pdfUrl}
                download="Quotation_Document.pdf"
                className="btn btn-sm btn-outline-primary fw-medium"
              >
                <i className="bi bi-download me-1"></i> Download PDF File
              </a>
            ) : (
              <div></div>
            )}
            <button type="button" className="btn btn-sm btn-secondary fw-medium px-3" onClick={onClose}>
              Close Viewer
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PdfViewerModal;

import React, { useState, useEffect } from 'react';
import { vendorQuoteApi } from '../../api/vendorQuoteApi';

const VendorQuoteFormModal = ({ show, csId, editQuote = null, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    vendorName: '',
    quotedRate: '',
    transportationCost: '0.00',
    paymentTerms: '',
    deliveryPeriod: '',
    validity: '',
    warranty: '',
    remarks: '',
    quoteDate: new Date().toISOString().split('T')[0],
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (editQuote) {
      setFormData({
        vendorName: editQuote.vendorName || '',
        quotedRate: editQuote.quotedRate || '',
        transportationCost: editQuote.transportationCost || '0.00',
        paymentTerms: editQuote.paymentTerms || '',
        deliveryPeriod: editQuote.deliveryPeriod || '',
        validity: editQuote.validity || '',
        warranty: editQuote.warranty || '',
        remarks: editQuote.remarks || '',
        quoteDate: editQuote.quoteDate || new Date().toISOString().split('T')[0],
      });
    } else {
      setFormData({
        vendorName: '',
        quotedRate: '',
        transportationCost: '0.00',
        paymentTerms: '',
        deliveryPeriod: '',
        validity: '',
        warranty: '',
        remarks: '',
        quoteDate: new Date().toISOString().split('T')[0],
      });
    }
    setError(null);
  }, [editQuote, show]);

  if (!show) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const payload = {
        vendorName: formData.vendorName,
        quotedRate: parseFloat(formData.quotedRate),
        transportationCost: parseFloat(formData.transportationCost || 0),
        paymentTerms: formData.paymentTerms,
        deliveryPeriod: formData.deliveryPeriod,
        validity: formData.validity,
        warranty: formData.warranty,
        remarks: formData.remarks,
        quoteDate: formData.quoteDate,
        procurementCaseId: parseInt(csId, 10),
      };

      if (editQuote) {
        await vendorQuoteApi.updateVendorQuote(editQuote.quoteId, payload);
      } else {
        await vendorQuoteApi.addVendorQuote(payload);
      }
      onSuccess();
    } catch (err) {
      console.error('Error saving vendor quote:', err);
      const errMsg = err.response?.data?.message || err.response?.data || 'Failed to save quote.';
      setError(typeof errMsg === 'string' ? errMsg : 'Quote submission failed');
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
              {editQuote ? 'Edit Vendor Quote' : 'Add Vendor Quote'}
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
                  <label className="form-label fw-medium text-secondary small">Vendor Name *</label>
                  <input
                    type="text"
                    name="vendorName"
                    className="form-control"
                    placeholder="e.g. Acme Tech Corp"
                    value={formData.vendorName}
                    onChange={handleChange}
                    maxLength={100}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Quote Date *</label>
                  <input
                    type="date"
                    name="quoteDate"
                    className="form-control"
                    value={formData.quoteDate}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Quoted Rate per Unit (₹) *</label>
                  <input
                    type="number"
                    step="0.01"
                    name="quotedRate"
                    className="form-control"
                    placeholder="0.00"
                    value={formData.quotedRate}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Transportation Cost (₹) *</label>
                  <input
                    type="number"
                    step="0.01"
                    name="transportationCost"
                    className="form-control"
                    placeholder="0.00"
                    value={formData.transportationCost}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Payment Terms</label>
                  <input
                    type="text"
                    name="paymentTerms"
                    className="form-control"
                    placeholder="e.g. 50% advance, 50% on delivery"
                    value={formData.paymentTerms}
                    onChange={handleChange}
                    maxLength={255}
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Delivery Period</label>
                  <input
                    type="text"
                    name="deliveryPeriod"
                    className="form-control"
                    placeholder="e.g. 14 Days"
                    value={formData.deliveryPeriod}
                    onChange={handleChange}
                    maxLength={255}
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Validity</label>
                  <input
                    type="text"
                    name="validity"
                    className="form-control"
                    placeholder="e.g. Valid 30 days"
                    value={formData.validity}
                    onChange={handleChange}
                    maxLength={100}
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Warranty</label>
                  <input
                    type="text"
                    name="warranty"
                    className="form-control"
                    placeholder="e.g. 1 Year Manufacturer Warranty"
                    value={formData.warranty}
                    onChange={handleChange}
                    maxLength={100}
                  />
                </div>

                <div className="col-12">
                  <label className="form-label fw-medium text-secondary small">Remarks / Evaluation Notes</label>
                  <textarea
                    name="remarks"
                    className="form-control"
                    rows="2"
                    placeholder="Any specific technical compliance or notes..."
                    value={formData.remarks}
                    onChange={handleChange}
                    maxLength={1000}
                  ></textarea>
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
                {loading ? 'Saving Quote...' : editQuote ? 'Update Quote' : 'Add Quote'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default VendorQuoteFormModal;

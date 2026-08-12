import React, { useState, useEffect } from 'react';
import { procurementApi } from '../../api/procurementApi';

const ProcurementFormModal = ({ show, editCase = null, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    unit: '',
    quantity: 1,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (editCase) {
      setFormData({
        title: editCase.title || '',
        description: editCase.description || '',
        unit: editCase.unit || '',
        quantity: editCase.quantity || 1,
      });
    } else {
      setFormData({
        title: '',
        description: '',
        unit: '',
        quantity: 1,
      });
    }
    setError(null);
  }, [editCase, show]);

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
        title: formData.title,
        description: formData.description,
        unit: formData.unit,
        quantity: parseInt(formData.quantity, 10),
      };

      if (editCase) {
        await procurementApi.updateProcurementCase(editCase.procurementCaseId, payload);
      } else {
        await procurementApi.createProcurementCase(payload);
      }
      onSuccess();
    } catch (err) {
      console.error('Error saving procurement case:', err);
      const errMsg = err.response?.data?.message || err.response?.data || 'Failed to save case.';
      setError(typeof errMsg === 'string' ? errMsg : 'Form submission failed');
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
              {editCase ? 'Edit Procurement Case' : 'New Procurement Case'}
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

              <div className="mb-3">
                <label className="form-label fw-medium text-secondary small">Case Title *</label>
                <input
                  type="text"
                  name="title"
                  className="form-control"
                  placeholder="e.g. Laptops and Monitors for IT Dept"
                  value={formData.title}
                  onChange={handleChange}
                  maxLength={50}
                  required
                />
                <div className="form-text text-muted" style={{ fontSize: '0.75rem' }}>Max 50 characters</div>
              </div>

              <div className="mb-3">
                <label className="form-label fw-medium text-secondary small">Description *</label>
                <textarea
                  name="description"
                  className="form-control"
                  rows="3"
                  placeholder="Provide technical specifications or justification..."
                  value={formData.description}
                  onChange={handleChange}
                  maxLength={250}
                  required
                ></textarea>
                <div className="form-text text-muted" style={{ fontSize: '0.75rem' }}>Max 250 characters</div>
              </div>

              <div className="row g-3">
                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Unit of Measurement *</label>
                  <input
                    type="text"
                    name="unit"
                    className="form-control"
                    placeholder="e.g. Set, Units, Boxes"
                    value={formData.unit}
                    onChange={handleChange}
                    maxLength={50}
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label fw-medium text-secondary small">Quantity *</label>
                  <input
                    type="number"
                    name="quantity"
                    className="form-control"
                    min="1"
                    value={formData.quantity}
                    onChange={handleChange}
                    required
                  />
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
                {loading ? 'Saving...' : editCase ? 'Update Case' : 'Create Case'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ProcurementFormModal;

import React, { useState } from 'react';
import useAuth from '../../hooks/useAuth';
import { authApi } from '../../api/authApi';

const ChangePasswordPage = () => {
  const { user } = useAuth();
  const [email, setEmail] = useState(user?.email || '');
  const [password, setPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);

    if (newPassword !== confirmPassword) {
      setError('New password and confirm password do not match.');
      return;
    }

    if (newPassword.length < 8) {
      setError('New password must be at least 8 characters long.');
      return;
    }

    setLoading(true);

    try {
      await authApi.changePassword({
        email,
        password,
        newPassword,
      });
      setSuccess(true);
      setPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      console.error('Change password error:', err);
      const errMsg =
        err.response?.data?.message ||
        err.response?.data ||
        'Password update failed. Verify current password.';
      setError(typeof errMsg === 'string' ? errMsg : 'Operation failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '600px' }}>
      <div className="mb-4">
        <h4 className="fw-bold text-slate-900 mb-1">Change Account Password</h4>
        <p className="text-secondary small mb-0">
          Ensure your account is using a strong, unique security key.
        </p>
      </div>

      <div className="sp-card">
        <div className="sp-card-body p-4">
          {error && (
            <div className="alert alert-danger py-2 px-3 small mb-3">
              <i className="bi bi-exclamation-triangle-fill me-2"></i>
              {error}
            </div>
          )}

          {success && (
            <div className="alert alert-success py-2 px-3 small mb-3">
              <i className="bi bi-check-circle-fill me-2"></i>
              Password updated successfully!
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label fw-medium text-secondary small">Email Address *</label>
              <input
                type="email"
                className="form-control"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="mb-3">
              <label className="form-label fw-medium text-secondary small">Current Password *</label>
              <input
                type="password"
                className="form-control"
                placeholder="Enter current password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <div className="mb-3">
              <label className="form-label fw-medium text-secondary small">New Password *</label>
              <input
                type="password"
                className="form-control"
                placeholder="Min 8 characters"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                minLength={8}
                required
              />
            </div>

            <div className="mb-4">
              <label className="form-label fw-medium text-secondary small">Confirm New Password *</label>
              <input
                type="password"
                className="form-control"
                placeholder="Re-enter new password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                minLength={8}
                required
              />
            </div>

            <button type="submit" className="btn btn-sp-primary fw-medium px-4 py-2" disabled={loading}>
              {loading ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  Updating Password...
                </>
              ) : (
                'Update Password'
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ChangePasswordPage;

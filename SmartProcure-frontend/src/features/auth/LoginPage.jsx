import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { authApi } from '../../api/authApi';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/dashboard';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await authApi.login({ email, password });
      login(response.data);
      navigate(from, { replace: true });
    } catch (err) {
      console.error('Login error:', err);
      const errMsg =
        err.response?.data?.message ||
        err.response?.data ||
        'Invalid credentials. Please check email and password.';
      setError(typeof errMsg === 'string' ? errMsg : 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const fillSeedCredentials = (seedEmail, seedPassword) => {
    setEmail(seedEmail);
    setPassword(seedPassword);
  };

  return (
    <div
      className="d-flex align-items-center justify-content-center min-vh-100"
      style={{ backgroundColor: '#f8fafc' }}
    >
      <div className="container" style={{ maxWidth: '440px' }}>
        <div className="sp-card border-0 shadow-lg p-2">
          <div className="sp-card-body p-4">
            <div className="text-center mb-4">
              <div
                className="d-inline-flex align-items-center justify-content-center bg-primary text-white rounded-3 mb-3"
                style={{ width: '54px', height: '54px', backgroundColor: '#1e3a8a' }}
              >
                <i className="bi bi-shield-check fs-2"></i>
              </div>
              <h4 className="fw-bold text-slate-900 mb-1">SmartProcure</h4>
              <p className="text-secondary small">Enterprise Procurement &amp; Approval System</p>
            </div>

            {error && (
              <div className="alert alert-danger py-2 px-3 small d-flex align-items-center" role="alert">
                <i className="bi bi-exclamation-triangle-fill me-2"></i>
                <div>{error}</div>
              </div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label text-secondary fw-medium small mb-1">Email Address</label>
                <div className="input-group">
                  <span className="input-group-text bg-light text-secondary border-end-0">
                    <i className="bi bi-envelope"></i>
                  </span>
                  <input
                    type="email"
                    className="form-control border-start-0 ps-0"
                    placeholder="name@company.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="mb-4">
                <label className="form-label text-secondary fw-medium small mb-1">Password</label>
                <div className="input-group">
                  <span className="input-group-text bg-light text-secondary border-end-0">
                    <i className="bi bi-lock"></i>
                  </span>
                  <input
                    type="password"
                    className="form-control border-start-0 ps-0"
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
              </div>

              <button
                type="submit"
                className="btn btn-sp-primary w-100 py-2 fw-semibold mb-3"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    Authenticating...
                  </>
                ) : (
                  'Sign In'
                )}
              </button>
            </form>

            <div className="mt-4 pt-3 border-top">
              <div className="text-muted small fw-semibold mb-2">Default Credentials Quick-Fill:</div>
              <div className="d-flex flex-wrap gap-1">
                <button
                  type="button"
                  className="btn btn-outline-secondary btn-sm"
                  style={{ fontSize: '0.725rem' }}
                  onClick={() => fillSeedCredentials('masteradmin@gmail.com', 'Juno@123')}
                >
                  Master Admin
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;

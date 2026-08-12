import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import useAuth from '../../hooks/useAuth';
import { procurementApi } from '../../api/procurementApi';
import { approvalApi } from '../../api/approvalApi';
import StatusBadge from '../../components/StatusBadge';
import { formatCurrency, formatDate, formatRole } from '../../utils/formatters';

const DashboardPage = () => {
  const { user, isManager, isMasterAdmin, isEmployee } = useAuth();
  const [cases, setCases] = useState([]);
  const [pendingCases, setPendingCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [metrics, setMetrics] = useState({
    total: 0,
    drafts: 0,
    underReview: 0,
    approved: 0,
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const res = await procurementApi.getProcurementCases({ page: 0, size: 5 });
      const content = res.data.content || [];
      setCases(content);

      // Compute simple metrics from the returned dataset or totalElements
      let draftCount = 0;
      let reviewCount = 0;
      let approvedCount = 0;

      content.forEach((c) => {
        if (c.status === 'DRAFT') draftCount++;
        else if (c.status === 'UNDER_REVIEW' || c.status === 'SUBMITTED') reviewCount++;
        else if (c.status === 'APPROVED') approvedCount++;
      });

      setMetrics({
        total: res.data.totalElements || content.length,
        drafts: draftCount,
        underReview: reviewCount,
        approved: approvedCount,
      });

      if (isManager || isMasterAdmin) {
        const pendingRes = await approvalApi.getPendingProcurementCases(user?.userId);
        setPendingCases(pendingRes.data || []);
      }
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {/* Welcome Banner */}
      <div className="sp-card border-0 mb-4 bg-white p-4">
        <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center">
          <div>
            <h4 className="fw-bold text-slate-900 mb-1">
              Welcome back, {user?.firstName || 'User'}!
            </h4>
            <p className="text-secondary small mb-0">
              Role: <span className="fw-semibold text-dark">{formatRole(user?.userRole)}</span> | Enterprise Procurement Workspace
            </p>
          </div>
          {isEmployee && (
            <div className="mt-3 mt-md-0">
              <Link to="/procurement-cases" className="btn btn-sp-primary fw-medium">
                <i className="bi bi-plus-lg me-1"></i> New Procurement Case
              </Link>
            </div>
          )}
        </div>
      </div>

      {/* Summary Stat Cards */}
      <div className="row g-3 mb-4">
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="sp-stat-card">
            <div className="sp-stat-icon bg-primary bg-opacity-10 text-primary">
              <i className="bi bi-folder2-open"></i>
            </div>
            <div>
              <div className="sp-stat-label">Total Cases</div>
              <div className="sp-stat-value">{metrics.total}</div>
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="sp-stat-card">
            <div className="sp-stat-icon bg-secondary bg-opacity-10 text-secondary">
              <i className="bi bi-pencil-square"></i>
            </div>
            <div>
              <div className="sp-stat-label">Draft Cases</div>
              <div className="sp-stat-value">{metrics.drafts}</div>
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="sp-stat-card">
            <div className="sp-stat-icon bg-warning bg-opacity-10 text-warning">
              <i className="bi bi-hourglass-split"></i>
            </div>
            <div>
              <div className="sp-stat-label">Under Review</div>
              <div className="sp-stat-value">{metrics.underReview}</div>
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="sp-stat-card">
            <div className="sp-stat-icon bg-success bg-opacity-10 text-success">
              <i className="bi bi-check-circle"></i>
            </div>
            <div>
              <div className="sp-stat-label">Approved</div>
              <div className="sp-stat-value">{metrics.approved}</div>
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        {/* Recent Cases */}
        <div className={isManager || isMasterAdmin ? 'col-12 col-lg-7' : 'col-12'}>
          <div className="sp-card">
            <div className="sp-card-header">
              <h5 className="sp-card-title">Recent Procurement Cases</h5>
              <Link to="/procurement-cases" className="btn btn-sm btn-light text-primary fw-medium">
                View All <i className="bi bi-arrow-right ms-1"></i>
              </Link>
            </div>
            <div className="sp-card-body p-0">
              {loading ? (
                <div className="p-4 text-center text-muted">Loading procurement cases...</div>
              ) : cases.length === 0 ? (
                <div className="p-4 text-center text-muted">No procurement cases found.</div>
              ) : (
                <div className="table-responsive">
                  <table className="table sp-table mb-0">
                    <thead>
                      <tr>
                        <th>Code</th>
                        <th>Title</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {cases.map((cs) => (
                        <tr key={cs.procurementCaseId}>
                          <td className="fw-semibold text-primary">{cs.procurementCode}</td>
                          <td>{cs.title}</td>
                          <td>{formatCurrency(cs.packageAmount)}</td>
                          <td>
                            <StatusBadge status={cs.status} />
                          </td>
                          <td>
                            <Link
                              to={`/procurement-cases/${cs.procurementCaseId}`}
                              className="btn btn-light btn-sm px-2 py-1 text-secondary"
                            >
                              <i className="bi bi-eye"></i>
                            </Link>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Manager Pending Inbox */}
        {(isManager || isMasterAdmin) && (
          <div className="col-12 col-lg-5">
            <div className="sp-card">
              <div className="sp-card-header">
                <h5 className="sp-card-title">Pending My Approval</h5>
                <span className="badge bg-danger rounded-pill">{pendingCases.length}</span>
              </div>
              <div className="sp-card-body p-0">
                {loading ? (
                  <div className="p-4 text-center text-muted">Checking pending approvals...</div>
                ) : pendingCases.length === 0 ? (
                  <div className="p-4 text-center text-muted">No pending cases awaiting your decision.</div>
                ) : (
                  <div className="list-group list-group-flush">
                    {pendingCases.slice(0, 5).map((p) => (
                      <div
                        key={p.procurementCaseId}
                        className="list-group-item p-3 d-flex align-items-center justify-content-between"
                      >
                        <div>
                          <div className="fw-semibold text-dark">{p.title}</div>
                          <div className="small text-muted">
                            Code: {p.procurementCode} | Amount: {formatCurrency(p.packageAmount)}
                          </div>
                        </div>
                        <Link
                          to={`/procurement-cases/${p.procurementCaseId}`}
                          className="btn btn-sm btn-sp-primary fw-medium ms-2"
                        >
                          Review
                        </Link>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default DashboardPage;

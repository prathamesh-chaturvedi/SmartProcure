import React, { useEffect, useState } from 'react';
import useAuth from '../../hooks/useAuth';
import { userApi } from '../../api/userApi';
import { companyApi } from '../../api/companyApi';
import { formatRole, formatDesignation, formatDate } from '../../utils/formatters';
import UserFormModal from '../users/UserFormModal';

const ProfilePage = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [company, setCompany] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showEditModal, setShowEditModal] = useState(false);

  useEffect(() => {
    fetchProfileData();
  }, []);

  const fetchProfileData = async () => {
    setLoading(true);
    try {
      const userRes = await userApi.getProfile();
      setProfile(userRes.data);

      try {
        const compRes = await companyApi.getOwnCompany();
        setCompany(compRes.data);
      } catch (cErr) {
        console.error('Error fetching company details:', cErr);
      }
    } catch (err) {
      console.error('Error fetching profile:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="p-5 text-center text-muted">
        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
        Loading profile information...
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px' }}>
      {/* Profile Header Card */}
      <div className="sp-card mb-4">
        <div className="sp-card-body p-4">
          <div className="d-flex flex-column flex-sm-row align-items-sm-center justify-content-between">
            <div className="d-flex align-items-center mb-3 mb-sm-0">
              <div
                className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center me-3 font-monospace fw-bold"
                style={{ width: '64px', height: '64px', fontSize: '1.8rem', backgroundColor: '#1e3a8a' }}
              >
                {profile?.firstName ? profile.firstName.charAt(0).toUpperCase() : 'U'}
              </div>
              <div>
                <h4 className="fw-bold text-slate-900 mb-1">
                  {profile?.firstName} {profile?.lastName}
                </h4>
                <div className="text-secondary small">
                  <span className="badge bg-light text-dark border me-2">
                    {formatRole(profile?.userRole)}
                  </span>
                  {formatDesignation(profile?.designation)}
                </div>
              </div>
            </div>

            <button
              className="btn btn-outline-secondary btn-sm fw-medium"
              onClick={() => setShowEditModal(true)}
            >
              <i className="bi bi-pencil me-1"></i> Edit Profile
            </button>
          </div>
        </div>
      </div>

      {/* Account Info Details */}
      <div className="sp-card mb-4">
        <div className="sp-card-header">
          <h5 className="sp-card-title">Personal Information</h5>
        </div>
        <div className="sp-card-body">
          <div className="row g-3">
            <div className="col-12 col-sm-6">
              <label className="text-uppercase text-muted small fw-semibold d-block">First Name</label>
              <div className="fw-medium text-dark">{profile?.firstName || 'N/A'}</div>
            </div>

            <div className="col-12 col-sm-6">
              <label className="text-uppercase text-muted small fw-semibold d-block">Last Name</label>
              <div className="fw-medium text-dark">{profile?.lastName || 'N/A'}</div>
            </div>

            <div className="col-12 col-sm-6">
              <label className="text-uppercase text-muted small fw-semibold d-block">Email Address</label>
              <div className="fw-medium text-dark">{profile?.email || 'N/A'}</div>
            </div>

            <div className="col-12 col-sm-6">
              <label className="text-uppercase text-muted small fw-semibold d-block">Date of Birth</label>
              <div className="fw-medium text-dark">{formatDate(profile?.dob)}</div>
            </div>
          </div>
        </div>
      </div>

      {/* Company Info */}
      <div className="sp-card">
        <div className="sp-card-header">
          <h5 className="sp-card-title">Company Affiliation</h5>
        </div>
        <div className="sp-card-body">
          <div className="row g-3">
            <div className="col-12 col-sm-6">
              <label className="text-uppercase text-muted small fw-semibold d-block">Company Name</label>
              <div className="fw-bold text-slate-900">
                {company?.companyName || profile?.companyName || 'SmartProcure Platform'}
              </div>
            </div>

            <div className="col-12 col-sm-6">
              <label className="text-uppercase text-muted small fw-semibold d-block">Official Email</label>
              <div className="fw-medium text-dark">{company?.email || 'N/A'}</div>
            </div>

            <div className="col-12 col-sm-6">
              <label className="text-uppercase text-muted small fw-semibold d-block">Phone</label>
              <div className="fw-medium text-dark">{company?.phone || 'N/A'}</div>
            </div>

            <div className="col-12 col-sm-6">
              <label className="text-uppercase text-muted small fw-semibold d-block">Address</label>
              <div className="fw-medium text-dark">{company?.address || 'N/A'}</div>
            </div>
          </div>
        </div>
      </div>

      {/* Edit Profile Modal */}
      <UserFormModal
        show={showEditModal}
        editUser={profile}
        onClose={() => setShowEditModal(false)}
        onSuccess={() => {
          setShowEditModal(false);
          fetchProfileData();
        }}
      />
    </div>
  );
};

export default ProfilePage;

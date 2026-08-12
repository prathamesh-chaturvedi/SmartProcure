import axiosClient from './axiosClient';

export const approvalApi = {
  submitProcurementCase: (csId) => axiosClient.patch(`/approvals/${csId}/submit`),
  getPendingProcurementCases: (userId) => axiosClient.get(`/approvals/pending-approval/${userId || 0}`),
  getApprovalHistory: (csId) => axiosClient.get(`/approvals/history/${csId}`),
  approveProcurementCase: (csId, data) => axiosClient.patch(`/approvals/${csId}/approve`, data),
  rejectProcurementCase: (csId, data) => axiosClient.patch(`/approvals/${csId}/reject`, data),
};

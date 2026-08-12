import axiosClient from './axiosClient';

export const matrixApi = {
  getApprovalMatrix: (matrixId) => axiosClient.get(`/approval-matrices/${matrixId}`),
  getApprovalMatricesByCompany: (companyId) => axiosClient.get(`/approval-matrices/company/${companyId}`),
  addApprovalMatrix: (data) => axiosClient.post('/approval-matrices', data),
  updateApprovalMatrix: (matrixId, data) => axiosClient.put(`/approval-matrices/${matrixId}`, data),
  deleteApprovalMatrix: (matrixId) => axiosClient.delete(`/approval-matrices/${matrixId}`),
};

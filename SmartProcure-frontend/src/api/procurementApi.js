import axiosClient from './axiosClient';

export const procurementApi = {
  getProcurementCases: (params) => axiosClient.get('/procurement-cases', { params }),
  getProcurementCaseById: (csId) => axiosClient.get(`/procurement-cases/${csId}`),
  createProcurementCase: (data) => axiosClient.post('/procurement-cases', data),
  updateProcurementCase: (csId, data) => axiosClient.put(`/procurement-cases/${csId}`, data),
  deleteProcurementCase: (csId) => axiosClient.delete(`/procurement-cases/${csId}`),
  downloadCsPdf: (csId) => axiosClient.get(`/procurement-cases/${csId}/download-cs`, { responseType: 'blob' }),
};

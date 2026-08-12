import axiosClient from './axiosClient';

export const companyApi = {
  getOwnCompany: () => axiosClient.get('/companies/me'),
  getCompanyById: (companyId) => axiosClient.get(`/companies/${companyId}`),
  getCompanies: (params) => axiosClient.get('/companies', { params }),
  addCompany: (data) => axiosClient.post('/companies', data),
  createCompanyAdmin: (companyId, data) => axiosClient.post(`/companies/${companyId}/admin`, data),
  updateCompany: (companyId, data) => axiosClient.put(`/companies/${companyId}`, data),
  deleteCompany: (companyId) => axiosClient.delete(`/companies/${companyId}`),
};

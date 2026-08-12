import axiosClient from './axiosClient';

export const vendorQuoteApi = {
  getRankedVendorQuotes: (csId) => axiosClient.get(`/vendor-quotes/procurement-case/${csId}`),
  getVendorQuote: (quoteId, csId) => axiosClient.get(`/vendor-quotes/${quoteId}/case/${csId}`),
  addVendorQuote: (data) => axiosClient.post('/vendor-quotes', data),
  updateVendorQuote: (quoteId, data) => axiosClient.put(`/vendor-quotes/${quoteId}`, data),
  deleteVendorQuote: (quoteId) => axiosClient.delete(`/vendor-quotes/${quoteId}`),
  uploadQuotePdf: (quoteId, formData) => axiosClient.post(`/vendor-quotes/${quoteId}/pdf`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  getQuotePdf: (quoteId) => axiosClient.get(`/vendor-quotes/${quoteId}/pdf`, { responseType: 'blob' }),
};

import axiosClient from './axiosClient';

export const userApi = {
  getProfile: (userId) => axiosClient.get('/users', { params: { userId } }),
  getUsers: (params) => axiosClient.get('/users/list', { params }),
  addUser: (data) => axiosClient.post('/users', data),
  updateUser: (userId, data) => axiosClient.put('/users', data, { params: { userId } }),
  deleteUser: (userId) => axiosClient.delete(`/users/${userId}`),
};

import axiosClient from './axiosClient';

export const authApi = {
  login: (data) => axiosClient.post('/auth/login', data),
  changePassword: (data) => axiosClient.patch('/auth/change-password', data),
};

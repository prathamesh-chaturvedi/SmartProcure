import React, { createContext, useState, useEffect } from 'react';
import { ROLES } from '../utils/constants';

export const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem('sp_token') || null);
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('sp_user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [loading, setLoading] = useState(false);

  const login = (authResponse) => {
    const { token: jwtToken, userId, firstName, userRole, companyId } = authResponse;
    const userData = { userId, firstName, userRole, companyId };
    
    localStorage.setItem('sp_token', jwtToken);
    localStorage.setItem('sp_user', JSON.stringify(userData));
    
    setToken(jwtToken);
    setUser(userData);
  };

  const logout = () => {
    localStorage.removeItem('sp_token');
    localStorage.removeItem('sp_user');
    setToken(null);
    setUser(null);
  };

  const hasRole = (allowedRoles = []) => {
    if (!user || !user.userRole) return false;
    if (allowedRoles.length === 0) return true;
    return allowedRoles.includes(user.userRole);
  };

  const isAuthenticated = !!token && !!user;
  const isMasterAdmin = user?.userRole === ROLES.MASTER_ADMIN;
  const isAdmin = user?.userRole === ROLES.ADMIN;
  const isManager = user?.userRole === ROLES.MANAGER;
  const isEmployee = user?.userRole === ROLES.EMPLOYEE;

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        loading,
        isAuthenticated,
        isMasterAdmin,
        isAdmin,
        isManager,
        isEmployee,
        login,
        logout,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

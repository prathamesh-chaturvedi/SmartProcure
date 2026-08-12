import { ROLE_LABELS, DESIGNATION_LABELS } from './constants';

export const formatCurrency = (amount) => {
  if (amount === null || amount === undefined || isNaN(amount)) return '₹0.00';
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2,
  }).format(amount);
};

export const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  } catch (e) {
    return dateString;
  }
};

export const formatRole = (role) => {
  return ROLE_LABELS[role] || role || 'User';
};

export const formatDesignation = (designation) => {
  return DESIGNATION_LABELS[designation] || designation || 'Staff';
};

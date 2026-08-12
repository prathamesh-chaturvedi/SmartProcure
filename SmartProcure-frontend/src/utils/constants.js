export const ROLES = {
  MASTER_ADMIN: 'MASTER_ADMIN',
  ADMIN: 'ADMIN',
  MANAGER: 'MANAGER',
  EMPLOYEE: 'EMPLOYEE', // Also referred to as PROCUREMENT_EXECUTIVE
};

export const ROLE_LABELS = {
  MASTER_ADMIN: 'Master Admin',
  ADMIN: 'Company Admin',
  MANAGER: 'Manager',
  EMPLOYEE: 'Procurement Executive',
};

export const DESIGNATIONS = {
  SYSTEM_ADMINISTRATOR: 'SYSTEM_ADMINISTRATOR',
  PROCUREMENT_EXECUTIVE: 'PROCUREMENT_EXECUTIVE',
  PROCUREMENT_MANAGER: 'PROCUREMENT_MANAGER',
  SENIOR_MANAGER: 'SENIOR_MANAGER',
  GENERAL_MANAGER: 'GENERAL_MANAGER',
  DIRECTOR: 'DIRECTOR',
};

export const DESIGNATION_LABELS = {
  SYSTEM_ADMINISTRATOR: 'System Administrator',
  PROCUREMENT_EXECUTIVE: 'Procurement Executive',
  PROCUREMENT_MANAGER: 'Procurement Manager',
  SENIOR_MANAGER: 'Senior Manager',
  GENERAL_MANAGER: 'General Manager',
  DIRECTOR: 'Director',
};

export const PROCUREMENT_STATUS = {
  DRAFT: 'DRAFT',
  SUBMITTED: 'SUBMITTED',
  UNDER_REVIEW: 'UNDER_REVIEW',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
};

export const ACTION_TYPES = {
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
};

export const STATUS_BADGES = {
  DRAFT: { label: 'Draft', bgClass: 'bg-secondary' },
  SUBMITTED: { label: 'Submitted', bgClass: 'bg-warning text-dark' },
  UNDER_REVIEW: { label: 'Under Review', bgClass: 'bg-warning text-dark' },
  APPROVED: { label: 'Approved', bgClass: 'bg-success' },
  REJECTED: { label: 'Rejected', bgClass: 'bg-danger' },
};

export type Role = 'PLATFORM_ADMIN' | 'TENANT_ADMIN' | 'SUPPORT_AGENT' | 'CUSTOMER';

export type TenantStatus = 'ACTIVE' | 'SUSPENDED' | 'INACTIVE';

export interface User {
  id: number;
  tenantId: number | null;
  fullName: string;
  email: string;
  role: Role;
  active: boolean;
  createdAt: string;
}

export interface Tenant {
  id: number;
  name: string;
  slug: string;
  status: TenantStatus;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
  tenant: Tenant | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  tenantName: string;
  tenantSlug: string;
  role?: Role;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: String;
  message: string;
  path: string;
}

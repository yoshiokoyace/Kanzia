export type TransactionType = 'INCOME' | 'EXPENSE';

export interface Transaction {
  id: number;
  type: TransactionType;
  amount: number;
  category: string;
  note: string;
  date: string; // ISO string e.g. "2026-09-02"
  timestamp: number;
}

export interface Investment {
  id: number;
  stock: string; // Ticker e.g. "NVDA"
  amount: number;
  date: string;
  timestamp: number;
}

export interface Stock {
  ticker: string;
  name: string;
}

export interface User {
  id?: number;
  name: string;
  email: string;
  passwordHash: string;
}

export type NavTab = 'home' | 'stats' | 'investments';

export type AuthMode = 'LOGIN' | 'REGISTER' | 'FORGOT_PASSWORD';

export interface CategoryMeta {
  name: string;
  color: string;
  iconName: string;
}

export const CATEGORIES: Record<string, { color: string; bg: string }> = {
  Food: { color: '#F59E0B', bg: 'rgba(245, 158, 11, 0.15)' },
  Transport: { color: '#3B82F6', bg: 'rgba(59, 130, 246, 0.15)' },
  Shopping: { color: '#EC4899', bg: 'rgba(236, 72, 153, 0.15)' },
  Entertainment: { color: '#8B5CF6', bg: 'rgba(139, 92, 246, 0.15)' },
  Bills: { color: '#EF4444', bg: 'rgba(239, 68, 68, 0.15)' },
  Salary: { color: '#10B981', bg: 'rgba(16, 185, 129, 0.15)' },
  Investment: { color: '#6366F1', bg: 'rgba(99, 102, 241, 0.15)' },
  Other: { color: '#64748B', bg: 'rgba(100, 116, 139, 0.15)' },
};

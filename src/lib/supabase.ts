import { Transaction, Investment, Stock, User } from '../types';

// Safely get Supabase credentials
const getSupabaseConfig = () => {
  const url =
    (typeof process !== 'undefined' && process.env?.SUPABASE_URL) ||
    import.meta.env.VITE_SUPABASE_URL ||
    'https://wsrzdnxzjjgqsrsaohgc.supabase.co';

  const key =
    (typeof process !== 'undefined' && process.env?.SUPABASE_ANON_KEY) ||
    import.meta.env.VITE_SUPABASE_ANON_KEY ||
    'sb_secret__X1aOBmgfgecEAM6Y7vikQ_lWCD7GKd';

  return { url: url.replace(/\/$/, ''), key };
};

const { url: SUPABASE_URL, key: SUPABASE_KEY } = getSupabaseConfig();

const getHeaders = () => ({
  apikey: SUPABASE_KEY,
  Authorization: `Bearer ${SUPABASE_KEY}`,
  'Content-Type': 'application/json',
  Prefer: 'return=representation',
});

export interface SupabaseUserRow {
  id: number;
  name: string;
  email: string;
  password_hash: string;
  created_at?: string;
}

export interface SupabaseTxRow {
  id: number;
  type: 'INCOME' | 'EXPENSE';
  amount: number;
  category: string;
  note: string;
  date: string;
  timestamp: number;
}

export interface SupabaseInvRow {
  id: number;
  stock: string;
  amount: number;
  date: string;
  timestamp: number;
}

export interface SupabaseStockRow {
  ticker: string;
  name: string;
}

export class SupabaseService {
  static get isConfigured(): boolean {
    return Boolean(SUPABASE_URL && SUPABASE_KEY);
  }

  // USER AUTHENTICATION & QUERIES
  static async findUserByEmail(email: string): Promise<User | null> {
    try {
      const cleanEmail = email.trim().toLowerCase();
      const res = await fetch(
        `${SUPABASE_URL}/rest/v1/users?email=ilike.${encodeURIComponent(cleanEmail)}&select=*`,
        { headers: getHeaders() }
      );
      if (!res.ok) {
        console.warn('Supabase findUserByEmail response not ok:', res.status);
        return null;
      }
      const data: SupabaseUserRow[] = await res.json();
      if (data && data.length > 0) {
        const u = data[0];
        return {
          id: u.id,
          name: u.name,
          email: u.email,
          passwordHash: u.password_hash,
        };
      }
      return null;
    } catch (err) {
      console.error('Supabase findUserByEmail error:', err);
      return null;
    }
  }

  static async createUser(name: string, email: string, passwordHash: string): Promise<User | null> {
    try {
      const cleanEmail = email.trim().toLowerCase();
      const res = await fetch(`${SUPABASE_URL}/rest/v1/users`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify({
          name: name.trim(),
          email: cleanEmail,
          password_hash: passwordHash.trim(),
        }),
      });
      if (!res.ok) {
        const errText = await res.text();
        console.warn('Supabase createUser failed:', res.status, errText);
        return null;
      }
      const created: SupabaseUserRow[] = await res.json();
      if (created && created.length > 0) {
        const u = created[0];
        return {
          id: u.id,
          name: u.name,
          email: u.email,
          passwordHash: u.password_hash,
        };
      }
      return null;
    } catch (err) {
      console.error('Supabase createUser network error:', err);
      return null;
    }
  }

  static async updatePassword(email: string, newPassword: string): Promise<boolean> {
    try {
      const cleanEmail = email.trim().toLowerCase();
      const res = await fetch(
        `${SUPABASE_URL}/rest/v1/users?email=ilike.${encodeURIComponent(cleanEmail)}`,
        {
          method: 'PATCH',
          headers: getHeaders(),
          body: JSON.stringify({
            password_hash: newPassword.trim(),
          }),
        }
      );
      return res.ok;
    } catch (err) {
      console.error('Supabase updatePassword error:', err);
      return false;
    }
  }

  // TRANSACTIONS
  static async getTransactions(): Promise<Transaction[]> {
    try {
      const res = await fetch(
        `${SUPABASE_URL}/rest/v1/transactions?select=*&order=timestamp.desc`,
        { headers: getHeaders() }
      );
      if (!res.ok) return [];
      const data: SupabaseTxRow[] = await res.json();
      return data.map(tx => ({
        id: tx.id,
        type: tx.type,
        amount: Number(tx.amount),
        category: tx.category || 'Other',
        note: tx.note || '',
        date: tx.date,
        timestamp: Number(tx.timestamp),
      }));
    } catch (err) {
      console.error('Supabase getTransactions error:', err);
      return [];
    }
  }

  static async addTransaction(tx: Transaction): Promise<boolean> {
    try {
      const res = await fetch(`${SUPABASE_URL}/rest/v1/transactions`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify({
          id: tx.id,
          type: tx.type,
          amount: tx.amount,
          category: tx.category,
          note: tx.note || '',
          date: tx.date,
          timestamp: tx.timestamp,
        }),
      });
      return res.ok;
    } catch (err) {
      console.error('Supabase addTransaction error:', err);
      return false;
    }
  }

  static async deleteTransaction(id: number): Promise<boolean> {
    try {
      const res = await fetch(`${SUPABASE_URL}/rest/v1/transactions?id=eq.${id}`, {
        method: 'DELETE',
        headers: getHeaders(),
      });
      return res.ok;
    } catch (err) {
      console.error('Supabase deleteTransaction error:', err);
      return false;
    }
  }

  // INVESTMENTS
  static async getInvestments(): Promise<Investment[]> {
    try {
      const res = await fetch(
        `${SUPABASE_URL}/rest/v1/investments?select=*&order=timestamp.desc`,
        { headers: getHeaders() }
      );
      if (!res.ok) return [];
      const data: SupabaseInvRow[] = await res.json();
      return data.map(inv => ({
        id: inv.id,
        stock: inv.stock,
        amount: Number(inv.amount),
        date: inv.date,
        timestamp: Number(inv.timestamp),
      }));
    } catch (err) {
      console.error('Supabase getInvestments error:', err);
      return [];
    }
  }

  static async addInvestment(inv: Investment): Promise<boolean> {
    try {
      const res = await fetch(`${SUPABASE_URL}/rest/v1/investments`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify({
          id: inv.id,
          stock: inv.stock,
          amount: inv.amount,
          date: inv.date,
          timestamp: inv.timestamp,
        }),
      });
      return res.ok;
    } catch (err) {
      console.error('Supabase addInvestment error:', err);
      return false;
    }
  }

  static async deleteInvestment(id: number): Promise<boolean> {
    try {
      const res = await fetch(`${SUPABASE_URL}/rest/v1/investments?id=eq.${id}`, {
        method: 'DELETE',
        headers: getHeaders(),
      });
      return res.ok;
    } catch (err) {
      console.error('Supabase deleteInvestment error:', err);
      return false;
    }
  }

  // STOCKS
  static async getStocks(): Promise<Stock[]> {
    try {
      const res = await fetch(`${SUPABASE_URL}/rest/v1/stocks?select=*`, {
        headers: getHeaders(),
      });
      if (!res.ok) return [];
      const data: SupabaseStockRow[] = await res.json();
      return data.map(s => ({
        ticker: s.ticker,
        name: s.name,
      }));
    } catch (err) {
      console.error('Supabase getStocks error:', err);
      return [];
    }
  }

  static async addStock(stock: Stock): Promise<boolean> {
    try {
      const res = await fetch(`${SUPABASE_URL}/rest/v1/stocks`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify({
          ticker: stock.ticker.toUpperCase(),
          name: stock.name.trim(),
        }),
      });
      return res.ok;
    } catch (err) {
      console.error('Supabase addStock error:', err);
      return false;
    }
  }

  static async deleteStock(ticker: string): Promise<boolean> {
    try {
      const res = await fetch(
        `${SUPABASE_URL}/rest/v1/stocks?ticker=eq.${encodeURIComponent(ticker.toUpperCase())}`,
        {
          method: 'DELETE',
          headers: getHeaders(),
        }
      );
      return res.ok;
    } catch (err) {
      console.error('Supabase deleteStock error:', err);
      return false;
    }
  }
}

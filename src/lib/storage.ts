import { Transaction, Investment, Stock, User } from '../types';
import { SupabaseService } from './supabase';

const STORAGE_KEYS = {
  TRANSACTIONS: 'kanzia_transactions_v1',
  INVESTMENTS: 'kanzia_investments_v1',
  STOCKS: 'kanzia_stocks_v1',
  CURRENT_USER: 'kanzia_current_user_v1',
  SAVED_EMAIL: 'kanzia_saved_email_v1',
  REGISTERED_USERS: 'kanzia_users_v1',
  LAST_SYNC: 'kanzia_last_sync_v1',
  HAS_LAUNCHED: 'kanzia_has_launched_v1',
};

const DEFAULT_STOCKS: Stock[] = [
  { ticker: 'NVDA', name: 'NVIDIA' },
  { ticker: 'AAPL', name: 'Apple Inc.' },
  { ticker: 'MSFT', name: 'Microsoft Corporation' },
  { ticker: 'AMZN', name: 'Amazon.com, Inc.' },
  { ticker: 'GOOGL', name: 'Alphabet Inc.' },
  { ticker: 'TSLA', name: 'Tesla, Inc.' },
];

function getSeedTransactions(): Transaction[] {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth();

  const pad = (n: number) => String(n).padStart(2, '0');
  const makeDate = (day: number) => `${year}-${pad(month + 1)}-${pad(day)}`;

  return [
    {
      id: 1,
      type: 'INCOME',
      amount: 4500.0,
      category: 'Salary',
      note: 'Monthly Tech Salary Deposit',
      date: makeDate(1),
      timestamp: new Date(year, month, 1, 9, 30).getTime(),
    },
    {
      id: 2,
      type: 'EXPENSE',
      amount: 1450.0,
      category: 'Bills',
      note: 'Apartment Rent & Utilities',
      date: makeDate(2),
      timestamp: new Date(year, month, 2, 10, 15).getTime(),
    },
    {
      id: 3,
      type: 'EXPENSE',
      amount: 142.8,
      category: 'Food',
      note: "Trader Joe's Grocery Haul",
      date: makeDate(5),
      timestamp: new Date(year, month, 5, 17, 45).getTime(),
    },
    {
      id: 4,
      type: 'EXPENSE',
      amount: 45.0,
      category: 'Transport',
      note: 'Fuel & Highway Transit Pass',
      date: makeDate(8),
      timestamp: new Date(year, month, 8, 8, 20).getTime(),
    },
    {
      id: 5,
      type: 'EXPENSE',
      amount: 89.99,
      category: 'Shopping',
      note: 'Ergonomic Desk Accessories',
      date: makeDate(11),
      timestamp: new Date(year, month, 11, 14, 10).getTime(),
    },
    {
      id: 6,
      type: 'INCOME',
      amount: 650.0,
      category: 'Other',
      note: 'Freelance Advisory Consultation',
      date: makeDate(14),
      timestamp: new Date(year, month, 14, 16, 0).getTime(),
    },
    {
      id: 7,
      type: 'EXPENSE',
      amount: 68.5,
      category: 'Entertainment',
      note: 'Cinema & Weekend Dinner',
      date: makeDate(17),
      timestamp: new Date(year, month, 17, 20, 15).getTime(),
    },
    {
      id: 8,
      type: 'EXPENSE',
      amount: 250.0,
      category: 'Investment',
      note: 'Automated Roth IRA Stock Allocation',
      date: makeDate(20),
      timestamp: new Date(year, month, 20, 11, 0).getTime(),
    },
  ];
}

function getSeedInvestments(): Investment[] {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth();
  const pad = (n: number) => String(n).padStart(2, '0');

  return [
    {
      id: 1,
      stock: 'NVDA',
      amount: 100.0,
      date: `${year}-${pad(month + 1)}-03`,
      timestamp: new Date(year, month, 3, 10, 0).getTime(),
    },
  ];
}

export class StorageService {
  // TRANSACTIONS
  static getTransactions(): Transaction[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.TRANSACTIONS);
      if (!data) {
        const seed = getSeedTransactions();
        localStorage.setItem(STORAGE_KEYS.TRANSACTIONS, JSON.stringify(seed));
        return seed;
      }
      return JSON.parse(data);
    } catch {
      return getSeedTransactions();
    }
  }

  static saveTransactions(transactions: Transaction[]) {
    localStorage.setItem(STORAGE_KEYS.TRANSACTIONS, JSON.stringify(transactions));
  }

  static addTransaction(tx: Omit<Transaction, 'id'>): Transaction {
    const list = this.getTransactions();
    const newId = list.length > 0 ? Math.max(...list.map(t => t.id)) + 1 : 1;
    const newTx: Transaction = { ...tx, id: newId };
    list.unshift(newTx);
    this.saveTransactions(list);

    // Sync with Supabase asynchronously
    SupabaseService.addTransaction(newTx).catch(err =>
      console.warn('Background Supabase tx sync failed:', err)
    );

    return newTx;
  }

  static deleteTransaction(id: number): Transaction[] {
    const list = this.getTransactions().filter(t => t.id !== id);
    this.saveTransactions(list);

    // Sync deletion with Supabase asynchronously
    SupabaseService.deleteTransaction(id).catch(err =>
      console.warn('Background Supabase delete tx failed:', err)
    );

    return list;
  }

  // INVESTMENTS
  static getInvestments(): Investment[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.INVESTMENTS);
      if (!data) {
        const seed = getSeedInvestments();
        localStorage.setItem(STORAGE_KEYS.INVESTMENTS, JSON.stringify(seed));
        return seed;
      }
      return JSON.parse(data);
    } catch {
      return getSeedInvestments();
    }
  }

  static saveInvestments(investments: Investment[]) {
    localStorage.setItem(STORAGE_KEYS.INVESTMENTS, JSON.stringify(investments));
  }

  static addInvestment(inv: Omit<Investment, 'id'>): Investment {
    const list = this.getInvestments();
    const newId = list.length > 0 ? Math.max(...list.map(i => i.id)) + 1 : 1;
    const newInv: Investment = { ...inv, id: newId };
    list.unshift(newInv);
    this.saveInvestments(list);

    // Sync with Supabase asynchronously
    SupabaseService.addInvestment(newInv).catch(err =>
      console.warn('Background Supabase inv sync failed:', err)
    );

    return newInv;
  }

  static deleteInvestment(id: number): Investment[] {
    const list = this.getInvestments().filter(i => i.id !== id);
    this.saveInvestments(list);

    // Sync deletion with Supabase asynchronously
    SupabaseService.deleteInvestment(id).catch(err =>
      console.warn('Background Supabase delete inv failed:', err)
    );

    return list;
  }

  // STOCKS
  static getStocks(): Stock[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.STOCKS);
      if (!data) {
        localStorage.setItem(STORAGE_KEYS.STOCKS, JSON.stringify(DEFAULT_STOCKS));
        return DEFAULT_STOCKS;
      }
      return JSON.parse(data);
    } catch {
      return DEFAULT_STOCKS;
    }
  }

  static saveStocks(stocks: Stock[]) {
    localStorage.setItem(STORAGE_KEYS.STOCKS, JSON.stringify(stocks));
  }

  static addStock(stock: Stock): Stock[] {
    const list = this.getStocks();
    const exists = list.some(s => s.ticker.toUpperCase() === stock.ticker.toUpperCase());
    if (!exists) {
      const newStock: Stock = {
        ticker: stock.ticker.toUpperCase(),
        name: stock.name.trim(),
      };
      list.push(newStock);
      this.saveStocks(list);

      // Sync with Supabase asynchronously
      SupabaseService.addStock(newStock).catch(err =>
        console.warn('Background Supabase stock sync failed:', err)
      );
    }
    return list;
  }

  static deleteStock(ticker: string): Stock[] {
    const cleanTicker = ticker.toUpperCase();
    const list = this.getStocks().filter(s => s.ticker.toUpperCase() !== cleanTicker);
    this.saveStocks(list);

    // Sync deletion with Supabase asynchronously
    SupabaseService.deleteStock(cleanTicker).catch(err =>
      console.warn('Background Supabase delete stock failed:', err)
    );

    return list;
  }

  // USER & AUTHENTICATION
  static getUsers(): User[] {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.REGISTERED_USERS);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  }

  static saveUsers(users: User[]) {
    localStorage.setItem(STORAGE_KEYS.REGISTERED_USERS, JSON.stringify(users));
  }

  static getCurrentUser(): User | null {
    try {
      const data = localStorage.getItem(STORAGE_KEYS.CURRENT_USER);
      return data ? JSON.parse(data) : null;
    } catch {
      return null;
    }
  }

  static getSavedEmail(): string {
    return localStorage.getItem(STORAGE_KEYS.SAVED_EMAIL) || '';
  }

  // ASYNC LOGIN: Queries Supabase database first, then checks local cache
  static async login(
    email: string,
    pass: string
  ): Promise<{ success: boolean; user?: User; error?: string }> {
    const cleanEmail = email.trim().toLowerCase();
    const cleanPass = pass.trim();

    // 1. Try querying Supabase directly
    try {
      const supabaseUser = await SupabaseService.findUserByEmail(cleanEmail);
      if (supabaseUser) {
        if (supabaseUser.passwordHash !== cleanPass) {
          return { success: false, error: 'Incorrect password. Please try again.' };
        }

        // Cache user in local storage
        localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(supabaseUser));
        localStorage.setItem(STORAGE_KEYS.SAVED_EMAIL, supabaseUser.email);
        localStorage.setItem(STORAGE_KEYS.HAS_LAUNCHED, 'true');

        // Update local users list cache
        const localUsers = this.getUsers().filter(
          u => u.email.toLowerCase() !== supabaseUser.email.toLowerCase()
        );
        localUsers.push(supabaseUser);
        this.saveUsers(localUsers);

        return { success: true, user: supabaseUser };
      }
    } catch (err) {
      console.warn('Supabase login check failed, falling back to local cache:', err);
    }

    // 2. Check local cache (offline mode or previously cached users)
    const users = this.getUsers();
    const localUser = users.find(u => u.email.toLowerCase() === cleanEmail);
    if (localUser) {
      if (localUser.passwordHash !== cleanPass) {
        return { success: false, error: 'Incorrect password. Please try again.' };
      }
      localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(localUser));
      localStorage.setItem(STORAGE_KEYS.SAVED_EMAIL, localUser.email);
      localStorage.setItem(STORAGE_KEYS.HAS_LAUNCHED, 'true');
      return { success: true, user: localUser };
    }

    // 3. Neither Supabase nor local storage has an account with this email
    return {
      success: false,
      error: 'No account found with this email. Please check your spelling or sign up.',
    };
  }

  // ASYNC REGISTER: Creates account in Supabase and saves locally
  static async register(
    name: string,
    email: string,
    pass: string
  ): Promise<{ success: boolean; user?: User; error?: string }> {
    const cleanEmail = email.trim().toLowerCase();
    const cleanName = name.trim();
    const cleanPass = pass.trim();

    // 1. Check if user already exists in Supabase
    try {
      const existingSupabase = await SupabaseService.findUserByEmail(cleanEmail);
      if (existingSupabase) {
        return { success: false, error: 'An account with this email already exists.' };
      }
    } catch {
      // Continue if network fails
    }

    // Check local cache
    const users = this.getUsers();
    if (users.some(u => u.email.toLowerCase() === cleanEmail)) {
      return { success: false, error: 'An account with this email already exists.' };
    }

    // 2. Create in Supabase
    let newUser: User | null = null;
    try {
      newUser = await SupabaseService.createUser(cleanName, cleanEmail, cleanPass);
    } catch (err) {
      console.warn('Failed to insert user into Supabase directly:', err);
    }

    if (!newUser) {
      // Fallback: create local user object
      newUser = {
        id: Date.now(),
        name: cleanName,
        email: cleanEmail,
        passwordHash: cleanPass,
      };
    }

    users.push(newUser);
    this.saveUsers(users);
    localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(newUser));
    localStorage.setItem(STORAGE_KEYS.SAVED_EMAIL, newUser.email);
    localStorage.setItem(STORAGE_KEYS.HAS_LAUNCHED, 'true');

    return { success: true, user: newUser };
  }

  // ASYNC RESET PASSWORD: Updates Supabase and local cache
  static async resetPassword(
    email: string,
    newPass: string
  ): Promise<{ success: boolean; error?: string }> {
    const cleanEmail = email.trim().toLowerCase();
    const cleanPass = newPass.trim();

    let foundInSupabase = false;
    try {
      const user = await SupabaseService.findUserByEmail(cleanEmail);
      if (user) {
        foundInSupabase = true;
        await SupabaseService.updatePassword(cleanEmail, cleanPass);
      }
    } catch {
      // fallback
    }

    const users = this.getUsers();
    const idx = users.findIndex(u => u.email.toLowerCase() === cleanEmail);
    if (!foundInSupabase && idx === -1) {
      return { success: false, error: 'No account found with this email.' };
    }

    if (idx !== -1) {
      users[idx].passwordHash = cleanPass;
      this.saveUsers(users);
    }

    // Also update current user if matching
    const current = this.getCurrentUser();
    if (current && current.email.toLowerCase() === cleanEmail) {
      current.passwordHash = cleanPass;
      localStorage.setItem(STORAGE_KEYS.CURRENT_USER, JSON.stringify(current));
    }

    return { success: true };
  }

  // SYNC WITH SUPABASE: Fetches latest data from database
  static async syncWithSupabase(): Promise<{
    transactions?: Transaction[];
    investments?: Investment[];
    stocks?: Stock[];
  }> {
    try {
      const [remoteTx, remoteInv, remoteStocks] = await Promise.all([
        SupabaseService.getTransactions(),
        SupabaseService.getInvestments(),
        SupabaseService.getStocks(),
      ]);

      const result: {
        transactions?: Transaction[];
        investments?: Investment[];
        stocks?: Stock[];
      } = {};

      if (remoteTx && remoteTx.length > 0) {
        this.saveTransactions(remoteTx);
        result.transactions = remoteTx;
      } else {
        result.transactions = this.getTransactions();
      }

      if (remoteInv && remoteInv.length > 0) {
        this.saveInvestments(remoteInv);
        result.investments = remoteInv;
      } else {
        result.investments = this.getInvestments();
      }

      if (remoteStocks && remoteStocks.length > 0) {
        // Merge with defaults to ensure rich list
        const existingTickers = new Set(remoteStocks.map(s => s.ticker.toUpperCase()));
        const merged = [...remoteStocks];
        for (const def of DEFAULT_STOCKS) {
          if (!existingTickers.has(def.ticker.toUpperCase())) {
            merged.push(def);
          }
        }
        this.saveStocks(merged);
        result.stocks = merged;
      } else {
        result.stocks = this.getStocks();
      }

      localStorage.setItem(STORAGE_KEYS.LAST_SYNC, new Date().toISOString());
      return result;
    } catch (err) {
      console.warn('Sync with Supabase encountered error:', err);
      return {
        transactions: this.getTransactions(),
        investments: this.getInvestments(),
        stocks: this.getStocks(),
      };
    }
  }

  static logout() {
    localStorage.removeItem(STORAGE_KEYS.CURRENT_USER);
  }

  static resetAllData() {
    localStorage.removeItem(STORAGE_KEYS.TRANSACTIONS);
    localStorage.removeItem(STORAGE_KEYS.INVESTMENTS);
    localStorage.removeItem(STORAGE_KEYS.STOCKS);
    localStorage.removeItem(STORAGE_KEYS.LAST_SYNC);
  }
}

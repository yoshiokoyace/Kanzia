import React, { useState, useEffect, useCallback } from 'react';
import { NavTab, Transaction, Investment, Stock, User, TransactionType } from './types';
import { StorageService } from './lib/storage';
import { useInactivityLock } from './lib/inactivity';
import { Navbar } from './components/Navbar';
import { BottomNav } from './components/BottomNav';
import { HomeScreen } from './components/HomeScreen';
import { StatsScreen } from './components/StatsScreen';
import { InvestmentsScreen } from './components/InvestmentsScreen';
import { AddTransactionSheet } from './components/AddTransactionSheet';
import { AddInvestmentModal } from './components/AddInvestmentModal';
import { AddStockDialog } from './components/AddStockDialog';
import { ProfileSheet } from './components/ProfileSheet';
import { AuthModal } from './components/AuthModal';
import { SessionExpiredModal } from './components/SessionExpiredModal';
import { PullToRefresh } from './components/PullToRefresh';

export const App: React.FC = () => {
  // Navigation: limited strictly to mobile and web view
  const [activeTab, setActiveTab] = useState<NavTab>('home');
  const [currentDate, setCurrentDate] = useState<Date>(() => new Date());

  // Data State
  const [transactions, setTransactions] = useState<Transaction[]>(() => StorageService.getTransactions());
  const [investments, setInvestments] = useState<Investment[]>(() => StorageService.getInvestments());
  const [stocks, setStocks] = useState<Stock[]>(() => StorageService.getStocks());
  const [currentUser, setCurrentUser] = useState<User | null>(() => StorageService.getCurrentUser());

  // User Authentication State
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(() => !!StorageService.getCurrentUser());
  const [isAuthModalOpen, setIsAuthModalOpen] = useState<boolean>(() => !StorageService.getCurrentUser());

  // Modals & Sheets
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const [isAddTransactionOpen, setIsAddTransactionOpen] = useState(false);
  const [isAddInvestmentOpen, setIsAddInvestmentOpen] = useState(false);
  const [isAddStockOpen, setIsAddStockOpen] = useState(false);
  const [isSessionExpiredOpen, setIsSessionExpiredOpen] = useState(false);
  const [isSyncing, setIsSyncing] = useState(false);
  const [selectedStockForDeposit, setSelectedStockForDeposit] = useState<string | undefined>(undefined);

  // Inactivity auto-lock after 3 minutes: triggers the session expired notice pop-up
  const handleSessionExpired = useCallback(() => {
    setIsLoggedIn(false);
    setIsProfileOpen(false);
    setIsAddTransactionOpen(false);
    setIsAddInvestmentOpen(false);
    setIsAddStockOpen(false);
    setIsAuthModalOpen(false);
    setIsSessionExpiredOpen(true);
  }, []);

  // Manual lock session (from profile)
  const handleManualLockSession = useCallback(() => {
    setIsLoggedIn(false);
    setIsAuthModalOpen(true);
    setIsProfileOpen(false);
  }, []);

  useInactivityLock(isLoggedIn, handleSessionExpired);

  // Initial sync with Supabase database on app start
  useEffect(() => {
    let mounted = true;
    StorageService.syncWithSupabase().then(data => {
      if (!mounted) return;
      if (data.transactions) setTransactions(data.transactions);
      if (data.investments) setInvestments(data.investments);
      if (data.stocks) setStocks(data.stocks);
    });
    return () => {
      mounted = false;
    };
  }, []);

  // Cloud database refresh & state update for gesture refresh
  const handleSync = async () => {
    setIsSyncing(true);
    try {
      const data = await StorageService.syncWithSupabase();
      if (data.transactions) setTransactions(data.transactions);
      if (data.investments) setInvestments(data.investments);
      if (data.stocks) setStocks(data.stocks);
    } catch (e) {
      console.error('Refresh sync failed:', e);
    } finally {
      // Re-read latest storage cache
      setTransactions(StorageService.getTransactions());
      setInvestments(StorageService.getInvestments());
      setStocks(StorageService.getStocks());
      setIsSyncing(false);
    }
  };

  // Month navigation for Stats
  const handlePrevMonth = () => {
    setCurrentDate(prev => new Date(prev.getFullYear(), prev.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setCurrentDate(prev => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
  };

  // Transaction CRUD
  const handleAddTransaction = (data: {
    type: TransactionType;
    amount: number;
    category: string;
    note: string;
    date: string;
  }) => {
    StorageService.addTransaction({
      ...data,
      timestamp: new Date().getTime(),
    });
    setTransactions(StorageService.getTransactions());
  };

  const handleDeleteTransaction = (id: number) => {
    const updated = StorageService.deleteTransaction(id);
    setTransactions(updated);
  };

  // Investment CRUD
  const handleAddInvestment = (data: Omit<Investment, 'id'>) => {
    StorageService.addInvestment(data);
    setInvestments(StorageService.getInvestments());
  };

  const handleDeleteInvestment = (id: number) => {
    const updated = StorageService.deleteInvestment(id);
    setInvestments(updated);
  };

  // Stock CRUD: proceedToDeposit ensures that after adding a new stock it proceeds to add the amount
  const handleAddStock = (stock: Stock, proceedToDeposit: boolean = true) => {
    const updated = StorageService.addStock(stock);
    setStocks(updated);
    if (proceedToDeposit) {
      setSelectedStockForDeposit(stock.ticker);
      setIsAddInvestmentOpen(true);
    }
  };

  const handleDeleteStock = (ticker: string) => {
    const updated = StorageService.deleteStock(ticker);
    setStocks(updated);
  };

  // Auth actions
  const handleLogin = async (email: string, pass: string) => {
    const res = await StorageService.login(email, pass);
    if (res.success && res.user) {
      setCurrentUser(res.user);
      setIsLoggedIn(true);
      setIsAuthModalOpen(false);
      // Sync latest cloud data for the logged in user
      StorageService.syncWithSupabase().then(data => {
        if (data.transactions) setTransactions(data.transactions);
        if (data.investments) setInvestments(data.investments);
        if (data.stocks) setStocks(data.stocks);
      });
    }
    return res;
  };

  const handleRegister = async (name: string, email: string, pass: string) => {
    const res = await StorageService.register(name, email, pass);
    if (res.success && res.user) {
      setCurrentUser(res.user);
      setIsLoggedIn(true);
      setIsAuthModalOpen(false);
    }
    return res;
  };

  const handleResetPassword = async (email: string, newPass: string) => {
    return await StorageService.resetPassword(email, newPass);
  };

  const handleLogout = () => {
    StorageService.logout();
    setCurrentUser(null);
    setIsLoggedIn(false);
    setIsAuthModalOpen(true);
  };

  const handleResetAllData = () => {
    StorageService.resetAllData();
    setTransactions(StorageService.getTransactions());
    setInvestments(StorageService.getInvestments());
    setStocks(StorageService.getStocks());
  };

  return (
    <div className="min-h-screen bg-black text-white flex flex-col font-sans selection:bg-white selection:text-black relative overflow-x-hidden">
      {/* Ambient Celestial Orb Glow (monochrome specular aura from reference image) */}
      <div className="fixed top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[550px] sm:w-[750px] h-[350px] sm:h-[450px] bg-white/[0.07] rounded-full blur-[120px] pointer-events-none -z-10" />
      <div className="fixed top-[-80px] left-1/2 -translate-x-1/2 w-[240px] sm:w-[340px] h-[120px] sm:h-[180px] bg-white/[0.12] rounded-full blur-[70px] pointer-events-none -z-10" />

      {/* Top Navbar */}
      <Navbar
        currentUser={currentUser}
        onOpenProfile={() => setIsProfileOpen(true)}
        isSyncing={isSyncing}
      />

      {/* Main Content Area with Pull-to-Refresh Gesture */}
      <PullToRefresh onRefresh={handleSync} isRefreshingExternal={isSyncing}>
        <main id="main-content-view" className="flex-1 max-w-md md:max-w-3xl lg:max-w-4xl w-full mx-auto px-4 pt-4 relative z-10">
          {activeTab === 'home' && (
            <HomeScreen
              transactions={transactions}
              investments={investments}
              currentDate={currentDate}
              onPrevMonth={handlePrevMonth}
              onNextMonth={handleNextMonth}
              onAddTransactionClick={() => setIsAddTransactionOpen(true)}
              onDeleteTransaction={handleDeleteTransaction}
            />
          )}

          {activeTab === 'stats' && (
            <StatsScreen
              transactions={transactions}
              currentDate={currentDate}
              onPrevMonth={handlePrevMonth}
              onNextMonth={handleNextMonth}
            />
          )}

          {activeTab === 'investments' && (
            <InvestmentsScreen
              investments={investments}
              stocks={stocks}
              onOpenAddInvestment={() => setIsAddInvestmentOpen(true)}
              onOpenAddStock={() => setIsAddStockOpen(true)}
              onDeleteInvestment={handleDeleteInvestment}
              onDeleteStock={handleDeleteStock}
            />
          )}
        </main>
      </PullToRefresh>

      {/* Bottom Navigation with Liquid Water Droplet Controls & Thumb Actions */}
      <BottomNav
        activeTab={activeTab}
        onTabChange={setActiveTab}
        onAddTransactionClick={() => setIsAddTransactionOpen(true)}
        onNewDepositClick={() => setIsAddInvestmentOpen(true)}
      />

      {/* Modals & Dialogs */}
      <AddTransactionSheet
        isOpen={isAddTransactionOpen}
        onClose={() => setIsAddTransactionOpen(false)}
        onAdd={handleAddTransaction}
      />

      {/* Add Investment Modal: supports inline stock creation so deposit flow is never exited */}
      <AddInvestmentModal
        isOpen={isAddInvestmentOpen}
        onClose={() => {
          setIsAddInvestmentOpen(false);
          setSelectedStockForDeposit(undefined);
        }}
        stocks={stocks}
        onAdd={handleAddInvestment}
        onSaveStock={(stock) => {
          const updated = StorageService.addStock(stock);
          setStocks(updated);
        }}
        initialStock={selectedStockForDeposit}
      />

      {/* Standalone Add Stock Dialog: option to proceed directly to adding deposit amount */}
      <AddStockDialog
        isOpen={isAddStockOpen}
        onClose={() => setIsAddStockOpen(false)}
        onSave={handleAddStock}
      />

      <ProfileSheet
        isOpen={isProfileOpen}
        onClose={() => setIsProfileOpen(false)}
        currentUser={currentUser}
        onLogout={handleLogout}
        onLockSession={handleManualLockSession}
        onTriggerSessionExpired={handleSessionExpired}
        onResetData={handleResetAllData}
        onSync={handleSync}
        isSyncing={isSyncing}
      />

      {/* Session Expired Inactivity Notice Popup */}
      <SessionExpiredModal
        isOpen={isSessionExpiredOpen}
        currentUser={currentUser}
        onLoginAgain={() => {
          setIsSessionExpiredOpen(false);
          setIsAuthModalOpen(true);
        }}
      />

      <AuthModal
        isOpen={isAuthModalOpen}
        isDismissable={isLoggedIn}
        savedEmail={StorageService.getSavedEmail()}
        onDismiss={() => setIsAuthModalOpen(false)}
        onLogin={handleLogin}
        onRegister={handleRegister}
        onResetPassword={handleResetPassword}
      />
    </div>
  );
};

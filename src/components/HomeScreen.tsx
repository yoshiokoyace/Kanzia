import React, { useState, useMemo } from 'react';
import {
  ChevronDown,
  ArrowUpRight,
  ArrowDownRight,
  TrendingUp,
  Search,
  Trash2,
  Wallet,
  Calendar,
} from 'lucide-react';
import { Transaction, Investment, CATEGORIES } from '../types';
import { formatCurrency, formatDate, formatTime } from '../lib/formatters';

export type TimeframeOption = 'today' | 'week' | 'month' | 'all';

interface HomeScreenProps {
  transactions: Transaction[];
  investments: Investment[];
  currentDate?: Date;
  onPrevMonth?: () => void;
  onNextMonth?: () => void;
  onAddTransactionClick: () => void;
  onDeleteTransaction: (id: number) => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  transactions,
  investments,
  currentDate = new Date(),
  onAddTransactionClick,
  onDeleteTransaction,
}) => {
  const [timeframe, setTimeframe] = useState<TimeframeOption>('month');
  const [selectedCategory, setSelectedCategory] = useState<string>('All');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Helper to check if a date string ("YYYY-MM-DD") falls into the chosen timeframe
  const isInTimeframe = (dateStr: string, tf: TimeframeOption, baseDate: Date) => {
    if (tf === 'all') return true;

    const parts = dateStr.split('-');
    if (parts.length < 3) return true;
    const y = parseInt(parts[0], 10);
    const m = parseInt(parts[1], 10) - 1;
    const d = parseInt(parts[2], 10);

    const txDate = new Date(y, m, d);
    const today = new Date(baseDate.getFullYear(), baseDate.getMonth(), baseDate.getDate());

    switch (tf) {
      case 'today':
        return txDate.getTime() === today.getTime();
      case 'week': {
        const dayOfWeek = today.getDay();
        const startOfWeek = new Date(today);
        startOfWeek.setDate(today.getDate() - dayOfWeek);
        startOfWeek.setHours(0, 0, 0, 0);

        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6);
        endOfWeek.setHours(23, 59, 59, 999);

        return txDate >= startOfWeek && txDate <= endOfWeek;
      }
      case 'month':
        return y === baseDate.getFullYear() && m === baseDate.getMonth();
      default:
        return true;
    }
  };

  // Transactions filtered by active timeframe
  const timeframeTransactions = useMemo(() => {
    return transactions.filter(tx => isInTimeframe(tx.date, timeframe, currentDate));
  }, [transactions, timeframe, currentDate]);

  // Investments filtered by active timeframe
  const timeframeInvestments = useMemo(() => {
    return investments.filter(inv => isInTimeframe(inv.date, timeframe, currentDate));
  }, [investments, timeframe, currentDate]);

  // Financial totals for this timeframe
  const totalIncome = useMemo(() => {
    return timeframeTransactions
      .filter(t => t.type === 'INCOME')
      .reduce((sum, t) => sum + t.amount, 0);
  }, [timeframeTransactions]);

  const totalExpense = useMemo(() => {
    return timeframeTransactions
      .filter(t => t.type === 'EXPENSE')
      .reduce((sum, t) => sum + t.amount, 0);
  }, [timeframeTransactions]);

  const totalInvested = useMemo(() => {
    return timeframeInvestments.reduce((sum, i) => sum + i.amount, 0);
  }, [timeframeInvestments]);

  const netBalance = totalIncome - totalExpense;

  const timeframeLabel = useMemo(() => {
    switch (timeframe) {
      case 'today':
        return "Today's";
      case 'week':
        return 'Weekly';
      case 'month':
        return 'Monthly';
      case 'all':
        return 'Total';
    }
  }, [timeframe]);

  // Filter by category chip and search query
  const filteredTransactions = useMemo(() => {
    return timeframeTransactions.filter(tx => {
      const matchesCategory =
        selectedCategory === 'All' || tx.category.toLowerCase() === selectedCategory.toLowerCase();
      const matchesSearch =
        !searchQuery.trim() ||
        tx.note.toLowerCase().includes(searchQuery.toLowerCase()) ||
        tx.category.toLowerCase().includes(searchQuery.toLowerCase()) ||
        tx.amount.toString().includes(searchQuery);
      return matchesCategory && matchesSearch;
    });
  }, [timeframeTransactions, selectedCategory, searchQuery]);

  return (
    <div className="space-y-5 sm:space-y-6 pb-36">
      {/* Main Net Balance Display Card with Black & White Gradient and Ambient Celestial Glow */}
      <div
        id="net-balance-card"
        className="p-6 rounded-3xl bg-gradient-to-b from-[#141418] via-[#0C0C0F] to-[#050507] border border-white/15 shadow-[0_20px_50px_rgba(0,0,0,0.85)] relative overflow-hidden"
      >
        {/* Ambient Top Glow inspired by the shining orb in image.png */}
        <div className="absolute top-0 right-1/4 w-56 h-36 bg-white/10 rounded-full blur-3xl pointer-events-none" />

        <div className="flex items-center gap-2 relative z-10">
          <div className="w-8 h-8 rounded-xl bg-white/10 border border-white/20 flex items-center justify-center text-white shadow-sm">
            <Wallet className="w-4 h-4 stroke-[2.2]" />
          </div>
          <span className="text-xs font-bold uppercase tracking-wider text-zinc-400">
            {timeframeLabel} Net Cash Flow
          </span>
        </div>

        <div className="mt-4 relative z-10">
          <h1
            className={`text-3xl sm:text-4xl font-black tracking-tight ${
              netBalance >= 0 ? 'text-white' : 'text-zinc-300'
            }`}
          >
            {formatCurrency(netBalance)}
          </h1>
          <p className="text-xs text-zinc-400 mt-1 font-medium">
            {netBalance >= 0
              ? 'Positive cash flow retained for this period'
              : 'Deficit incurred — expenditures exceed earnings'}
          </p>
        </div>

        {/* 3 Summary Badges */}
        <div className="mt-6 grid grid-cols-3 gap-2.5 pt-5 border-t border-white/10 relative z-10">
          <div className="p-3 rounded-2xl bg-[#0c0c0e]/90 border border-white/10">
            <div className="flex items-center gap-1 text-emerald-400 mb-1">
              <ArrowUpRight className="w-3.5 h-3.5" />
              <span className="text-[11px] font-bold uppercase tracking-wide">Income</span>
            </div>
            <p className="text-sm sm:text-base font-extrabold text-white">
              {formatCurrency(totalIncome)}
            </p>
          </div>

          <div className="p-3 rounded-2xl bg-[#0c0c0e]/90 border border-white/10">
            <div className="flex items-center gap-1 text-zinc-400 mb-1">
              <ArrowDownRight className="w-3.5 h-3.5" />
              <span className="text-[11px] font-bold uppercase tracking-wide">Expense</span>
            </div>
            <p className="text-sm sm:text-base font-extrabold text-white">
              {formatCurrency(totalExpense)}
            </p>
          </div>

          <div className="p-3 rounded-2xl bg-[#0c0c0e]/90 border border-white/10">
            <div className="flex items-center gap-1 text-zinc-300 mb-1">
              <TrendingUp className="w-3.5 h-3.5" />
              <span className="text-[11px] font-bold uppercase tracking-wide">Invested</span>
            </div>
            <p className="text-sm sm:text-base font-extrabold text-white">
              {formatCurrency(totalInvested)}
            </p>
          </div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="space-y-3">
        <div className="flex items-center gap-2">
          {/* Search box with vertically centered icon */}
          <div className="relative flex-1">
            <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-zinc-500">
              <Search className="w-4 h-4" />
            </div>
            <input
              id="transactions-search-input"
              type="text"
              placeholder="Search transactions..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              className="w-full bg-[#0c0c0e] border border-white/10 focus:border-white rounded-xl py-2.5 pl-10 pr-9 text-xs sm:text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
            />
            {searchQuery && (
              <button
                type="button"
                onClick={() => setSearchQuery('')}
                className="absolute inset-y-0 right-0 pr-3 flex items-center text-zinc-500 hover:text-white cursor-pointer"
                title="Clear search"
              >
                <span className="text-xs font-bold leading-none">✕</span>
              </button>
            )}
          </div>
        </div>

        {/* Category Horizontal Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 scrollbar-none">
          <button
            id="cat-filter-all"
            type="button"
            onClick={() => setSelectedCategory('All')}
            className={`px-3 py-1.5 rounded-xl text-xs font-bold whitespace-nowrap transition-all cursor-pointer ${
              selectedCategory === 'All'
                ? 'bg-gradient-to-r from-white to-zinc-200 text-black shadow-md'
                : 'bg-[#0c0c0e] text-zinc-400 border border-white/10 hover:text-white hover:border-white/20'
            }`}
          >
            All Categories ({timeframeTransactions.length})
          </button>
          {Object.entries(CATEGORIES).map(([catName]) => {
            const count = timeframeTransactions.filter(
              t => t.category.toLowerCase() === catName.toLowerCase()
            ).length;
            const isSelected = selectedCategory.toLowerCase() === catName.toLowerCase();
            return (
              <button
                key={catName}
                id={`cat-filter-${catName}`}
                type="button"
                onClick={() => setSelectedCategory(catName)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap flex items-center gap-1.5 transition-all cursor-pointer ${
                  isSelected
                    ? 'bg-gradient-to-r from-white to-zinc-200 text-black font-bold shadow-md'
                    : 'bg-[#0c0c0e] text-zinc-400 border border-white/10 hover:text-white hover:border-white/20'
                }`}
              >
                <span className="w-1.5 h-1.5 rounded-full bg-zinc-400" />
                <span>{catName}</span>
                {count > 0 && <span className="text-[10px] opacity-75 font-mono">({count})</span>}
              </button>
            );
          })}
        </div>
      </div>

      {/* Transactions List Section */}
      <div className="space-y-3">
        <div className="flex items-center justify-between gap-3 flex-wrap">
          <div className="flex items-center gap-2">
            <h3 className="text-sm sm:text-base font-bold text-white">
              Transactions Record
            </h3>
            <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-white/10 text-zinc-300">
              {filteredTransactions.length}
            </span>
          </div>

          {/* Timeframe Dropdown */}
          <div className="relative inline-flex items-center">
            <Calendar className="w-3.5 h-3.5 text-white absolute left-2.5 pointer-events-none" />
            <select
              id="transactions-timeframe-dropdown"
              value={timeframe}
              onChange={e => setTimeframe(e.target.value as TimeframeOption)}
              className="appearance-none bg-[#0c0c0e] hover:bg-[#141418] text-white text-xs font-semibold py-1.5 pl-8 pr-7 rounded-xl border border-white/10 focus:border-white focus:outline-none cursor-pointer transition-all shadow-sm"
              title="Filter by timeframe"
            >
              <option value="today" className="bg-[#0c0c0e] text-white">
                Today
              </option>
              <option value="week" className="bg-[#0c0c0e] text-white">
                This Week
              </option>
              <option value="month" className="bg-[#0c0c0e] text-white">
                This Month
              </option>
              <option value="all" className="bg-[#0c0c0e] text-white">
                All Time
              </option>
            </select>
            <ChevronDown className="w-3.5 h-3.5 text-zinc-400 absolute right-2 pointer-events-none" />
          </div>
        </div>

        {filteredTransactions.length === 0 ? (
          <div
            id="empty-transactions-state"
            className="p-10 rounded-2xl bg-[#0c0c0e] border border-white/10 text-center"
          >
            <div className="w-12 h-12 rounded-2xl bg-white/5 border border-white/10 flex items-center justify-center text-zinc-400 mx-auto mb-3">
              <Wallet className="w-6 h-6" />
            </div>
            <p className="text-sm font-bold text-white">No transactions found</p>
            <p className="text-xs text-zinc-400 mt-1 max-w-sm mx-auto">
              {searchQuery
                ? 'No transactions matched your search criteria.'
                : `You have no transactions recorded for ${
                    timeframe === 'today'
                      ? 'today'
                      : timeframe === 'week'
                      ? 'this week'
                      : timeframe === 'month'
                      ? 'this month'
                      : 'this period'
                  }. Use the action button below to record entries.`}
            </p>
            <button
              type="button"
              onClick={onAddTransactionClick}
              className="mt-4 px-4 py-2 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 text-black text-xs font-bold shadow-md cursor-pointer hover:brightness-105 active:scale-95"
            >
              Record First Entry
            </button>
          </div>
        ) : (
          <div className="space-y-2">
            {filteredTransactions.map(tx => {
              const isIncome = tx.type === 'INCOME';

              return (
                <div
                  key={tx.id}
                  id={`transaction-item-${tx.id}`}
                  className="p-3 sm:p-4 rounded-2xl bg-[#0c0c0e] hover:bg-[#121216] border border-white/10 hover:border-white/20 flex items-center justify-between gap-2.5 sm:gap-4 transition-all group overflow-hidden"
                >
                  {/* Category icon & notes */}
                  <div className="flex items-center gap-2.5 sm:gap-3 min-w-0 flex-1">
                    <div className="w-9 h-9 sm:w-10 sm:h-10 rounded-xl bg-white/5 border border-white/10 flex items-center justify-center shrink-0 text-white">
                      {isIncome ? (
                        <ArrowUpRight className="w-4 h-4 text-white stroke-[2.2]" />
                      ) : (
                        <ArrowDownRight className="w-4 h-4 text-zinc-400 stroke-[2.2]" />
                      )}
                    </div>

                    <div className="min-w-0 flex-1 pr-1 sm:pr-2">
                      <p
                        className="text-xs sm:text-sm font-bold text-white truncate"
                        title={tx.note || tx.category}
                      >
                        {tx.note || tx.category}
                      </p>
                      <div className="flex items-center gap-1.5 text-[10px] sm:text-[11px] text-zinc-400 mt-0.5 truncate">
                        <span className="font-semibold shrink-0 text-zinc-300">
                          {tx.category}
                        </span>
                        <span className="shrink-0">•</span>
                        <span className="shrink-0">{formatDate(tx.date)}</span>
                        {tx.timestamp && (
                          <span className="hidden sm:inline shrink-0 text-zinc-500">
                            • {formatTime(tx.timestamp)}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Amount & Delete */}
                  <div className="flex items-center gap-2 sm:gap-3 shrink-0 text-right">
                    <div className="text-right">
                      <p
                        className={`text-xs sm:text-sm font-black tracking-tight ${
                          isIncome ? 'text-white' : 'text-zinc-300'
                        }`}
                      >
                        {isIncome ? '+' : '-'}
                        {formatCurrency(tx.amount)}
                      </p>
                      <span className="text-[10px] uppercase font-bold text-zinc-500 tracking-wider">
                        {tx.type}
                      </span>
                    </div>

                    <button
                      id={`delete-tx-${tx.id}`}
                      type="button"
                      onClick={() => onDeleteTransaction(tx.id)}
                      className="p-1.5 rounded-lg text-zinc-500 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
                      title="Delete Transaction"
                    >
                      <Trash2 className="w-3.5 h-3.5 sm:w-4 sm:h-4" />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

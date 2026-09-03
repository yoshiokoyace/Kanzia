import React, { useState } from 'react';
import { X, ArrowDownRight, ArrowUpRight, Calendar, FileText } from 'lucide-react';
import { TransactionType, CATEGORIES } from '../types';

interface AddTransactionSheetProps {
  isOpen: boolean;
  onClose: () => void;
  onAdd: (tx: {
    type: TransactionType;
    amount: number;
    category: string;
    note: string;
    date: string;
  }) => void;
}

export const AddTransactionSheet: React.FC<AddTransactionSheetProps> = ({
  isOpen,
  onClose,
  onAdd,
}) => {
  const [type, setType] = useState<TransactionType>('EXPENSE');
  const [amount, setAmount] = useState('');
  const [category, setCategory] = useState('Food');
  const [note, setNote] = useState('');
  const [date, setDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const parsedAmount = parseFloat(amount);
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      setError('Please enter a valid amount greater than 0');
      return;
    }
    if (!category) {
      setError('Please select a category');
      return;
    }

    onAdd({
      type,
      amount: parsedAmount,
      category,
      note: note.trim() || `${category} ${type === 'INCOME' ? 'Income' : 'Expense'}`,
      date,
    });

    // Reset
    setAmount('');
    setNote('');
    setError('');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/85 backdrop-blur-md animate-in fade-in duration-200">
      <div
        id="add-transaction-sheet"
        className="w-full max-w-lg bg-gradient-to-b from-[#141418] via-[#0D0D10] to-[#070709] border border-white/15 rounded-t-3xl sm:rounded-3xl p-6 sm:p-7 shadow-[0_25px_60px_rgba(0,0,0,0.95)] relative overflow-hidden max-h-[92vh] overflow-y-auto"
      >
        {/* Ambient Top Glow */}
        <div className="absolute -top-16 left-1/2 -translate-x-1/2 w-48 h-32 bg-white/10 rounded-full blur-3xl pointer-events-none" />

        {/* Header */}
        <div className="flex items-center justify-between pb-4 border-b border-white/10 relative z-10">
          <div>
            <h2 className="text-base font-bold text-white tracking-tight">Record Transaction</h2>
            <p className="text-[11px] text-zinc-400 font-medium">Log cash inflow or outflow</p>
          </div>
          <button
            id="close-add-transaction"
            type="button"
            onClick={onClose}
            className="p-1.5 rounded-xl text-zinc-400 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-5 space-y-4 relative z-10">
          {/* Type Toggle: Expense / Income */}
          <div className="grid grid-cols-2 gap-2 p-1 bg-black/60 rounded-2xl border border-white/10">
            <button
              type="button"
              id="type-expense-btn"
              onClick={() => {
                setType('EXPENSE');
                if (category === 'Salary') setCategory('Food');
              }}
              className={`flex items-center justify-center gap-2 py-2.5 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                type === 'EXPENSE'
                  ? 'bg-gradient-to-r from-white to-zinc-200 text-black shadow-md'
                  : 'text-zinc-400 hover:text-white'
              }`}
            >
              <ArrowDownRight className="w-4 h-4 stroke-[2.2]" />
              <span>Expense</span>
            </button>
            <button
              type="button"
              id="type-income-btn"
              onClick={() => {
                setType('INCOME');
                setCategory('Salary');
              }}
              className={`flex items-center justify-center gap-2 py-2.5 rounded-xl text-xs sm:text-sm font-bold transition-all cursor-pointer ${
                type === 'INCOME'
                  ? 'bg-gradient-to-r from-white to-zinc-200 text-black shadow-md'
                  : 'text-zinc-400 hover:text-white'
              }`}
            >
              <ArrowUpRight className="w-4 h-4 stroke-[2.2]" />
              <span>Income</span>
            </button>
          </div>

          {/* Amount input */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400 mb-1.5">
              Amount
            </label>
            <div className="relative flex items-center">
              <span className="absolute left-4 text-xl font-bold text-zinc-500">$</span>
              <input
                id="transaction-amount-input"
                type="number"
                step="0.01"
                min="0.01"
                placeholder="0.00"
                value={amount}
                onChange={e => {
                  setAmount(e.target.value);
                  setError('');
                }}
                autoFocus
                className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-3 pl-9 pr-4 text-2xl font-black text-white placeholder-zinc-600 focus:outline-none transition-all"
              />
            </div>
          </div>

          {/* Category selection */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400 mb-2">
              Category
            </label>
            <div className="grid grid-cols-4 gap-2">
              {Object.entries(CATEGORIES).map(([catName]) => {
                const isSelected = category === catName;
                return (
                  <button
                    key={catName}
                    id={`cat-chip-${catName}`}
                    type="button"
                    onClick={() => setCategory(catName)}
                    className={`flex flex-col items-center justify-center p-2 rounded-xl border text-xs font-semibold transition-all cursor-pointer ${
                      isSelected
                        ? 'border-white bg-white/15 text-white font-bold'
                        : 'border-white/10 bg-[#161619] text-zinc-400 hover:border-white/20 hover:text-white'
                    }`}
                  >
                    <span className="w-2 h-2 rounded-full mb-1.5 bg-zinc-400" />
                    <span className="truncate w-full text-center">{catName}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Note / Description */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400 mb-1.5">
              Note (Optional)
            </label>
            <div className="relative flex items-center">
              <FileText className="absolute left-3.5 w-4 h-4 text-zinc-400" />
              <input
                id="transaction-note-input"
                type="text"
                placeholder="e.g. Grocery trip, Monthly subscription"
                value={note}
                onChange={e => setNote(e.target.value)}
                className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 pl-10 pr-4 text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
              />
            </div>
          </div>

          {/* Date Picker */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400 mb-1.5">
              Date
            </label>
            <div className="relative flex items-center">
              <Calendar className="absolute left-3.5 w-4 h-4 text-zinc-400" />
              <input
                id="transaction-date-input"
                type="date"
                value={date}
                onChange={e => setDate(e.target.value)}
                className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 pl-10 pr-4 text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
              />
            </div>
          </div>

          {error && (
            <p className="text-xs text-white font-medium bg-white/10 border border-white/20 p-2.5 rounded-lg">
              {error}
            </p>
          )}

          {/* Submit Button with Black & White Gradient */}
          <div className="pt-2">
            <button
              id="save-transaction-btn"
              type="submit"
              className="w-full py-3.5 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 hover:brightness-105 text-black font-extrabold text-sm shadow-[0_8px_25px_rgba(255,255,255,0.25),inset_0_1px_2px_rgba(255,255,255,1)] border border-white/30 flex items-center justify-center gap-2 cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.34,1.6,0.64,1)] active:scale-[0.98] relative overflow-hidden"
            >
              {/* Specular highlight */}
              <div className="absolute inset-x-4 top-0.5 h-2 rounded-full bg-gradient-to-b from-white to-transparent pointer-events-none" />
              <span className="relative z-10">Save Transaction</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

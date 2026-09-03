import React, { useState, useRef, useEffect } from 'react';
import { X, Calendar, TrendingUp, Plus, Check, ArrowRight } from 'lucide-react';
import { Stock, Investment } from '../types';

interface AddInvestmentModalProps {
  isOpen: boolean;
  onClose: () => void;
  stocks: Stock[];
  onAdd: (inv: Omit<Investment, 'id'>) => void;
  onSaveStock: (stock: Stock) => void;
  initialStock?: string;
}

export const AddInvestmentModal: React.FC<AddInvestmentModalProps> = ({
  isOpen,
  onClose,
  stocks,
  onAdd,
  onSaveStock,
  initialStock,
}) => {
  const [selectedStock, setSelectedStock] = useState<string>(
    initialStock || stocks[0]?.ticker || 'AAPL'
  );
  const [amount, setAmount] = useState('');
  const [date, setDate] = useState(() => new Date().toISOString().split('T')[0]);
  const [error, setError] = useState('');

  // Inline "Add Stock" workflow state so the user never leaves the deposit process
  const [isAddingNewStock, setIsAddingNewStock] = useState(false);
  const [newTicker, setNewTicker] = useState('');
  const [newName, setNewName] = useState('');
  const [newStockError, setNewStockError] = useState('');
  const [successNote, setSuccessNote] = useState('');

  const amountInputRef = useRef<HTMLInputElement>(null);
  const newTickerInputRef = useRef<HTMLInputElement>(null);

  // Sync initialStock if provided or if stocks change
  useEffect(() => {
    if (initialStock) {
      setSelectedStock(initialStock);
    } else if (stocks.length > 0 && !stocks.some(s => s.ticker === selectedStock)) {
      setSelectedStock(stocks[0].ticker);
    }
  }, [initialStock, stocks, selectedStock]);

  // Focus amount input when modal opens or after adding a new stock
  useEffect(() => {
    if (isOpen && !isAddingNewStock) {
      const timer = setTimeout(() => {
        amountInputRef.current?.focus();
      }, 100);
      return () => clearTimeout(timer);
    }
  }, [isOpen, isAddingNewStock]);

  // Focus ticker input when inline stock addition is toggled open
  useEffect(() => {
    if (isAddingNewStock) {
      const timer = setTimeout(() => {
        newTickerInputRef.current?.focus();
      }, 100);
      return () => clearTimeout(timer);
    }
  }, [isAddingNewStock]);

  if (!isOpen) return null;

  // Handle saving the new stock inline and immediately proceeding to amount
  const handleSaveInlineStock = (e: React.FormEvent) => {
    e.preventDefault();
    const cleanTicker = newTicker.trim().toUpperCase();
    const cleanName = newName.trim();

    if (!cleanTicker || !cleanName) {
      setNewStockError('Please enter both ticker symbol and company name');
      return;
    }

    const createdStock: Stock = {
      ticker: cleanTicker,
      name: cleanName,
    };

    // Save stock to application state and persistence
    onSaveStock(createdStock);

    // Automatically select the newly created stock
    setSelectedStock(cleanTicker);

    // Clear and close inline stock creation form
    setNewTicker('');
    setNewName('');
    setNewStockError('');
    setIsAddingNewStock(false);

    // Show confirmation and guide user directly into typing the amount
    setSuccessNote(`Added ${cleanTicker}! Proceed with entering deposit amount.`);
    setTimeout(() => {
      amountInputRef.current?.focus();
      amountInputRef.current?.select();
    }, 120);
  };

  const handleSubmitDeposit = (e: React.FormEvent) => {
    e.preventDefault();
    const parsedAmount = parseFloat(amount);
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      setError('Please enter a valid investment deposit amount');
      amountInputRef.current?.focus();
      return;
    }
    if (!selectedStock) {
      setError('Please select or add a stock');
      return;
    }

    onAdd({
      stock: selectedStock,
      amount: parsedAmount,
      date,
      timestamp: new Date(date).getTime() || Date.now(),
    });

    setAmount('');
    setError('');
    setSuccessNote('');
    setIsAddingNewStock(false);
    onClose();
  };

  return (
    <div
      id="add-investment-overlay"
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-md animate-in fade-in duration-200"
    >
      <div
        id="add-investment-modal"
        className="w-full max-w-md bg-gradient-to-b from-[#141418] via-[#0D0D10] to-[#070709] border border-white/15 rounded-3xl p-6 sm:p-7 shadow-[0_25px_60px_rgba(0,0,0,0.95)] relative overflow-hidden text-left"
      >
        {/* Ambient Top Monochromatic Glow - referencing the glowing moon in image.png */}
        <div className="absolute -top-16 left-1/2 -translate-x-1/2 w-48 h-32 bg-white/10 rounded-full blur-3xl pointer-events-none" />

        {/* Modal Header */}
        <div className="flex items-center justify-between pb-4 border-b border-white/10 relative z-10">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-white/10 border border-white/20 flex items-center justify-center text-white shadow-sm">
              <TrendingUp className="w-4 h-4 stroke-[2.2]" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white tracking-tight">Record Deposit</h3>
              <p className="text-[11px] text-zinc-400 font-medium">Add capital to your stock portfolio</p>
            </div>
          </div>
          <button
            id="close-add-investment-btn"
            type="button"
            onClick={() => {
              setIsAddingNewStock(false);
              onClose();
            }}
            className="p-1.5 rounded-xl text-zinc-400 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Success Alert Banner when a new stock is added */}
        {successNote && (
          <div className="mt-4 p-2.5 rounded-xl bg-white/10 border border-white/25 flex items-center gap-2 text-xs font-semibold text-white animate-in fade-in duration-200">
            <Check className="w-4 h-4 text-white shrink-0" />
            <span>{successNote}</span>
          </div>
        )}

        {/* Inline Add New Stock Section (Expands smoothly without exiting deposit process) */}
        {isAddingNewStock ? (
          <div
            id="inline-add-stock-panel"
            className="mt-4 p-4 rounded-2xl bg-white/[0.04] border border-white/20 space-y-3 animate-in fade-in zoom-in-95 duration-200"
          >
            <div className="flex items-center justify-between pb-2 border-b border-white/10">
              <div className="flex items-center gap-1.5 text-xs font-bold text-white uppercase tracking-wider">
                <Plus className="w-3.5 h-3.5" />
                <span>Create New Stock Ticker</span>
              </div>
              <button
                type="button"
                onClick={() => {
                  setIsAddingNewStock(false);
                  setNewStockError('');
                }}
                className="text-[11px] text-zinc-400 hover:text-white transition-colors cursor-pointer"
              >
                Back to Select
              </button>
            </div>

            <form onSubmit={handleSaveInlineStock} className="space-y-3">
              <div>
                <label className="block text-[11px] font-bold uppercase tracking-wider text-zinc-400 mb-1">
                  Ticker Symbol
                </label>
                <input
                  ref={newTickerInputRef}
                  id="inline-ticker-input"
                  type="text"
                  placeholder="e.g. NVDA, PLTR, AMZN"
                  value={newTicker}
                  onChange={e => {
                    setNewTicker(e.target.value.toUpperCase());
                    setNewStockError('');
                  }}
                  className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2 px-3 text-sm font-bold tracking-wider text-white placeholder-zinc-500 focus:outline-none transition-all uppercase"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold uppercase tracking-wider text-zinc-400 mb-1">
                  Company / Stock Name
                </label>
                <input
                  id="inline-name-input"
                  type="text"
                  placeholder="e.g. NVIDIA Corporation"
                  value={newName}
                  onChange={e => {
                    setNewName(e.target.value);
                    setNewStockError('');
                  }}
                  className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2 px-3 text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
                />
              </div>

              {newStockError && (
                <p className="text-[11px] text-zinc-200 font-medium bg-white/10 border border-white/20 p-2 rounded-lg">
                  {newStockError}
                </p>
              )}

              <div className="flex items-center gap-2 pt-1">
                <button
                  id="save-inline-stock-btn"
                  type="submit"
                  className="flex-1 py-2.5 px-4 rounded-xl bg-gradient-to-b from-white via-zinc-100 to-zinc-300 hover:brightness-105 text-black font-extrabold text-xs shadow-md flex items-center justify-center gap-1.5 transition-all cursor-pointer active:scale-[0.98]"
                >
                  <span>Save & Add Amount</span>
                  <ArrowRight className="w-3.5 h-3.5" />
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setIsAddingNewStock(false);
                    setNewStockError('');
                  }}
                  className="py-2.5 px-3 rounded-xl bg-[#1a1a1e] hover:bg-[#242429] text-zinc-400 hover:text-white text-xs font-semibold border border-white/10 transition-all cursor-pointer"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        ) : (
          /* Standard Deposit Form */
          <form onSubmit={handleSubmitDeposit} className="mt-4 space-y-4">
            {/* Stock selector & Inline Add button */}
            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400">
                  Target Stock Ticker
                </label>
                <button
                  id="inline-toggle-add-stock-btn"
                  type="button"
                  onClick={() => {
                    setIsAddingNewStock(true);
                    setSuccessNote('');
                  }}
                  className="text-xs text-white hover:text-zinc-300 font-bold flex items-center gap-1 cursor-pointer bg-white/10 hover:bg-white/15 px-2.5 py-1 rounded-lg border border-white/20 transition-all active:scale-95"
                  title="Add a new custom stock ticker without exiting deposit"
                >
                  <Plus className="w-3.5 h-3.5" />
                  <span>Add New Stock</span>
                </button>
              </div>

              <select
                id="investment-stock-select"
                value={selectedStock}
                onChange={e => {
                  setSelectedStock(e.target.value);
                  setSuccessNote('');
                }}
                className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 px-3 text-sm font-bold text-white focus:outline-none transition-all cursor-pointer"
              >
                {stocks.map(s => (
                  <option key={s.ticker} value={s.ticker} className="bg-[#121215] text-white">
                    {s.ticker} — {s.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Amount input */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400 mb-1.5">
                Deposit Amount
              </label>
              <div className="relative flex items-center">
                <span className="absolute left-4 text-xl font-bold text-zinc-500">$</span>
                <input
                  ref={amountInputRef}
                  id="investment-amount-input"
                  type="number"
                  step="0.01"
                  min="0.01"
                  placeholder="0.00"
                  value={amount}
                  onChange={e => {
                    setAmount(e.target.value);
                    setError('');
                  }}
                  className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-3 pl-9 pr-4 text-2xl font-black text-white placeholder-zinc-600 focus:outline-none transition-all"
                />
              </div>
            </div>

            {/* Date selector */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400 mb-1.5">
                Date
              </label>
              <div className="relative flex items-center">
                <Calendar className="absolute left-3.5 w-4 h-4 text-zinc-400" />
                <input
                  id="investment-date-input"
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

            {/* Submit Deposit Button with Black and White Gradient */}
            <div className="pt-2">
              <button
                id="submit-investment-btn"
                type="submit"
                className="w-full py-3.5 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 hover:brightness-105 text-black font-extrabold text-sm shadow-[0_8px_25px_rgba(255,255,255,0.25),inset_0_1px_2px_rgba(255,255,255,1)] border border-white/30 flex items-center justify-center gap-2 cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.34,1.6,0.64,1)] active:scale-[0.98] relative overflow-hidden"
              >
                {/* Specular highlight */}
                <div className="absolute inset-x-4 top-0.5 h-2 rounded-full bg-gradient-to-b from-white to-transparent pointer-events-none" />
                <span className="relative z-10">Confirm Deposit</span>
                <ArrowRight className="w-4 h-4 relative z-10 stroke-[2.5]" />
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

import React, { useState } from 'react';
import { X, TrendingUp, ArrowRight } from 'lucide-react';
import { Stock } from '../types';

interface AddStockDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (stock: Stock, proceedToDeposit?: boolean) => void;
}

export const AddStockDialog: React.FC<AddStockDialogProps> = ({ isOpen, onClose, onSave }) => {
  const [ticker, setTicker] = useState('');
  const [name, setName] = useState('');
  const [proceedToDeposit, setProceedToDeposit] = useState(true);
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!ticker.trim() || !name.trim()) {
      setError('Please enter both ticker symbol and stock name');
      return;
    }

    onSave(
      {
        ticker: ticker.trim().toUpperCase(),
        name: name.trim(),
      },
      proceedToDeposit
    );

    setTicker('');
    setName('');
    setError('');
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-md animate-in fade-in duration-200">
      <div
        id="add-stock-dialog"
        className="w-full max-w-md bg-gradient-to-b from-[#141418] via-[#0D0D10] to-[#070709] border border-white/15 rounded-3xl p-6 sm:p-7 shadow-[0_25px_60px_rgba(0,0,0,0.95)] relative overflow-hidden"
      >
        {/* Ambient Top Glow */}
        <div className="absolute -top-16 left-1/2 -translate-x-1/2 w-48 h-32 bg-white/10 rounded-full blur-3xl pointer-events-none" />

        <div className="flex items-center justify-between pb-4 border-b border-white/10 relative z-10">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-white/10 border border-white/20 flex items-center justify-center text-white shadow-sm">
              <TrendingUp className="w-4 h-4 stroke-[2.2]" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white tracking-tight">Add Stock Ticker</h3>
              <p className="text-[11px] text-zinc-400 font-medium">Add to your tracked equities</p>
            </div>
          </div>
          <button
            id="close-stock-dialog-button"
            type="button"
            onClick={onClose}
            className="p-1.5 rounded-xl text-zinc-400 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-4 space-y-4 relative z-10">
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400 mb-1.5">
              Ticker Symbol
            </label>
            <input
              id="stock-ticker-input"
              type="text"
              placeholder="e.g. NVDA, MSFT, TSLA"
              value={ticker}
              onChange={e => {
                setTicker(e.target.value.toUpperCase());
                setError('');
              }}
              autoFocus
              className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 px-3.5 text-sm font-bold tracking-wider text-white placeholder-zinc-500 focus:outline-none transition-all uppercase"
            />
          </div>

          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-zinc-400 mb-1.5">
              Company / Stock Name
            </label>
            <input
              id="stock-name-input"
              type="text"
              placeholder="e.g. NVIDIA Corporation"
              value={name}
              onChange={e => {
                setName(e.target.value);
                setError('');
              }}
              className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 px-3.5 text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
            />
          </div>

          {/* Option to automatically proceed to deposit */}
          <label className="flex items-center gap-2.5 p-3 rounded-xl bg-white/[0.04] border border-white/10 cursor-pointer">
            <input
              type="checkbox"
              checked={proceedToDeposit}
              onChange={e => setProceedToDeposit(e.target.checked)}
              className="w-4 h-4 rounded bg-black border-white/30 text-white accent-white focus:ring-0 cursor-pointer"
            />
            <div className="text-xs">
              <span className="font-semibold text-white">Proceed immediately to add deposit amount</span>
              <p className="text-[11px] text-zinc-400">Keeps the deposit workflow active with this ticker</p>
            </div>
          </label>

          {error && (
            <p className="text-xs text-white font-medium bg-white/10 border border-white/20 p-2.5 rounded-lg">
              {error}
            </p>
          )}

          <div className="pt-2">
            <button
              id="save-stock-button"
              type="submit"
              className="w-full py-3.5 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 hover:brightness-105 text-black font-extrabold text-sm shadow-[0_8px_25px_rgba(255,255,255,0.25),inset_0_1px_2px_rgba(255,255,255,1)] border border-white/30 flex items-center justify-center gap-2 cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.34,1.6,0.64,1)] active:scale-[0.98] relative overflow-hidden"
            >
              {/* Specular highlight */}
              <div className="absolute inset-x-4 top-0.5 h-2 rounded-full bg-gradient-to-b from-white to-transparent pointer-events-none" />
              <span className="relative z-10">
                {proceedToDeposit ? 'Save & Proceed to Deposit' : 'Save Stock Ticker'}
              </span>
              <ArrowRight className="w-4 h-4 relative z-10 stroke-[2.5]" />
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

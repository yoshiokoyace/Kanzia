import React, { useMemo } from 'react';
import {
  TrendingUp,
  Building2,
  Trash2,
  Calendar,
  Plus,
} from 'lucide-react';
import { Investment, Stock } from '../types';
import { formatCurrency, formatDate, formatTime } from '../lib/formatters';
import { PortfolioPieChart } from './PortfolioPieChart';

// Monochromatic minimal palette inspired by the black and white gradient reference image
const STOCK_PALETTE = [
  '#FFFFFF', // Pure White
  '#E4E4E7', // Zinc 200 (Platinum)
  '#D4D4D8', // Zinc 300 (Silver)
  '#A1A1AA', // Zinc 400 (Medium Silver)
  '#8E8E93', // Cool Silver
  '#71717A', // Zinc 500 (Slate)
  '#52525B', // Zinc 600 (Charcoal)
  '#3F3F46', // Zinc 700 (Dark Zinc)
];

interface InvestmentsScreenProps {
  investments: Investment[];
  stocks: Stock[];
  onOpenAddInvestment: () => void;
  onOpenAddStock: () => void;
  onDeleteInvestment: (id: number) => void;
  onDeleteStock: (ticker: string) => void;
}

export const InvestmentsScreen: React.FC<InvestmentsScreenProps> = ({
  investments,
  stocks,
  onOpenAddInvestment,
  onOpenAddStock,
  onDeleteInvestment,
  onDeleteStock,
}) => {
  // Total invested across all records
  const totalPortfolioValue = useMemo(() => {
    return investments.reduce((sum, inv) => sum + inv.amount, 0);
  }, [investments]);

  // Breakdown per stock
  const stockHoldings = useMemo(() => {
    const map: Record<string, number> = {};
    investments.forEach(inv => {
      map[inv.stock] = (map[inv.stock] || 0) + inv.amount;
    });

    return stocks
      .map(stock => {
        const invested = map[stock.ticker] || 0;
        const share = totalPortfolioValue > 0 ? (invested / totalPortfolioValue) * 100 : 0;
        return {
          ...stock,
          invested,
          share,
        };
      })
      .sort((a, b) => b.invested - a.invested)
      .map((stock, idx) => ({
        ...stock,
        color: STOCK_PALETTE[idx % STOCK_PALETTE.length],
      }));
  }, [stocks, investments, totalPortfolioValue]);

  return (
    <div className="space-y-6 pb-36">
      {/* Portfolio Overview Card with Black & White Gradient & Ethereal Top Light */}
      <div
        id="portfolio-summary-card"
        className="p-5 sm:p-6 rounded-3xl bg-gradient-to-b from-[#141418] via-[#0C0C0F] to-[#050507] border border-white/15 shadow-[0_20px_50px_rgba(0,0,0,0.85)] relative overflow-hidden"
      >
        {/* Ambient Top Monochromatic Glow */}
        <div className="absolute top-0 right-1/4 w-56 h-36 bg-white/10 rounded-full blur-3xl pointer-events-none" />

        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-5 relative z-10">
          <div className="flex-1">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-xl bg-white/10 border border-white/20 flex items-center justify-center text-white shadow-sm">
                <TrendingUp className="w-4 h-4 stroke-[2.2]" />
              </div>
              <span className="text-xs font-bold uppercase tracking-wider text-zinc-400">
                Total Portfolio Holdings
              </span>
            </div>

            <div className="mt-3.5">
              <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
                {formatCurrency(totalPortfolioValue)}
              </h1>
            </div>
          </div>

          {/* Stock Allocation Pie Chart Widget */}
          <div className="shrink-0">
            <PortfolioPieChart
              allocations={stockHoldings}
              totalValue={totalPortfolioValue}
            />
          </div>
        </div>
      </div>

      {/* Stock Watchlist / Holdings */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Building2 className="w-4 h-4 text-white" />
            <h3 className="text-sm font-bold text-white">Stock Holdings & Allocations</h3>
          </div>
          <button
            id="add-custom-stock-btn"
            type="button"
            onClick={onOpenAddStock}
            className="text-xs text-white hover:text-zinc-300 font-bold flex items-center gap-1 cursor-pointer bg-white/10 hover:bg-white/15 px-2.5 py-1 rounded-lg border border-white/20 transition-all active:scale-95"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Add Stock</span>
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {stockHoldings.map(stock => {
            return (
              <div
                key={stock.ticker}
                id={`stock-card-${stock.ticker}`}
                className="p-4 rounded-2xl bg-[#0c0c0e] hover:bg-[#121216] border border-white/10 hover:border-white/20 transition-all flex flex-col justify-between shadow-sm group"
              >
                <div>
                  <div className="flex items-start justify-between">
                    <div>
                      <span
                        className="inline-block px-2.5 py-0.5 rounded-lg text-xs font-black tracking-wider border"
                        style={{
                          backgroundColor: `${stock.color}15`,
                          color: stock.color,
                          borderColor: `${stock.color}40`,
                        }}
                      >
                        {stock.ticker}
                      </span>
                      <h4 className="text-sm font-bold text-white mt-1.5 truncate">
                        {stock.name}
                      </h4>
                    </div>

                    <div className="text-right">
                      <p className="text-sm font-extrabold text-white">
                        {formatCurrency(stock.invested)}
                      </p>
                      <span className="text-[11px] text-zinc-400">
                        {stock.share.toFixed(1)}% of total
                      </span>
                    </div>
                  </div>
                </div>

                <div className="mt-3.5 pt-3 border-t border-white/10 flex items-center justify-between">
                  <div className="w-32 bg-black h-1.5 rounded-full overflow-hidden border border-white/10">
                    <div
                      className="h-full rounded-full transition-all duration-300"
                      style={{
                        width: `${Math.min(100, stock.share)}%`,
                        backgroundColor: stock.color,
                      }}
                    />
                  </div>

                  <button
                    type="button"
                    onClick={() => onDeleteStock(stock.ticker)}
                    className="text-[11px] text-zinc-500 hover:text-white p-1 cursor-pointer transition-colors"
                    title="Remove stock from watchlist"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Investment Deposit History */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Calendar className="w-4 h-4 text-zinc-400" />
            <h3 className="text-sm font-bold text-white">Deposit Transactions</h3>
          </div>
          <span className="text-xs text-zinc-400 font-semibold">{investments.length} Total</span>
        </div>

        {investments.length === 0 ? (
          <div className="p-8 rounded-2xl bg-[#0c0c0e] border border-white/10 text-center">
            <p className="text-xs text-zinc-400">No investment deposits logged yet.</p>
            <button
              type="button"
              onClick={onOpenAddInvestment}
              className="mt-3 px-4 py-2 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 text-black text-xs font-bold shadow-md cursor-pointer hover:brightness-105 active:scale-95"
            >
              Deposit to Portfolio
            </button>
          </div>
        ) : (
          <div className="space-y-2">
            {investments.map(inv => (
              <div
                key={inv.id}
                id={`investment-item-${inv.id}`}
                className="p-3.5 sm:p-4 rounded-2xl bg-[#0c0c0e] hover:bg-[#121216] border border-white/10 hover:border-white/20 flex items-center justify-between gap-3 transition-all"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-white/10 border border-white/15 flex items-center justify-center text-white font-extrabold text-xs">
                    {inv.stock.slice(0, 3)}
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-black text-white tracking-wide">
                        {inv.stock}
                      </span>
                      <span className="text-[10px] uppercase font-bold text-black bg-white px-2 py-0.5 rounded-full">
                        Deposit
                      </span>
                    </div>
                    <p className="text-[11px] text-zinc-400 mt-0.5">
                      {formatDate(inv.date)}
                      {inv.timestamp && ` • ${formatTime(inv.timestamp)}`}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  <span className="text-sm sm:text-base font-extrabold text-white">
                    +{formatCurrency(inv.amount)}
                  </span>
                  <button
                    id={`delete-inv-${inv.id}`}
                    type="button"
                    onClick={() => onDeleteInvestment(inv.id)}
                    className="p-1.5 rounded-lg text-zinc-500 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
                    title="Delete Deposit"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

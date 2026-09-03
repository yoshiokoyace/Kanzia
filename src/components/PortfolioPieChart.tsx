import React, { useState } from 'react';
import { PieChart as PieIcon } from 'lucide-react';
import { formatCurrency } from '../lib/formatters';

export interface StockAllocationItem {
  ticker: string;
  name: string;
  invested: number;
  share: number; // percentage 0 - 100
  color: string;
}

interface PortfolioPieChartProps {
  allocations: StockAllocationItem[];
  totalValue: number;
}

export const PortfolioPieChart: React.FC<PortfolioPieChartProps> = ({
  allocations,
  totalValue,
}) => {
  const [hoveredTicker, setHoveredTicker] = useState<string | null>(null);

  // Filter only stocks that have actual invested capital
  const activeAllocations = allocations.filter(a => a.invested > 0);

  // Center coordinates & radius for SVG Donut
  const size = 96;
  const center = size / 2;
  const radius = 35;
  const strokeWidth = 11;
  const circumference = 2 * Math.PI * radius;

  // Compute slice offsets
  let cumulativeOffset = 0;
  const slices = activeAllocations.map(stock => {
    const strokeDash = (stock.share / 100) * circumference;
    const offset = cumulativeOffset;
    cumulativeOffset += strokeDash;

    return {
      ...stock,
      strokeDash,
      offset,
    };
  });

  const activeStock = hoveredTicker
    ? activeAllocations.find(s => s.ticker === hoveredTicker)
    : activeAllocations[0];

  return (
    <div
      id="portfolio-allocation-pie-widget"
      className="relative flex items-center gap-3 bg-black/60 p-2.5 sm:p-3 rounded-2xl border border-white/10 shadow-[inset_0_1px_2px_rgba(255,255,255,0.05)] select-none"
    >
      {/* SVG Donut / Pie */}
      <div className="relative w-20 h-20 sm:w-24 sm:h-24 shrink-0 flex items-center justify-center">
        <svg
          width={size}
          height={size}
          viewBox={`0 0 ${size} ${size}`}
          className="w-full h-full transform -rotate-90"
        >
          {/* Background Track Circle */}
          <circle
            cx={center}
            cy={center}
            r={radius}
            fill="transparent"
            stroke="#1c1c21"
            strokeWidth={strokeWidth}
          />

          {/* Slices for each stock allocation in monochrome palette */}
          {activeAllocations.length > 0 ? (
            slices.map(slice => {
              const isHovered = hoveredTicker === slice.ticker;
              const isOnlyOne = activeAllocations.length === 1;

              return (
                <circle
                  key={slice.ticker}
                  cx={center}
                  cy={center}
                  r={radius}
                  fill="transparent"
                  stroke={slice.color}
                  strokeWidth={isHovered ? strokeWidth + 2 : strokeWidth}
                  strokeDasharray={
                    isOnlyOne
                      ? `${circumference} 0`
                      : `${Math.max(slice.strokeDash - 1.5, 0.5)} ${circumference}`
                  }
                  strokeDashoffset={-slice.offset}
                  strokeLinecap={isOnlyOne ? 'round' : 'butt'}
                  className="transition-all duration-200 cursor-pointer"
                  onMouseEnter={() => setHoveredTicker(slice.ticker)}
                  onMouseLeave={() => setHoveredTicker(null)}
                  onTouchStart={() => setHoveredTicker(slice.ticker)}
                />
              );
            })
          ) : (
            /* Empty State Ring */
            <circle
              cx={center}
              cy={center}
              r={radius}
              fill="transparent"
              stroke="#27272a"
              strokeWidth={strokeWidth}
              strokeDasharray="4 4"
            />
          )}
        </svg>

        {/* Center Donut Cutout Metric */}
        <div
          className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none text-center"
          title={
            activeStock
              ? `${activeStock.ticker}: ${activeStock.share.toFixed(1)}%`
              : 'No investments'
          }
        >
          {activeAllocations.length > 0 && activeStock ? (
            <>
              <span className="text-[11px] sm:text-xs font-black tracking-tight text-white">
                {activeStock.share.toFixed(0)}%
              </span>
              <span
                className="text-[9px] font-bold uppercase tracking-wider truncate max-w-[48px]"
                style={{ color: activeStock.color }}
              >
                {activeStock.ticker}
              </span>
            </>
          ) : (
            <>
              <PieIcon className="w-3.5 h-3.5 text-zinc-500 mb-0.5" />
              <span className="text-[9px] font-bold text-zinc-500">0%</span>
            </>
          )}
        </div>
      </div>

      {/* Breakdown Legend (compact) */}
      <div className="flex flex-col justify-center min-w-[95px] sm:min-w-[120px] max-w-[150px] pr-1">
        {activeAllocations.length > 0 ? (
          <div className="space-y-1 max-h-16 overflow-y-auto pr-1 scrollbar-none">
            {activeAllocations.slice(0, 4).map(stock => {
              const isSelected = activeStock?.ticker === stock.ticker;
              return (
                <button
                  key={stock.ticker}
                  type="button"
                  onClick={() => setHoveredTicker(stock.ticker)}
                  onMouseEnter={() => setHoveredTicker(stock.ticker)}
                  className={`w-full flex items-center justify-between gap-1 text-[11px] transition-all cursor-pointer text-left rounded px-1 py-0.5 ${
                    isSelected ? 'bg-white/10 text-white font-bold' : 'text-zinc-400 hover:text-white'
                  }`}
                >
                  <div className="flex items-center gap-1.5 min-w-0 truncate">
                    <span
                      className="w-2 h-2 rounded-full shrink-0 shadow-sm"
                      style={{ backgroundColor: stock.color }}
                    />
                    <span className="truncate">{stock.ticker}</span>
                  </div>
                  <span className="text-[10px] font-mono shrink-0">
                    {stock.share.toFixed(0)}%
                  </span>
                </button>
              );
            })}
            {activeAllocations.length > 4 && (
              <p className="text-[9px] text-zinc-500 font-medium pl-1">
                +{activeAllocations.length - 4} more
              </p>
            )}
          </div>
        ) : (
          <p className="text-[11px] text-zinc-500 font-medium leading-tight">
            No stock deposits recorded yet
          </p>
        )}
      </div>
    </div>
  );
};

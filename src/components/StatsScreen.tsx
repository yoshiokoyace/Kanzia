import React, { useMemo } from 'react';
import {
  PieChart as PieIcon,
  BarChart3,
  ChevronLeft,
  ChevronRight,
  TrendingUp,
  ArrowUpRight,
  ArrowDownRight,
  Layers,
  Calendar,
} from 'lucide-react';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Legend,
} from 'recharts';
import { Transaction, CATEGORIES } from '../types';
import { formatCurrency, formatMonthYear } from '../lib/formatters';

interface StatsScreenProps {
  transactions: Transaction[];
  currentDate: Date;
  onPrevMonth: () => void;
  onNextMonth: () => void;
}

const MONO_PALETTE = [
  '#FFFFFF',
  '#E4E4E7',
  '#D4D4D8',
  '#A1A1AA',
  '#8E8E93',
  '#71717A',
  '#52525B',
  '#3F3F46',
];

export const StatsScreen: React.FC<StatsScreenProps> = ({
  transactions,
  currentDate,
  onPrevMonth,
  onNextMonth,
}) => {
  // Filter transactions for the selected month and year
  const monthTransactions = useMemo(() => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    return transactions.filter(t => {
      const parts = t.date.split('-');
      if (parts.length < 2) return false;
      const tYear = parseInt(parts[0], 10);
      const tMonth = parseInt(parts[1], 10) - 1;
      return tYear === year && tMonth === month;
    });
  }, [transactions, currentDate]);

  // Aggregate totals
  const totalIncome = useMemo(() => {
    return monthTransactions
      .filter(t => t.type === 'INCOME')
      .reduce((sum, t) => sum + t.amount, 0);
  }, [monthTransactions]);

  const totalExpense = useMemo(() => {
    return monthTransactions
      .filter(t => t.type === 'EXPENSE')
      .reduce((sum, t) => sum + t.amount, 0);
  }, [monthTransactions]);

  const savingsRate = useMemo(() => {
    if (totalIncome <= 0) return 0;
    const saved = totalIncome - totalExpense;
    return Math.max(0, (saved / totalIncome) * 100);
  }, [totalIncome, totalExpense]);

  // Category expenses for donut chart
  const categoryExpenses = useMemo(() => {
    const expenses = monthTransactions.filter(t => t.type === 'EXPENSE');
    const map: Record<string, number> = {};

    expenses.forEach(t => {
      map[t.category] = (map[t.category] || 0) + t.amount;
    });

    const entries = Object.entries(map).map(([name, value], idx) => {
      return {
        name,
        value,
        color: MONO_PALETTE[idx % MONO_PALETTE.length],
        percent: totalExpense > 0 ? (value / totalExpense) * 100 : 0,
      };
    });

    return entries.sort((a, b) => b.value - a.value);
  }, [monthTransactions, totalExpense]);

  // Daily income and expense trend within the active month
  const dailyTrendData = useMemo(() => {
    const daysInMonth = new Date(
      currentDate.getFullYear(),
      currentDate.getMonth() + 1,
      0
    ).getDate();

    const data: { day: string; Income: number; Expense: number }[] = [];
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    for (let day = 1; day <= daysInMonth; day++) {
      let income = 0;
      let expense = 0;

      monthTransactions.forEach(t => {
        const d = new Date(t.date);
        if (d.getFullYear() === year && d.getMonth() === month && d.getDate() === day) {
          if (t.type === 'INCOME') income += t.amount;
          else expense += t.amount;
        }
      });

      // Sample days with activity or interval
      if (day % 3 === 1 || income > 0 || expense > 0) {
        data.push({
          day: `${currentDate.toLocaleString('default', { month: 'short' })} ${day}`,
          Income: income,
          Expense: expense,
        });
      }
    }

    return data;
  }, [monthTransactions, currentDate]);

  return (
    <div className="space-y-6 pb-36">
      {/* Month Navigator */}
      <div
        id="stats-month-navigator"
        className="flex items-center justify-between p-2 rounded-2xl bg-[#0c0c0e] border border-white/10"
      >
        <button
          id="stats-prev-month-btn"
          type="button"
          onClick={onPrevMonth}
          className="p-2 rounded-xl text-zinc-400 hover:text-white hover:bg-white/10 transition-all cursor-pointer"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-2">
          <Calendar className="w-4 h-4 text-white" />
          <h2 className="text-sm sm:text-base font-bold text-white">
            {formatMonthYear(currentDate)}
          </h2>
        </div>

        <button
          id="stats-next-month-btn"
          type="button"
          onClick={onNextMonth}
          className="p-2 rounded-xl text-zinc-400 hover:text-white hover:bg-white/10 transition-all cursor-pointer"
        >
          <ChevronRight className="w-5 h-5" />
        </button>
      </div>

      {/* Cash Flow & Savings Rate Highlights */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        <div className="p-4 rounded-2xl bg-[#0c0c0e] border border-white/10">
          <div className="flex items-center justify-between text-xs font-bold text-zinc-400 uppercase mb-1">
            <span>Total Income</span>
            <ArrowUpRight className="w-4 h-4 text-emerald-400" />
          </div>
          <p className="text-xl font-extrabold text-white">{formatCurrency(totalIncome)}</p>
        </div>

        <div className="p-4 rounded-2xl bg-[#0c0c0e] border border-white/10">
          <div className="flex items-center justify-between text-xs font-bold text-zinc-400 uppercase mb-1">
            <span>Total Expense</span>
            <ArrowDownRight className="w-4 h-4 text-zinc-400" />
          </div>
          <p className="text-xl font-extrabold text-zinc-300">{formatCurrency(totalExpense)}</p>
        </div>

        <div className="p-4 rounded-2xl bg-[#0c0c0e] border border-white/10">
          <div className="flex items-center justify-between text-xs font-bold text-zinc-400 uppercase mb-1">
            <span>Savings Margin</span>
            <TrendingUp className="w-4 h-4 text-white" />
          </div>
          <p className="text-xl font-extrabold text-white">{savingsRate.toFixed(1)}%</p>
        </div>
      </div>

      {/* Category Breakdown Donut Chart */}
      <div className="p-5 rounded-3xl bg-[#0c0c0e] border border-white/10">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-white/10 border border-white/15 flex items-center justify-center text-white">
              <PieIcon className="w-4 h-4 stroke-[2.2]" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white">Expense Category Breakdown</h3>
              <p className="text-xs text-zinc-400">Distribution across spending categories</p>
            </div>
          </div>
        </div>

        {categoryExpenses.length === 0 ? (
          <div className="py-12 text-center text-zinc-400 text-xs">
            No expenses logged for this month.
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 items-center">
            {/* Donut Chart */}
            <div className="h-56 w-full relative flex items-center justify-center">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categoryExpenses}
                    cx="50%"
                    cy="50%"
                    innerRadius={55}
                    outerRadius={80}
                    paddingAngle={3}
                    dataKey="value"
                  >
                    {categoryExpenses.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} stroke="#0c0c0e" strokeWidth={2} />
                    ))}
                  </Pie>
                  <Tooltip
                    formatter={(val: number) => formatCurrency(val)}
                    contentStyle={{
                      backgroundColor: '#0a0a0c',
                      borderColor: 'rgba(255,255,255,0.15)',
                      borderRadius: '12px',
                      color: '#ffffff',
                      fontSize: '12px',
                    }}
                  />
                </PieChart>
              </ResponsiveContainer>
              <div className="absolute flex flex-col items-center justify-center pointer-events-none">
                <span className="text-xs text-zinc-400 font-bold uppercase">Total</span>
                <span className="text-sm font-black text-white">{formatCurrency(totalExpense)}</span>
              </div>
            </div>

            {/* Category Percent Legend */}
            <div className="space-y-2.5">
              {categoryExpenses.slice(0, 5).map(cat => (
                <div key={cat.name} className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <div className="flex items-center gap-2">
                      <span
                        className="w-2.5 h-2.5 rounded-full"
                        style={{ backgroundColor: cat.color }}
                      />
                      <span className="font-semibold text-white">{cat.name}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-white">{formatCurrency(cat.value)}</span>
                      <span className="text-zinc-400 text-[11px]">({cat.percent.toFixed(0)}%)</span>
                    </div>
                  </div>
                  <div className="w-full bg-black h-1.5 rounded-full overflow-hidden border border-white/10">
                    <div
                      className="h-full rounded-full transition-all duration-500"
                      style={{
                        width: `${cat.percent}%`,
                        backgroundColor: cat.color,
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Daily / Period Trend Bar Chart */}
      <div className="p-5 rounded-3xl bg-[#0c0c0e] border border-white/10">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-white/10 border border-white/15 flex items-center justify-center text-white">
              <BarChart3 className="w-4 h-4 stroke-[2.2]" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white">Income vs Expense Trajectory</h3>
              <p className="text-xs text-zinc-400">Periodic cash inflows vs outflows</p>
            </div>
          </div>
        </div>

        <div className="h-64 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={dailyTrendData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" vertical={false} />
              <XAxis
                dataKey="day"
                stroke="#71717A"
                fontSize={11}
                tickLine={false}
                axisLine={false}
              />
              <YAxis
                stroke="#71717A"
                fontSize={11}
                tickLine={false}
                axisLine={false}
                tickFormatter={val => `$${val}`}
              />
              <Tooltip
                formatter={(val: number) => formatCurrency(val)}
                contentStyle={{
                  backgroundColor: '#0a0a0c',
                  borderColor: 'rgba(255,255,255,0.15)',
                  borderRadius: '12px',
                  color: '#ffffff',
                  fontSize: '12px',
                }}
              />
              <Legend
                wrapperStyle={{ fontSize: '12px', paddingTop: '10px' }}
              />
              <Bar dataKey="Income" fill="#ffffff" radius={[4, 4, 0, 0]} maxBarSize={30} />
              <Bar dataKey="Expense" fill="#71717a" radius={[4, 4, 0, 0]} maxBarSize={30} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Detailed Categories List */}
      <div className="p-5 rounded-3xl bg-[#0c0c0e] border border-white/10 space-y-4">
        <div className="flex items-center gap-2">
          <Layers className="w-4 h-4 text-white" />
          <h3 className="text-sm font-bold text-white">Category Spending Metrics</h3>
        </div>

        {categoryExpenses.length === 0 ? (
          <p className="text-xs text-zinc-400 text-center py-4">No spending data to display.</p>
        ) : (
          <div className="divide-y divide-white/10">
            {categoryExpenses.map(cat => (
              <div key={cat.name} className="py-3 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div
                    className="w-3.5 h-3.5 rounded-full shrink-0"
                    style={{ backgroundColor: cat.color }}
                  />
                  <div>
                    <p className="text-xs font-bold text-white">{cat.name}</p>
                    <p className="text-[11px] text-zinc-400">
                      {cat.percent.toFixed(1)}% of total monthly expense
                    </p>
                  </div>
                </div>

                <div className="text-right">
                  <p className="text-sm font-bold text-white">{formatCurrency(cat.value)}</p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

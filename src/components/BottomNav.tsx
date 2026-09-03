import React from 'react';
import { Home, PieChart, TrendingUp, Plus } from 'lucide-react';
import { NavTab } from '../types';

interface BottomNavProps {
  activeTab: NavTab;
  onTabChange: (tab: NavTab) => void;
  onAddTransactionClick: () => void;
  onNewDepositClick: () => void;
}

export const BottomNav: React.FC<BottomNavProps> = ({
  activeTab,
  onTabChange,
  onAddTransactionClick,
  onNewDepositClick,
}) => {
  const tabs = [
    {
      id: 'home' as NavTab,
      label: 'Home',
      icon: Home,
    },
    {
      id: 'stats' as NavTab,
      label: 'Stats',
      icon: PieChart,
    },
    {
      id: 'investments' as NavTab,
      label: 'Investments',
      icon: TrendingUp,
    },
  ];

  return (
    <>
      {/* Bottom Gradient Blur Backdrop - smoothly diffuses and blurs content passing behind the elevated nav */}
      <div className="fixed inset-x-0 bottom-0 h-36 sm:h-40 pointer-events-none overflow-hidden select-none z-20">
        <div
          className="absolute inset-0 backdrop-blur-2xl"
          style={{
            maskImage:
              'linear-gradient(to top, rgba(0,0,0,1) 0%, rgba(0,0,0,0.85) 50%, rgba(0,0,0,0) 100%)',
            WebkitMaskImage:
              'linear-gradient(to top, rgba(0,0,0,1) 0%, rgba(0,0,0,0.85) 50%, rgba(0,0,0,0) 100%)',
          }}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black via-black/80 to-transparent" />
      </div>

      {/* Elevated Floating Navigation Bar & Thumb Action Button */}
      <nav
        id="bottom-nav"
        className="fixed bottom-5 sm:bottom-7 left-0 right-0 z-30 pointer-events-none pb-safe"
      >
        <div className="max-w-md md:max-w-lg mx-auto px-4 flex items-center justify-between gap-3 pointer-events-auto relative">
          {/* Liquid Floating Dock for Tabs in Black & White Gradient Style */}
          <div className="flex-1 flex items-center justify-around bg-[#0a0a0c]/90 backdrop-blur-2xl border border-white/15 shadow-[0_12px_40px_rgba(0,0,0,0.9),inset_0_1px_2px_rgba(255,255,255,0.15)] rounded-full px-2.5 py-1.5 relative">
            {tabs.map(tab => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.id;

              return (
                <button
                  key={tab.id}
                  id={`nav-tab-${tab.id}`}
                  type="button"
                  onClick={() => onTabChange(tab.id)}
                  aria-label={tab.label}
                  className={`group relative flex flex-col items-center justify-center flex-1 py-1.5 px-3 rounded-full transition-all duration-300 ease-[cubic-bezier(0.34,1.56,0.64,1)] active:scale-[1.15] sm:hover:scale-105 cursor-pointer select-none overflow-hidden ${
                    isActive
                      ? 'text-white font-bold'
                      : 'text-zinc-500 hover:text-zinc-300'
                  }`}
                >
                  {/* Liquid Water Droplet Texture for Active Tab in Monochrome Style */}
                  {isActive && (
                    <div className="absolute inset-0 rounded-full bg-gradient-to-b from-white/20 via-white/10 to-white/5 border border-white/25 shadow-[0_4px_16px_rgba(255,255,255,0.15),inset_0_1px_2px_rgba(255,255,255,0.35)] pointer-events-none">
                      {/* Specular water reflection shine at top of droplet */}
                      <div className="absolute inset-x-3 top-0.5 h-1.5 rounded-full bg-gradient-to-b from-white/40 to-transparent pointer-events-none" />
                    </div>
                  )}

                  <div className="relative z-10 flex flex-col items-center">
                    <Icon
                      className={`w-5 h-5 transition-all duration-300 ease-[cubic-bezier(0.34,1.56,0.64,1)] ${
                        isActive
                          ? 'scale-110 stroke-[2.35] text-white'
                          : 'stroke-[1.8] group-hover:scale-105 text-zinc-400'
                      }`}
                    />
                    <span className="text-[10px] sm:text-[11px] font-medium tracking-tight mt-0.5 whitespace-nowrap">
                      {tab.label}
                    </span>
                  </div>
                </button>
              );
            })}
          </div>

          {/* Elevated Thumb-Accessible Action Buttons with Black & White Gradient */}
          {activeTab !== 'investments' ? (
            /* Home/Stats Screen: Add Transaction Liquid Droplet FAB */
            <button
              id="fab-add-transaction"
              type="button"
              onClick={onAddTransactionClick}
              aria-label="Add Transaction"
              title="Add Transaction"
              className="w-13 h-13 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 text-black shadow-[0_8px_30px_rgba(255,255,255,0.3),inset_0_1px_2px_rgba(255,255,255,1),inset_0_-2px_4px_rgba(0,0,0,0.3)] border border-white/40 flex items-center justify-center cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.34,1.6,0.64,1)] active:scale-[1.18] sm:hover:scale-108 relative overflow-hidden shrink-0 select-none"
            >
              {/* Liquid specular dome reflection */}
              <div className="absolute inset-x-2 top-1 h-3 rounded-full bg-gradient-to-b from-white via-white/40 to-transparent pointer-events-none" />
              <Plus className="w-6 h-6 stroke-[2.5] relative z-10 text-black" />
            </button>
          ) : (
            /* Investment Screen: Thumb-Accessible New Deposit Liquid Droplet Button */
            <button
              id="fab-new-deposit"
              type="button"
              onClick={onNewDepositClick}
              aria-label="New Deposit"
              title="New Deposit"
              className="h-13 px-4 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 text-black shadow-[0_8px_30px_rgba(255,255,255,0.3),inset_0_1px_2px_rgba(255,255,255,1),inset_0_-2px_4px_rgba(0,0,0,0.3)] border border-white/40 flex items-center gap-1.5 cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.34,1.6,0.64,1)] active:scale-[1.16] sm:hover:scale-108 relative overflow-hidden shrink-0 select-none font-extrabold"
            >
              {/* Liquid specular dome reflection */}
              <div className="absolute inset-x-3 top-1 h-3 rounded-full bg-gradient-to-b from-white via-white/40 to-transparent pointer-events-none" />
              <Plus className="w-5 h-5 stroke-[2.5] relative z-10 text-black" />
              <span className="text-xs font-black tracking-wide relative z-10 pr-0.5 text-black">
                Deposit
              </span>
            </button>
          )}
        </div>
      </nav>
    </>
  );
};

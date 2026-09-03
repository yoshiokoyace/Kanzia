import React from 'react';
import { User } from '../types';

interface NavbarProps {
  currentUser: User | null;
  onOpenProfile: () => void;
  isSyncing?: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentUser,
  onOpenProfile,
  isSyncing = false,
}) => {
  const initials = currentUser?.name
    ? currentUser.name
        .split(' ')
        .map(n => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase()
    : 'U';

  return (
    <header
      id="app-header"
      className="sticky top-0 z-30 w-full pointer-events-none"
    >
      {/* Top Gradient Blur Backdrop - smoothly diffuses and blurs content scrolling behind the top navbar */}
      <div className="absolute inset-x-0 top-0 h-24 sm:h-28 pointer-events-none overflow-hidden select-none -z-10">
        <div
          className="absolute inset-0 backdrop-blur-2xl"
          style={{
            maskImage:
              'linear-gradient(to bottom, rgba(0,0,0,1) 0%, rgba(0,0,0,0.85) 50%, rgba(0,0,0,0) 100%)',
            WebkitMaskImage:
              'linear-gradient(to bottom, rgba(0,0,0,1) 0%, rgba(0,0,0,0.85) 50%, rgba(0,0,0,0) 100%)',
          }}
        />
        <div className="absolute inset-0 bg-gradient-to-b from-black via-black/80 to-transparent" />
      </div>

      <div className="max-w-md md:max-w-3xl lg:max-w-4xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between pointer-events-auto">
        {/* Brand Logo & Name */}
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-b from-white via-zinc-200 to-zinc-400 flex items-center justify-center shadow-[0_4px_15px_rgba(255,255,255,0.2)] border border-white relative overflow-hidden">
            {/* Liquid specular highlight */}
            <div className="absolute inset-x-0 top-0 h-1/2 bg-gradient-to-b from-white/70 to-transparent pointer-events-none" />
            <span className="text-black font-black text-base tracking-wider relative z-10">K</span>
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-sm sm:text-base font-black tracking-wider text-white">
                KANZIA
              </span>
              <span className="text-[9px] uppercase font-extrabold tracking-widest px-1.5 py-0.2 rounded-full bg-white/10 text-white border border-white/20">
                PRO
              </span>
            </div>
            <p className="text-[11px] text-zinc-400 font-medium hidden md:block">
              Financial Ledger & Portfolio
            </p>
          </div>
        </div>

        {/* Right Actions */}
        <div className="flex items-center gap-2.5">
          {/* Profile / Account button */}
          <button
            id="profile-button"
            type="button"
            onClick={onOpenProfile}
            className="flex items-center gap-2 p-1.5 sm:px-3 sm:py-1.5 rounded-xl bg-[#0c0c0e]/90 backdrop-blur-md hover:bg-[#141418] border border-white/10 hover:border-white/25 transition-all cursor-pointer active:scale-95"
          >
            <div className="w-6 h-6 rounded-lg bg-gradient-to-b from-white via-zinc-200 to-zinc-400 flex items-center justify-center text-[10px] font-black text-black shadow-sm">
              {initials}
            </div>
            <span className="text-xs font-semibold text-white hidden sm:inline max-w-[100px] truncate">
              {currentUser?.name || 'Account'}
            </span>
          </button>
        </div>
      </div>
    </header>
  );
};

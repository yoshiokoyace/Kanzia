import React, { useEffect } from 'react';
import { Clock, ShieldAlert, Lock, ArrowRight } from 'lucide-react';
import { User } from '../types';

interface SessionExpiredModalProps {
  isOpen: boolean;
  currentUser: User | null;
  onLoginAgain: () => void;
}

export const SessionExpiredModal: React.FC<SessionExpiredModalProps> = ({
  isOpen,
  currentUser,
  onLoginAgain,
}) => {
  // Handle ESC or Enter key to conveniently proceed to sign in
  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Enter') {
        e.preventDefault();
        onLoginAgain();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onLoginAgain]);

  if (!isOpen) return null;

  return (
    <div
      id="session-expired-overlay"
      role="alertdialog"
      aria-modal="true"
      aria-labelledby="session-expired-title"
      aria-describedby="session-expired-desc"
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-md animate-in fade-in duration-200"
    >
      <div
        id="session-expired-modal"
        className="w-full max-w-sm bg-gradient-to-b from-[#141418] via-[#0D0D10] to-[#070709] border border-white/20 rounded-3xl p-6 sm:p-7 shadow-[0_25px_60px_rgba(0,0,0,0.95)] relative overflow-hidden text-center select-none"
      >
        {/* Ambient Monochromatic Glow from image.png */}
        <div className="absolute -top-16 left-1/2 -translate-x-1/2 w-48 h-32 bg-white/15 rounded-full blur-3xl pointer-events-none" />

        {/* Security Warning Icon with Liquid Droplet Texture */}
        <div className="relative w-16 h-16 mx-auto mb-4 rounded-2xl bg-gradient-to-b from-white/20 to-white/5 border border-white/30 shadow-[0_4px_20px_rgba(255,255,255,0.2),inset_0_1px_2px_rgba(255,255,255,0.4)] flex items-center justify-center text-white">
          {/* Specular shine */}
          <div className="absolute inset-x-2 top-0.5 h-2 rounded-full bg-gradient-to-b from-white/60 to-transparent pointer-events-none" />
          <Clock className="w-8 h-8 stroke-[2.2]" />
          <span className="absolute -bottom-1 -right-1 w-5 h-5 rounded-full bg-white text-black flex items-center justify-center shadow-md">
            <Lock className="w-3 h-3 stroke-[2.5]" />
          </span>
        </div>

        {/* Badge */}
        <span className="inline-flex items-center gap-1 text-[11px] font-bold px-2.5 py-0.5 rounded-full bg-white/10 text-white border border-white/20 tracking-wide uppercase mb-2">
          <ShieldAlert className="w-3 h-3" />
          Inactivity Protection
        </span>

        {/* Headline */}
        <h3
          id="session-expired-title"
          className="text-xl font-extrabold tracking-tight text-white"
        >
          Session Expired
        </h3>

        {/* Description */}
        <p
          id="session-expired-desc"
          className="text-xs text-zinc-400 leading-relaxed mt-2"
        >
          Your session timed out after 3 minutes of inactivity. For your security, your financial ledger and balance records have been safely locked.
        </p>

        {/* Locked Account Identity Card */}
        {currentUser?.email && (
          <div className="mt-4 p-3 rounded-xl bg-white/[0.04] border border-white/10 flex items-center justify-between text-left">
            <div className="min-w-0 pr-2">
              <p className="text-[10px] uppercase font-bold tracking-wider text-zinc-500">
                Locked Account
              </p>
              <p className="text-xs font-semibold text-white truncate">
                {currentUser.email}
              </p>
            </div>
            <div className="w-7 h-7 rounded-lg bg-white/10 border border-white/20 flex items-center justify-center text-white shrink-0">
              <Lock className="w-3.5 h-3.5" />
            </div>
          </div>
        )}

        {/* Action Button: Sign in again */}
        <div className="mt-5 space-y-2">
          <button
            id="session-expired-login-btn"
            type="button"
            onClick={onLoginAgain}
            className="w-full py-3.5 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 hover:brightness-105 text-black font-extrabold text-sm shadow-[0_8px_25px_rgba(255,255,255,0.25),inset_0_1px_2px_rgba(255,255,255,1)] border border-white/30 flex items-center justify-center gap-2 cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.34,1.6,0.64,1)] active:scale-[0.98] relative overflow-hidden"
          >
            {/* Liquid specular shine */}
            <div className="absolute inset-x-4 top-0.5 h-2 rounded-full bg-gradient-to-b from-white to-transparent pointer-events-none" />
            <span className="relative z-10">Sign In to Resume</span>
            <ArrowRight className="w-4 h-4 relative z-10 stroke-[2.5]" />
          </button>
        </div>
      </div>
    </div>
  );
};

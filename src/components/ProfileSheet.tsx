import React, { useState } from 'react';
import { X, Mail, Lock, LogOut, RefreshCw, Trash2, Database, AlertTriangle, ShieldAlert } from 'lucide-react';
import { User as UserType } from '../types';

interface ProfileSheetProps {
  isOpen: boolean;
  onClose: () => void;
  currentUser: UserType | null;
  onLogout: () => void;
  onLockSession: () => void;
  onTriggerSessionExpired?: () => void;
  onResetData: () => void;
  onSync: () => void;
  isSyncing: boolean;
}

export const ProfileSheet: React.FC<ProfileSheetProps> = ({
  isOpen,
  onClose,
  currentUser,
  onLogout,
  onLockSession,
  onTriggerSessionExpired,
  onResetData,
  onSync,
  isSyncing,
}) => {
  const [showConfirmReset, setShowConfirmReset] = useState(false);

  if (!isOpen) return null;

  const initials = currentUser?.name
    ? currentUser.name
        .split(' ')
        .map(n => n[0])
        .slice(0, 2)
        .join('')
        .toUpperCase()
    : 'U';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-md animate-in fade-in duration-200">
      <div
        id="profile-sheet-modal"
        className="w-full max-w-md bg-gradient-to-b from-[#141418] via-[#0D0D10] to-[#070709] border border-white/15 rounded-3xl p-6 sm:p-7 shadow-[0_25px_60px_rgba(0,0,0,0.95)] relative overflow-hidden"
      >
        {/* Ambient Top Glow */}
        <div className="absolute -top-16 left-1/2 -translate-x-1/2 w-48 h-32 bg-white/10 rounded-full blur-3xl pointer-events-none" />

        {/* Header */}
        <div className="flex items-center justify-between pb-4 border-b border-white/10 relative z-10">
          <div>
            <h3 className="text-base font-bold text-white tracking-tight">User Profile & Settings</h3>
            <p className="text-[11px] text-zinc-400 font-medium">Session & storage management</p>
          </div>
          <button
            id="close-profile-btn"
            type="button"
            onClick={onClose}
            className="p-1.5 rounded-xl text-zinc-400 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* User Card */}
        <div className="mt-5 p-4 rounded-2xl bg-white/[0.04] border border-white/10 flex items-center gap-4 relative z-10">
          <div className="w-13 h-13 rounded-2xl bg-gradient-to-b from-white via-zinc-200 to-zinc-400 flex items-center justify-center text-lg font-black text-black shadow-md shrink-0">
            {initials}
          </div>
          <div className="flex-1 min-w-0">
            <h4 className="text-base font-bold text-white truncate">
              {currentUser?.name || 'Guest User'}
            </h4>
            <p className="text-xs text-zinc-400 flex items-center gap-1.5 truncate mt-0.5">
              <Mail className="w-3.5 h-3.5 shrink-0 text-white" />
              <span className="truncate">{currentUser?.email || 'No email registered'}</span>
            </p>
            <span className="inline-block mt-1.5 text-[10px] font-bold px-2 py-0.5 rounded-full bg-white/10 text-white border border-white/20">
              Active Session
            </span>
          </div>
        </div>

        {/* Storage / Security section */}
        <div className="mt-4 space-y-2 relative z-10">
          <div className="flex items-center justify-between p-3 rounded-xl bg-black/60 border border-white/10">
            <div className="flex items-center gap-2.5">
              <Database className="w-4 h-4 text-white" />
              <div>
                <p className="text-xs font-semibold text-white">Persistence Sync</p>
                <p className="text-[11px] text-zinc-500">Cloud Storage & Offline Engine</p>
              </div>
            </div>
            <button
              id="profile-sync-trigger"
              type="button"
              onClick={onSync}
              disabled={isSyncing}
              className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-white/10 text-white hover:bg-white/15 border border-white/15 flex items-center gap-1.5 cursor-pointer disabled:opacity-50 transition-all active:scale-95"
            >
              <RefreshCw className={`w-3 h-3 ${isSyncing ? 'animate-spin' : ''}`} />
              <span>{isSyncing ? 'Syncing...' : 'Sync'}</span>
            </button>
          </div>

          <div className="flex items-center justify-between p-3 rounded-xl bg-black/60 border border-white/10">
            <div className="flex items-center gap-2.5">
              <Lock className="w-4 h-4 text-white" />
              <div>
                <p className="text-xs font-semibold text-white">Inactivity Protection</p>
                <p className="text-[11px] text-zinc-500">Auto-locks ledger after 3m idle</p>
              </div>
            </div>
            <button
              id="trigger-test-session-expired-btn"
              type="button"
              onClick={() => {
                onClose();
                if (onTriggerSessionExpired) {
                  onTriggerSessionExpired();
                } else {
                  onLockSession();
                }
              }}
              className="px-2.5 py-1.5 rounded-lg text-xs font-semibold bg-white/10 text-zinc-300 hover:text-white hover:bg-white/15 border border-white/15 flex items-center gap-1 cursor-pointer transition-all active:scale-95"
              title="Test Inactivity Session Expiry Modal"
            >
              <ShieldAlert className="w-3 h-3" />
              <span>Test Notice</span>
            </button>
          </div>
        </div>

        {/* Danger zone & session actions */}
        <div className="mt-4 pt-4 border-t border-white/10 space-y-2 relative z-10">
          <button
            id="profile-lock-session-btn"
            type="button"
            onClick={() => {
              onClose();
              onLockSession();
            }}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-white/10 hover:bg-white/15 text-white text-xs font-bold border border-white/15 transition-all cursor-pointer"
          >
            <Lock className="w-3.5 h-3.5" />
            <span>Lock Ledger Session</span>
          </button>

          {showConfirmReset ? (
            <div className="p-3 rounded-xl bg-white/5 border border-white/20 space-y-2 text-center">
              <div className="flex items-center justify-center gap-1.5 text-xs font-bold text-white">
                <AlertTriangle className="w-4 h-4" />
                <span>Reset all transactions & stocks?</span>
              </div>
              <div className="flex gap-2">
                <button
                  id="confirm-reset-all-btn"
                  type="button"
                  onClick={() => {
                    onResetData();
                    setShowConfirmReset(false);
                    onClose();
                  }}
                  className="flex-1 py-1.5 rounded-lg bg-white text-black font-bold text-xs cursor-pointer hover:bg-zinc-200"
                >
                  Yes, Reset
                </button>
                <button
                  type="button"
                  onClick={() => setShowConfirmReset(false)}
                  className="flex-1 py-1.5 rounded-lg bg-zinc-800 text-zinc-300 hover:text-white font-semibold text-xs cursor-pointer"
                >
                  Cancel
                </button>
              </div>
            </div>
          ) : (
            <button
              id="request-reset-data-btn"
              type="button"
              onClick={() => setShowConfirmReset(true)}
              className="w-full flex items-center justify-center gap-2 py-2 rounded-xl text-zinc-500 hover:text-zinc-300 text-xs font-medium transition-colors cursor-pointer"
            >
              <Trash2 className="w-3.5 h-3.5" />
              <span>Reset Ledger to Defaults</span>
            </button>
          )}

          <button
            id="profile-logout-btn"
            type="button"
            onClick={() => {
              onClose();
              onLogout();
            }}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-[#161619] hover:bg-[#202025] text-zinc-400 hover:text-white text-xs font-bold border border-white/10 transition-all cursor-pointer"
          >
            <LogOut className="w-3.5 h-3.5" />
            <span>Log Out</span>
          </button>
        </div>
      </div>
    </div>
  );
};

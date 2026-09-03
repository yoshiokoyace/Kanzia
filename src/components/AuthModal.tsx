import React, { useState } from 'react';
import { KeyRound, Mail, Lock, User, Eye, EyeOff, AlertCircle, CheckCircle2, X, ArrowLeft } from 'lucide-react';
import { AuthMode, User as UserType } from '../types';

interface AuthModalProps {
  isOpen: boolean;
  initialMode?: AuthMode;
  isDismissable?: boolean;
  savedEmail?: string;
  onDismiss: () => void;
  onLogin: (email: string, pass: string) => Promise<{ success: boolean; user?: UserType; error?: string }> | { success: boolean; user?: UserType; error?: string };
  onRegister: (name: string, email: string, pass: string) => Promise<{ success: boolean; user?: UserType; error?: string }> | { success: boolean; user?: UserType; error?: string };
  onResetPassword: (email: string, newPass: string) => Promise<{ success: boolean; error?: string }> | { success: boolean; error?: string };
}

export const AuthModal: React.FC<AuthModalProps> = ({
  isOpen,
  initialMode = 'LOGIN',
  isDismissable = true,
  savedEmail = '',
  onDismiss,
  onLogin,
  onRegister,
  onResetPassword,
}) => {
  const [mode, setMode] = useState<AuthMode>(initialMode);
  const [name, setName] = useState('');
  const [email, setEmail] = useState(savedEmail);
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const trimmedEmail = email.trim();
    const trimmedPass = password.trim();

    if (!trimmedEmail) {
      setError('Please enter your email address');
      return;
    }

    if (mode === 'LOGIN') {
      if (!trimmedPass) {
        setError('Please enter your password');
        return;
      }
      setIsLoading(true);
      try {
        const res = await onLogin(trimmedEmail, trimmedPass);
        setIsLoading(false);
        if (!res.success) {
          setError(res.error || 'Invalid credentials');
        } else {
          onDismiss();
        }
      } catch (err: unknown) {
        setIsLoading(false);
        setError(err instanceof Error ? err.message : 'Login failed. Please try again.');
      }
      return;
    }

    if (mode === 'REGISTER') {
      if (!name.trim()) {
        setError('Please enter your full name');
        return;
      }
      if (trimmedPass.length < 6) {
        setError('Password must be at least 6 characters long');
        return;
      }
      if (trimmedPass !== confirmPassword.trim()) {
        setError('Passwords do not match');
        return;
      }
      setIsLoading(true);
      try {
        const res = await onRegister(name.trim(), trimmedEmail, trimmedPass);
        setIsLoading(false);
        if (!res.success) {
          setError(res.error || 'Failed to register');
        } else {
          onDismiss();
        }
      } catch (err: unknown) {
        setIsLoading(false);
        setError(err instanceof Error ? err.message : 'Failed to register');
      }
      return;
    }

    if (mode === 'FORGOT_PASSWORD') {
      if (trimmedPass.length < 6) {
        setError('New password must be at least 6 characters');
        return;
      }
      if (trimmedPass !== confirmPassword.trim()) {
        setError('Passwords do not match');
        return;
      }
      setIsLoading(true);
      try {
        const res = await onResetPassword(trimmedEmail, trimmedPass);
        setIsLoading(false);
        if (!res.success) {
          setError(res.error || 'Failed to reset password');
        } else {
          setSuccess('Password reset successfully! You can now log in.');
          setMode('LOGIN');
          setPassword('');
          setConfirmPassword('');
        }
      } catch (err: unknown) {
        setIsLoading(false);
        setError(err instanceof Error ? err.message : 'Failed to reset password');
      }
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/85 backdrop-blur-md animate-in fade-in duration-200">
      <div
        id="auth-modal-card"
        className="w-full max-w-md bg-gradient-to-b from-[#141418] via-[#0D0D10] to-[#070709] border border-white/15 rounded-3xl p-6 sm:p-7 shadow-[0_25px_60px_rgba(0,0,0,0.95)] overflow-hidden relative"
      >
        {/* Ambient Top Glow */}
        <div className="absolute -top-16 left-1/2 -translate-x-1/2 w-48 h-32 bg-white/10 rounded-full blur-3xl pointer-events-none" />

        {/* Top bar */}
        <div className="flex items-center justify-between relative z-10">
          {mode === 'FORGOT_PASSWORD' ? (
            <button
              id="back-to-login-header-btn"
              type="button"
              onClick={() => {
                setMode('LOGIN');
                setError(null);
              }}
              className="p-1.5 rounded-xl text-zinc-400 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
          ) : (
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-xl bg-white/10 border border-white/20 flex items-center justify-center text-white shadow-sm">
                <KeyRound className="w-4 h-4 stroke-[2.2]" />
              </div>
              <span className="text-xs font-black tracking-widest text-white uppercase">
                KANZIA
              </span>
            </div>
          )}

          {isDismissable && (
            <button
              id="auth-dismiss-button"
              type="button"
              onClick={onDismiss}
              className="p-1.5 rounded-xl text-zinc-400 hover:text-white hover:bg-white/10 transition-colors cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {/* Title and Subtitle */}
        <div className="mt-4 text-center relative z-10">
          <h2 className="text-xl sm:text-2xl font-black text-white">
            {mode === 'LOGIN' && 'Welcome Back'}
            {mode === 'REGISTER' && 'Create Account'}
            {mode === 'FORGOT_PASSWORD' && 'Reset Password'}
          </h2>
          <p className="text-xs text-zinc-400 mt-1 px-4 leading-relaxed">
            {mode === 'LOGIN' && 'Enter your credentials to access your financial ledger.'}
            {mode === 'REGISTER' && 'Sign up to track expenses, investments, and net worth.'}
            {mode === 'FORGOT_PASSWORD' && 'Enter your registered email and set a new password.'}
          </p>
        </div>

        {/* Tabs: Log In vs Sign Up */}
        {mode !== 'FORGOT_PASSWORD' && (
          <div className="mt-5 grid grid-cols-2 gap-1 p-1 bg-black/60 rounded-2xl border border-white/10 relative z-10">
            <button
              id="tab-login"
              type="button"
              onClick={() => {
                setMode('LOGIN');
                setError(null);
                setSuccess(null);
              }}
              className={`py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                mode === 'LOGIN'
                  ? 'bg-gradient-to-r from-white to-zinc-200 text-black shadow-md'
                  : 'text-zinc-400 hover:text-white'
              }`}
            >
              Log In
            </button>
            <button
              id="tab-register"
              type="button"
              onClick={() => {
                setMode('REGISTER');
                setError(null);
                setSuccess(null);
              }}
              className={`py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                mode === 'REGISTER'
                  ? 'bg-gradient-to-r from-white to-zinc-200 text-black shadow-md'
                  : 'text-zinc-400 hover:text-white'
              }`}
            >
              Sign Up
            </button>
          </div>
        )}

        {/* Error / Success alerts */}
        {error && (
          <div className="mt-4 flex items-center gap-2 p-3 rounded-xl bg-white/10 border border-white/20 text-xs font-medium text-white relative z-10">
            <AlertCircle className="w-4 h-4 shrink-0 text-white" />
            <span>{error}</span>
          </div>
        )}

        {success && (
          <div className="mt-4 flex items-center gap-2 p-3 rounded-xl bg-white/10 border border-white/25 text-xs font-medium text-white relative z-10">
            <CheckCircle2 className="w-4 h-4 shrink-0 text-white" />
            <span>{success}</span>
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} className="mt-5 space-y-3.5 relative z-10">
          {mode === 'REGISTER' && (
            <div>
              <label className="block text-[11px] font-bold uppercase tracking-wider text-zinc-400 mb-1">
                Full Name
              </label>
              <div className="relative flex items-center">
                <User className="absolute left-3.5 w-4 h-4 text-zinc-400" />
                <input
                  id="auth_name_input"
                  type="text"
                  placeholder="e.g. Alex Morgan"
                  value={name}
                  onChange={e => setName(e.target.value)}
                  className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 pl-10 pr-3.5 text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
                />
              </div>
            </div>
          )}

          <div>
            <label className="block text-[11px] font-bold uppercase tracking-wider text-zinc-400 mb-1">
              Email Address
            </label>
            <div className="relative flex items-center">
              <Mail className="absolute left-3.5 w-4 h-4 text-zinc-400" />
              <input
                id="auth_email_input"
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={e => setEmail(e.target.value)}
                className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 pl-10 pr-3.5 text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
              />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold uppercase tracking-wider text-zinc-400 mb-1">
              {mode === 'FORGOT_PASSWORD' ? 'New Password' : 'Password'}
            </label>
            <div className="relative flex items-center">
              <Lock className="absolute left-3.5 w-4 h-4 text-zinc-400" />
              <input
                id="auth_password_input"
                type={showPassword ? 'text' : 'password'}
                placeholder="••••••••"
                value={password}
                onChange={e => setPassword(e.target.value)}
                className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 pl-10 pr-10 text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 text-zinc-400 hover:text-white p-1 cursor-pointer"
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          {(mode === 'REGISTER' || mode === 'FORGOT_PASSWORD') && (
            <div>
              <label className="block text-[11px] font-bold uppercase tracking-wider text-zinc-400 mb-1">
                Confirm Password
              </label>
              <div className="relative flex items-center">
                <Lock className="absolute left-3.5 w-4 h-4 text-zinc-400" />
                <input
                  id="auth_confirm_password_input"
                  type={showConfirmPassword ? 'text' : 'password'}
                  placeholder="••••••••"
                  value={confirmPassword}
                  onChange={e => setConfirmPassword(e.target.value)}
                  className="w-full bg-[#161619] border border-white/15 focus:border-white rounded-xl py-2.5 pl-10 pr-10 text-sm text-white placeholder-zinc-500 focus:outline-none transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute right-3 text-zinc-400 hover:text-white p-1 cursor-pointer"
                >
                  {showConfirmPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>
          )}

          {mode === 'LOGIN' && (
            <div className="flex justify-end pt-1">
              <button
                id="forgot_password_button"
                type="button"
                onClick={() => {
                  setMode('FORGOT_PASSWORD');
                  setError(null);
                  setSuccess(null);
                }}
                className="text-xs font-semibold text-zinc-400 hover:text-white transition-colors cursor-pointer"
              >
                Forgot Password?
              </button>
            </div>
          )}

          <div className="pt-3">
            <button
              id="auth_submit_button"
              type="submit"
              disabled={isLoading}
              className="w-full py-3.5 rounded-full bg-gradient-to-b from-white via-zinc-100 to-zinc-300 hover:brightness-105 text-black font-extrabold text-sm shadow-[0_8px_25px_rgba(255,255,255,0.25),inset_0_1px_2px_rgba(255,255,255,1)] border border-white/30 flex items-center justify-center gap-2 cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.34,1.6,0.64,1)] active:scale-[0.98] disabled:opacity-50"
            >
              {isLoading ? (
                <span>Processing...</span>
              ) : (
                <span>
                  {mode === 'LOGIN' && 'Log In'}
                  {mode === 'REGISTER' && 'Create Account'}
                  {mode === 'FORGOT_PASSWORD' && 'Reset Password'}
                </span>
              )}
            </button>
          </div>
        </form>

        {/* Bottom switcher */}
        <div className="mt-5 text-center text-xs text-zinc-400 relative z-10">
          {mode === 'LOGIN' && (
            <p>
              Don't have an account?{' '}
              <button
                id="switch_to_register_link"
                type="button"
                onClick={() => {
                  setMode('REGISTER');
                  setError(null);
                }}
                className="font-bold text-white hover:underline cursor-pointer"
              >
                Sign Up
              </button>
            </p>
          )}

          {mode === 'REGISTER' && (
            <p>
              Already have an account?{' '}
              <button
                id="switch_to_login_link"
                type="button"
                onClick={() => {
                  setMode('LOGIN');
                  setError(null);
                }}
                className="font-bold text-white hover:underline cursor-pointer"
              >
                Log In
              </button>
            </p>
          )}

          {mode === 'FORGOT_PASSWORD' && (
            <p>
              Remembered your password?{' '}
              <button
                id="switch_to_login_from_forgot_link"
                type="button"
                onClick={() => {
                  setMode('LOGIN');
                  setError(null);
                }}
                className="font-bold text-white hover:underline cursor-pointer"
              >
                Back to Log In
              </button>
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

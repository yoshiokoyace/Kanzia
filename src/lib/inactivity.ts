import { useEffect, useRef } from 'react';

export const INACTIVITY_TIMEOUT_MS = 3 * 60 * 1000; // 3 minutes = 180,000 ms

export function useInactivityLock(isLoggedIn: boolean, onLock: () => void) {
  const lastActiveRef = useRef<number>(Date.now());
  const timerRef = useRef<number | null>(null);

  useEffect(() => {
    if (!isLoggedIn) return;

    const resetTimer = () => {
      lastActiveRef.current = Date.now();
      if (timerRef.current) {
        window.clearTimeout(timerRef.current);
      }
      timerRef.current = window.setTimeout(() => {
        onLock();
      }, INACTIVITY_TIMEOUT_MS);
    };

    // User interaction events
    const events = ['mousedown', 'mousemove', 'keydown', 'scroll', 'touchstart', 'click'];
    const handleActivity = () => {
      resetTimer();
    };

    events.forEach(evt => window.addEventListener(evt, handleActivity, { passive: true }));

    // Visibility / background check
    const handleVisibilityChange = () => {
      if (document.hidden) {
        // Tab went to background
        lastActiveRef.current = Date.now();
      } else {
        // Returned to tab: check if elapsed time exceeds timeout
        const elapsed = Date.now() - lastActiveRef.current;
        if (elapsed >= INACTIVITY_TIMEOUT_MS) {
          onLock();
        } else {
          resetTimer();
        }
      }
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);
    resetTimer();

    // Dev helper for immediate testing
    (window as unknown as { __triggerInactivityLock?: () => void }).__triggerInactivityLock = () => {
      onLock();
    };

    return () => {
      if (timerRef.current) window.clearTimeout(timerRef.current);
      events.forEach(evt => window.removeEventListener(evt, handleActivity));
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      delete (window as unknown as { __triggerInactivityLock?: () => void }).__triggerInactivityLock;
    };
  }, [isLoggedIn, onLock]);
}

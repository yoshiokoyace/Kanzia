import React, { useState, useEffect, useRef, useCallback } from 'react';

interface PullToRefreshProps {
  children: React.ReactNode;
  onRefresh: () => Promise<void> | void;
  isRefreshingExternal?: boolean;
}

// Distance in pixels to pull down before refresh activates
const PULL_THRESHOLD = 65;
const DEADZONE = 8; // Small deadzone so normal clicks or slight taps don't trigger it

export const PullToRefresh: React.FC<PullToRefreshProps> = ({
  children,
  onRefresh,
  isRefreshingExternal = false,
}) => {
  const [pullProgress, setPullProgress] = useState(0); // 0 to 1+
  const [isPulling, setIsPulling] = useState(false);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const startYRef = useRef<number>(0);
  const startXRef = useRef<number>(0);
  const isDraggingRef = useRef<boolean>(false);
  const isRefreshingRef = useRef<boolean>(false);
  const progressRef = useRef<number>(0);
  const wheelAccumulatorRef = useRef<number>(0);
  const wheelTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  isRefreshingRef.current = isRefreshing;

  const triggerRefresh = useCallback(async () => {
    if (isRefreshingRef.current) return;
    setIsRefreshing(true);
    setIsPulling(false);
    isDraggingRef.current = false;
    setPullProgress(1);
    progressRef.current = 1;

    try {
      // Runs the refresh and ensures a smooth loading animation until content is ready
      await Promise.all([
        Promise.resolve(onRefresh()),
        new Promise(resolve => setTimeout(resolve, 500)),
      ]);
    } catch (e) {
      console.error('Pull to refresh failed:', e);
    } finally {
      // Content is ready: immediately disappear cleanly
      setIsRefreshing(false);
      setPullProgress(0);
      progressRef.current = 0;
      isDraggingRef.current = false;
    }
  }, [onRefresh]);

  // Sync with external refreshing state if applicable
  useEffect(() => {
    if (isRefreshingExternal && !isRefreshing) {
      setIsRefreshing(true);
      setPullProgress(1);
    } else if (!isRefreshingExternal && isRefreshing && !isPulling) {
      setIsRefreshing(false);
      setPullProgress(0);
    }
  }, [isRefreshingExternal, isRefreshing, isPulling]);

  useEffect(() => {
    const getScrollTop = (): number => {
      return (
        window.scrollY ||
        window.pageYOffset ||
        document.documentElement.scrollTop ||
        document.body.scrollTop ||
        0
      );
    };

    const isAtTop = () => getScrollTop() <= 4;

    // Ignore interactive elements like buttons, form inputs, or dialogs
    const isInteractive = (target: EventTarget | null): boolean => {
      if (!target || !(target instanceof HTMLElement)) return false;
      return Boolean(
        target.closest('button, input, textarea, select, a, [role="button"], [role="dialog"]')
      );
    };

    // Unified pointer handlers (Touch, Mouse, Pen)
    const handlePointerDown = (e: PointerEvent) => {
      if (isRefreshingRef.current) return;
      if (isInteractive(e.target)) return;

      if (isAtTop()) {
        startYRef.current = e.clientY;
        startXRef.current = e.clientX;
        isDraggingRef.current = true;
      }
    };

    const handlePointerMove = (e: PointerEvent) => {
      if (!isDraggingRef.current || isRefreshingRef.current) return;

      const currentY = e.clientY;
      const currentX = e.clientX;
      const deltaY = currentY - startYRef.current;
      const deltaX = Math.abs(currentX - startXRef.current);

      // Cancel if horizontal swipe is detected
      if (deltaX > Math.abs(deltaY) && deltaY > 0) {
        isDraggingRef.current = false;
        setIsPulling(false);
        setPullProgress(0);
        progressRef.current = 0;
        return;
      }

      if (deltaY > DEADZONE && isAtTop()) {
        const effectivePull = deltaY - DEADZONE;
        const progress = Math.min(effectivePull / PULL_THRESHOLD, 1.25);

        setIsPulling(true);
        progressRef.current = progress;
        setPullProgress(progress);

        if (e.cancelable && effectivePull > 12) {
          e.preventDefault();
        }
      } else if (deltaY <= 0) {
        if (isDraggingRef.current && progressRef.current > 0) {
          setIsPulling(false);
          setPullProgress(0);
          progressRef.current = 0;
        }
      }
    };

    const handlePointerUp = () => {
      if (!isDraggingRef.current || isRefreshingRef.current) return;
      isDraggingRef.current = false;

      if (progressRef.current >= 1.0) {
        triggerRefresh();
      } else {
        setIsPulling(false);
        setPullProgress(0);
        progressRef.current = 0;
      }
    };

    // Touch handlers fallback
    const handleTouchStart = (e: TouchEvent) => {
      if (isRefreshingRef.current || e.touches.length !== 1) return;
      if (isInteractive(e.target)) return;

      if (isAtTop()) {
        startYRef.current = e.touches[0].clientY;
        startXRef.current = e.touches[0].clientX;
        isDraggingRef.current = true;
      }
    };

    const handleTouchMove = (e: TouchEvent) => {
      if (!isDraggingRef.current || isRefreshingRef.current || e.touches.length !== 1) return;

      const currentY = e.touches[0].clientY;
      const currentX = e.touches[0].clientX;
      const deltaY = currentY - startYRef.current;
      const deltaX = Math.abs(currentX - startXRef.current);

      if (deltaX > Math.abs(deltaY) && deltaY > 0) {
        isDraggingRef.current = false;
        setIsPulling(false);
        setPullProgress(0);
        progressRef.current = 0;
        return;
      }

      if (deltaY > DEADZONE && isAtTop()) {
        const effectivePull = deltaY - DEADZONE;
        const progress = Math.min(effectivePull / PULL_THRESHOLD, 1.25);

        setIsPulling(true);
        progressRef.current = progress;
        setPullProgress(progress);

        if (e.cancelable && effectivePull > 15) {
          e.preventDefault();
        }
      }
    };

    const handleTouchEnd = () => {
      if (!isDraggingRef.current || isRefreshingRef.current) return;
      isDraggingRef.current = false;

      if (progressRef.current >= 1.0) {
        triggerRefresh();
      } else {
        setIsPulling(false);
        setPullProgress(0);
        progressRef.current = 0;
      }
    };

    // Trackpad / Wheel overscroll support when at top of page
    const handleWheel = (e: WheelEvent) => {
      if (isRefreshingRef.current) return;

      // Scrolling upwards/pulling down while already at the top
      if (isAtTop() && e.deltaY < 0) {
        wheelAccumulatorRef.current += Math.abs(e.deltaY) * 0.4;
        const progress = Math.min(wheelAccumulatorRef.current / PULL_THRESHOLD, 1.25);

        setIsPulling(true);
        progressRef.current = progress;
        setPullProgress(progress);

        if (wheelTimerRef.current) clearTimeout(wheelTimerRef.current);

        if (progress >= 1.0) {
          wheelAccumulatorRef.current = 0;
          triggerRefresh();
        } else {
          wheelTimerRef.current = setTimeout(() => {
            wheelAccumulatorRef.current = 0;
            setIsPulling(false);
            setPullProgress(0);
            progressRef.current = 0;
          }, 320);
        }
      }
    };

    window.addEventListener('pointerdown', handlePointerDown, { passive: true });
    window.addEventListener('pointermove', handlePointerMove, { passive: false });
    window.addEventListener('pointerup', handlePointerUp);
    window.addEventListener('pointercancel', handlePointerUp);

    window.addEventListener('touchstart', handleTouchStart, { passive: true });
    window.addEventListener('touchmove', handleTouchMove, { passive: false });
    window.addEventListener('touchend', handleTouchEnd);
    window.addEventListener('touchcancel', handleTouchEnd);

    window.addEventListener('wheel', handleWheel, { passive: true });

    return () => {
      window.removeEventListener('pointerdown', handlePointerDown);
      window.removeEventListener('pointermove', handlePointerMove);
      window.removeEventListener('pointerup', handlePointerUp);
      window.removeEventListener('pointercancel', handlePointerUp);

      window.removeEventListener('touchstart', handleTouchStart);
      window.removeEventListener('touchmove', handleTouchMove);
      window.removeEventListener('touchend', handleTouchEnd);
      window.removeEventListener('touchcancel', handleTouchEnd);

      window.removeEventListener('wheel', handleWheel);
      if (wheelTimerRef.current) clearTimeout(wheelTimerRef.current);
    };
  }, [triggerRefresh]);

  const isVisible = isRefreshing || (isPulling && pullProgress > 0.1);

  // Position cleanly below the top navbar (fixed top offset)
  const indicatorTranslateY = isRefreshing
    ? 24
    : Math.min(pullProgress * 42, 46);

  // SVG circular gauge geometry
  const radius = 9;
  const strokeWidth = 2.5;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference * (1 - Math.min(pullProgress, 1));

  return (
    <div className="relative w-full overflow-visible">
      {/* 
        Single Floating Loading Circle (No texts)
        - Pops up when scrolling/pulling down
        - Stays spinning as long as page is refreshing
        - Disappears immediately when the page is ready
      */}
      <div
        id="pull-to-refresh-indicator"
        aria-hidden={!isVisible}
        className={`fixed top-16 inset-x-0 z-40 flex justify-center pointer-events-none transition-all ${
          isPulling
            ? 'duration-75 ease-out'
            : 'duration-250 ease-out'
        }`}
        style={{
          transform: `translate3d(0, ${indicatorTranslateY}px, 0) scale(${isVisible ? 1 : 0.6})`,
          opacity: isVisible ? 1 : 0,
        }}
      >
        <div
          className="w-10 h-10 rounded-full bg-[#16161a]/95 border border-white/25 shadow-[0_8px_25px_rgba(0,0,0,0.85),0_0_15px_rgba(255,255,255,0.08)] backdrop-blur-xl flex items-center justify-center"
        >
          {isRefreshing ? (
            // Indeterminate spinning circle while page refreshes
            <svg
              className="w-5 h-5 animate-spin text-white"
              viewBox="0 0 24 24"
              fill="none"
            >
              <circle
                className="opacity-20"
                cx="12"
                cy="12"
                r="9"
                stroke="currentColor"
                strokeWidth="2.75"
              />
              <path
                className="opacity-95"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
          ) : (
            // Progress circle as user pulls down
            <svg className="w-5 h-5 transform -rotate-90" viewBox="0 0 24 24">
              <circle
                cx="12"
                cy="12"
                r={radius}
                fill="none"
                stroke="#27272a"
                strokeWidth={strokeWidth}
              />
              <circle
                cx="12"
                cy="12"
                r={radius}
                fill="none"
                stroke="#ffffff"
                strokeWidth={strokeWidth}
                strokeDasharray={circumference}
                strokeDashoffset={strokeDashoffset}
                strokeLinecap="round"
                className="transition-all duration-75"
              />
            </svg>
          )}
        </div>
      </div>

      {/* 
        Child Content: completely stable without any layout jumps or flickering
      */}
      <div id="pull-to-refresh-content" className="w-full">
        {children}
      </div>
    </div>
  );
};

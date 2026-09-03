# Finance Ledger

A modern, high-performance personal finance and portfolio tracker web application built with **React**, **TypeScript**, and **Tailwind CSS**.

---

## Overview

**Finance Ledger** helps you track day-to-day income and expenses, analyze monthly cash flow distributions, and monitor your long-term investment portfolio and stock deposits in a unified, distraction-free interface.

---

## Features

### 1. Financial Ledger & Cash Flow
- **Daily Transaction Logging**: Record income and expense entries with custom dates, monetary amounts, notes, and tags.
- **Categorization**: Group expenditures by Food, Transport, Shopping, Entertainment, Bills, Salary, Investment, and more.
- **Search & Filter**: Search transactions instantly by keyword and filter by specific categories.
- **Month-by-Month Navigation**: Browse financial periods with automatic net balance, total income, and total expense recalculations.

### 2. Analytics & Visualizations
- **Expense Breakdown Chart**: Categorical distribution visualization using interactive donut and pie charts.
- **Monthly Trajectory**: Compare income vs. expense cash flow patterns across periods.
- **Savings Margin**: Real-time margin metrics and financial health indicators.

### 3. Investment Portfolio & Asset Allocation
- **Aggregated Portfolio Valuation**: Instant summary of total asset deposits and holdings.
- **Custom Stock Watchlist**: Add and track custom market tickers (e.g., NVDA, AAPL, MSFT, BTC).
- **Deposit Allocations**: Log periodic deposit transactions linked to selected portfolio assets.

### 4. Gestures & Mobile Experience
- **Single-Circle Pull-to-Refresh**: Clean, modern pull-down gesture that smoothly displays a minimal loading circle at the top while syncing, and disappears as soon as content is updated.
- **Zero-Flicker Layout**: Hardware-accelerated transitions that prevent page jumping or screen stutter.
- **Responsive Design**: Mobile-first navigation with bottom thumb controls and expanded desktop view.

### 5. Security & Session Management
- **Inactivity Auto-Lock**: Automatically secures the session after 3 minutes of inactivity.
- **Authentication Modal**: Client sign-in, registration, and password reset flows.
- **Cloud & Local Persistence**: Syncs data seamlessly across cloud storage with offline fallback.

---

## Tech Stack

- **Frontend**: [React 18](https://react.dev/) + [TypeScript](https://www.typescriptlang.org/)
- **Build Tool**: [Vite](https://vitejs.dev/)
- **Styling**: [Tailwind CSS v4](https://tailwindcss.com/)
- **Charts & Data Viz**: [Recharts](https://recharts.org/)
- **Icons**: [Lucide React](https://lucide.dev/)
- **Motion**: [Motion](https://motion.dev/)

---

## Getting Started

### Prerequisites

Make sure you have [Node.js](https://nodejs.org/) (v18 or newer) and `npm` installed.

### Installation

1. Clone the repository or export it from Google AI Studio:
   ```bash
   git clone https://github.com/your-username/finance-ledger.git
   cd finance-ledger
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the local development server:
   ```bash
   npm run dev
   ```
   The application will be running at `http://localhost:3000`.

### Building for Production

To create an optimized production build:
```bash
npm run build
```

To preview the production build locally:
```bash
npm run preview
```

---

## Project Structure

```
├── src/
│   ├── components/       # UI screens, modals, charts, and navigation components
│   │   ├── HomeScreen.tsx
│   │   ├── StatsScreen.tsx
│   │   ├── InvestmentsScreen.tsx
│   │   ├── Navbar.tsx
│   │   ├── BottomNav.tsx
│   │   ├── PullToRefresh.tsx
│   │   ├── PortfolioPieChart.tsx
│   │   └── ...
│   ├── services/         # Storage and synchronization services
│   ├── types.ts          # TypeScript models and interfaces
│   ├── App.tsx           # Main application shell
│   └── main.tsx          # Application entry point
├── index.html            # Main HTML document
├── package.json          # Project dependencies and scripts
├── tsconfig.json         # TypeScript compiler configuration
└── vite.config.ts        # Vite configuration
```

---

## License

This project is open-source and available under the [MIT License](LICENSE).

# Finance Ledger (Kanzia)

A personal finance and investment tracking application rewritten in React, TypeScript, and Tailwind CSS, preserving all core business logic, data models, and features from the original Kanzia project.

## Features

- **Financial Ledger & Cash Flow**:
  - Track monthly income and expenses with date, amount, note, and categorical tags.
  - Multi-category classification: Food, Transport, Shopping, Entertainment, Bills, Salary, Investment, and Other.
  - Interactive month navigation and instant cash flow calculations.
  - Real-time search and category filtering.

- **Financial Analytics & Visualization**:
  - Monthly net cash flow and savings margin percentages.
  - Expense category breakdown donut chart with categorical proportions.
  - Income versus expense trajectory bar charts across monthly periods.
  - Detailed metrics and progress allocations per category.

- **Investment Portfolio & Deposits**:
  - Portfolio tracking with aggregate asset valuation and deposit transaction counts.
  - Stock watchlist and custom ticker integration (with support for adding tickers like NVDA, AAPL, MSFT, etc.).
  - Periodic investment deposit logging and asset allocation percentages.

- **Session Security & Inactivity Management**:
  - Client authentication flow supporting Login, Account Registration, and Password Reset.
  - Session auto-lock after 3 minutes of inactivity (matching Android InactivityManager).
  - Profile modal with user badge, cache sync controls, and emergency local data wipe.

## Tech Stack

- **Framework**: React 18 with Vite
- **Language**: TypeScript
- **Styling**: Tailwind CSS with the authentic "Sophisticated" dark palette
- **Data Visualization**: Recharts
- **Icons**: Lucide React
- **Storage**: Local persistent storage with pre-seeded demonstration data

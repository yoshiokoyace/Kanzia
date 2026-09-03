import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  define: {
    'process.env.SUPABASE_URL': JSON.stringify(process.env.SUPABASE_URL || 'https://wsrzdnxzjjgqsrsaohgc.supabase.co'),
    'process.env.SUPABASE_ANON_KEY': JSON.stringify(process.env.SUPABASE_ANON_KEY || 'sb_secret__X1aOBmgfgecEAM6Y7vikQ_lWCD7GKd'),
    'import.meta.env.VITE_SUPABASE_URL': JSON.stringify(process.env.SUPABASE_URL || 'https://wsrzdnxzjjgqsrsaohgc.supabase.co'),
    'import.meta.env.VITE_SUPABASE_ANON_KEY': JSON.stringify(process.env.SUPABASE_ANON_KEY || 'sb_secret__X1aOBmgfgecEAM6Y7vikQ_lWCD7GKd'),
  },
  server: {
    host: '0.0.0.0',
    port: 3000,
  }
});

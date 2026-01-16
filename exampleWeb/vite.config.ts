import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

const extensions = ['.web.tsx', '.tsx', '.web.ts', '.ts', '.web.jsx', '.jsx', '.web.js', '.js'];

export default defineConfig({
  plugins: [
    react({
      babel: {
        parserOpts: {
          plugins: ['decorators-legacy'],
        },
      },
    }),
  ],
  resolve: {
    extensions,
    alias: {
      'react-native': 'react-native-web',
      'react-native-hole-view': path.resolve(__dirname, '..', 'src'),
    },
  },
  optimizeDeps: {
    esbuildOptions: {
      jsx: 'automatic',
      resolveExtensions: extensions,
      loader: { '.js': 'jsx' },
      define: {
        global: 'window',
      },
    },
  },
  server: {
    host: 'localhost',
    port: 5174,
  },
});


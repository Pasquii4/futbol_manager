// Re-export from the canonical lib/api.ts for compatibility
export * from '@/lib/api';
export { default as api } from '@/lib/api';

// Alias types used by dashboard
export type { Match, StandingsItem } from '@/lib/api';

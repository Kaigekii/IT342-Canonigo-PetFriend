import {
  formatCurrency,
  formatDuration,
  formatRating,
  isPastDate,
  isToday,
} from '../formatting';

describe('formatting utilities', () => {
  it('formats currency values with the expected symbol', () => {
    expect(formatCurrency(1250)).toBe('₱1250.00');
    expect(formatCurrency('42.5', 'USD')).toBe('$42.50');
  });

  it('formats duration across days and hours', () => {
    expect(formatDuration('2026-05-10T08:00:00Z', '2026-05-11T11:00:00Z')).toBe('1 day 3h');
    expect(formatDuration('2026-05-10T08:00:00Z', '2026-05-10T12:00:00Z')).toBe('4h');
  });

  it('formats ratings to one decimal place', () => {
    expect(formatRating(4.256)).toBe('4.3 / 5.0');
  });

  it('detects past and current dates', () => {
    const now = new Date();
    const past = new Date(now.getTime() - 60 * 60 * 1000);
    const future = new Date(now.getTime() + 60 * 60 * 1000);

    expect(isPastDate(past)).toBe(true);
    expect(isPastDate(future)).toBe(false);
    expect(isToday(now)).toBe(true);
  });
});
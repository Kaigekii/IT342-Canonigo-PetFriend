import React from 'react';
import { render, screen } from '@testing-library/react';
import { PawIcon } from '../PawIcon';

describe('PawIcon', () => {
  it('renders an SVG with default size and color', () => {
    const { container } = render(<PawIcon />);
    const byRole = screen.queryByRole('img', { hidden: true });
    const svg = byRole || container.querySelector('svg');
    expect(svg).toBeTruthy();
    expect(svg.getAttribute('width')).toBe('24');
  });
});

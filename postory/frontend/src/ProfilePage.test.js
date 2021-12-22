import React from 'react';
import { render, screen } from '@testing-library/react';
import * as ProfilePage from './ProfilePage.js'

describe('ProfilePageUpper', () => {
  test('renders ProfilePage component', () => {
    render(<ProfilePage.ProfilePageUpper />);

    expect(screen.getByText('Follow')).toBeInTheDocument();
    expect(screen.getByRole('button')).toBeInTheDocument();
  });
});
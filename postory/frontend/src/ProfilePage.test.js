import React from 'react';
import { render, screen } from '@testing-library/react';
import * as ProfilePage from './ProfilePage.js'

describe('ProfilePageUpper', () => {
  test('renders ProfilePage component', () => {
    render(<ProfilePage.ProfilePageUpper />);

    screen.debug();
  });
});
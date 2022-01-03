// References: https://reactjs.org/docs/testing-recipes.html#data-fetching

import React from 'react';
import { render, screen } from '@testing-library/react';
import { unmountComponentAtNode } from "react-dom";
import userEvent from '@testing-library/user-event'
import * as ProfilePage from './ProfilePage.js'

let container = null;

beforeEach(() => {
    // setup a DOM element as a render target
    container = document.createElement("div");
    document.body.appendChild(container);
});

afterEach(() => {
    // cleanup on exiting
    unmountComponentAtNode(container);
    container.remove();
    container = null;
});

describe('Profile page tests', () => {
  test('Profile page components render successfully', () => {
    render(<ProfilePage.ProfilePageUpper />, container);
    expect(screen.getByText('Follow')).toBeInTheDocument();
    expect(screen.getByRole('button')).toBeInTheDocument();
  });

  test('On click follow button successfully', () => {
    render(<ProfilePage.ProfilePageUpper />, container);

    userEvent.click(screen.getByText('Follow'))
    expect(screen.getByText("You Have Successfully Followed !")).toBeInTheDocument();
  });

  test('fetches mock user info', () => {
    const fakeUser = {
        "id": 555555,
        "username": "mock_test_user",
        "password": "mock_test_user",
        "name": "mock_test_user",
        "surname": "mock_test_user",
        "email": "mock_test_user@mock_test_user.com",
        "followedUsers": [],
        "followerUsers": [],
        "posts": [],
        "savedPosts": [],
        "likedPosts": [],
        "comments": [],
        "isBanned": false,
        "isAdmin": false,
        "isPrivate": false,
        "is_active": true,
        "userPhoto": ""
    };
      jest.spyOn(global, "fetch").mockImplementation(() =>
        Promise.resolve({
          json: () => Promise.resolve(fakeUser)
        })
      );
  });
});
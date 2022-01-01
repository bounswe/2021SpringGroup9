import {fireEvent, screen } from '@testing-library/react';
import { render, unmountComponentAtNode } from "react-dom";
import NavbarMenu from './NavbarMenu';




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

test('Renders the menu', () => {
    render(<NavbarMenu noNavigate/>, container);
    const c = container.firstChild.children;
    expect(c[0].text).toBe('Home');
    expect(c[1].text).toBe('Discover');
    expect(c[2].text).toBe('Create a Post');
    expect(c[3].text).toBe('My Profile Page');
  }
);





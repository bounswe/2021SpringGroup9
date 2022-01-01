import {fireEvent, screen } from '@testing-library/react';
import { render, unmountComponentAtNode } from "react-dom";
import NavbarMenu from './NavbarMenu';
import {MemoryRouter, Routes, Route} from "react-router-dom";




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
    render(<MemoryRouter>
      <NavbarMenu noNavigate/>
    </MemoryRouter>, container);
    const c = container.firstChild.children;
    expect(c[0].text).toBe('Home');
    expect(c[1].text).toBe('Discover');
    expect(c[2].text).toBe('Create a Post');
    expect(c[3].text).toBe('My Profile Page');
    expect(c[0].text).not.toBe('Random text');
  }
);





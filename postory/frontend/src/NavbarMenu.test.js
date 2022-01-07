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
  /*
    ID            : TC_F_14
    Title         : Navbar Menu/Render test
    Test Priority : High
    Module Name   : Frontend - Navbar Menu (all pages)
    Description   : Checks if the necessary links on the navbar menu is rendered properly
    */
  //MemoryRouter is needed because of useNavigate hook.
    render(<MemoryRouter>
      <NavbarMenu noNavigate/>
    </MemoryRouter>, container);
    const c = container.firstChild.children;
    const links = ['Home', 'Discover', 'Activities', 'Create a Post', 'My Profile Page', 'Sign In', 'Sign Up', 'Logout'];
    for(let i = 0; i<links.length;i++)
      expect(c[i].text).toBe(links[i]);
    expect(c[0].text).not.toBe('Random text');
  }
);





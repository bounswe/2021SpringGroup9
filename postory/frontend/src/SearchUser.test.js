import {fireEvent, screen } from '@testing-library/react';
import { render, unmountComponentAtNode } from "react-dom";
import SearchUserComponent from './SearchUser';




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

test('Renders placeholder text', () => {
    /*
    ID            : TC_F_15
    Title         : User Search/Render test
    Test Priority : High
    Module Name   : Frontend - User Search (all pages)
    Description   : Checks if the component is rendered properly.
    */
    render(<SearchUserComponent />, container);
    expect(screen.getByPlaceholderText('Type to search users...', {exact: false})).toBeVisible;
  }
);

test('Able to input text', () => {
  /*
    ID            : TC_F_16
    Title         : User Search/Input test
    Test Priority : High
    Module Name   : Frontend - User Search (all pages)
    Description   : Checks if the user is able to input text.
    */
    render(<SearchUserComponent />, container);
    let element = screen.getByPlaceholderText('Type to search users...', {exact: false});
    fireEvent.change(element, {target : {value:'Test'}});
    expect(element.value).toBe('Test');
  }
);



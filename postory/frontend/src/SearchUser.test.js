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
    render(<SearchUserComponent />, container);
    expect(screen.getByPlaceholderText('Type to search users...', {exact: false})).toBeVisible;
  }
);

test('Able to input text', () => {
    render(<SearchUserComponent />, container);
    let element = screen.getByPlaceholderText('Type to search users...', {exact: false});
    fireEvent.change(element, {target : {value:'Test'}});
    expect(element.value).toBe('Test');
  }
);



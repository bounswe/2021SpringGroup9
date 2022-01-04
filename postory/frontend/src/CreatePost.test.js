import {fireEvent, screen, waitFor, userEvent  } from '@testing-library/react';
import { render, unmountComponentAtNode} from "react-dom";
import CreatePost from './CreatePost';
import {MemoryRouter, Routes, Route} from "react-router-dom";




let container = null;
let spy = jest.spyOn(global, 'fetch').mockResolvedValue({
    json: jest.fn().mockResolvedValue({})
});

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
    //jest.restoreAllMocks();
  });

//It is not easy to test react-google-maps javascript api with jest because it uses internal functions that are not transparent.

test('Renders the page without errors', () => {
    render(<MemoryRouter><CreatePost /></MemoryRouter>, container);
  }
);

test('Sends post creation request', async () => {
    render(<MemoryRouter><CreatePost /></MemoryRouter>, container);
    let title = screen.getByLabelText('Post title');
    let body = screen.getByLabelText('Post body');
    
    let post_button = container.querySelector('.homePageCreatePostButton')

    //Post title and body fields are mandatory.
    fireEvent.change(title, {target : {value:'Title'}});
    fireEvent.change(body, {target : {value:'Body'}});
    post_button.fireEvent('onClick');
    //console.log(container.querySelector('.homePageCreatePostButton'));
    await waitFor(() => {},  {timeout:5000});
    //The fetchs request is successfully done.
    expect(spy).toHaveBeenCalledTimes(1);
  }
);


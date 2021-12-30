import {findByText, fireEvent, screen} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
import userEvent from '@testing-library/user-event'
import DiscoverFilter from "./DiscoverFilter";

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

test('Filtering components render successfully', () => {
    render(<DiscoverFilter />, container);
    expect(screen.getByText('Tags')).toBeInTheDocument();
    expect(screen.getByText('Date')).toBeInTheDocument();
    expect(screen.getByText('Keyword')).toBeInTheDocument();
    expect(screen.getByText('User')).toBeInTheDocument();
    expect(screen.getByText('Area (km)')).toBeInTheDocument();
});

test('Enters a tag successfully', () => {
    render(<DiscoverFilter />, container);
    const input = screen.getByPlaceholderText('Enter a tag')
    userEvent.type(input, 'tag1')
    expect(input.value).toBe('tag1')
})

test('Clicks on add a tag successfully', () => {
    render(<DiscoverFilter />, container);
    const input = screen.getByPlaceholderText('Enter a tag')
    userEvent.type(input, 'tag1')
    userEvent.click(screen.getByText('Add Tag'))
    expect(screen.getByText('tag1')).toBeInTheDocument();
})
//test('Redirecting to different page to show resulting posts successfully', async () => {
//    render(<DiscoverFilter />, container);
//  
//    userEvent.click(screen.getByText('Show Resulting Posts on a Different Page'))
//    await screen.findByText('Navigating to filteredPosts page');
//})

//test('Correct username & password results with success', async () => {
//    render(<SignIn />, container);
//    fireEvent.change(
//        screen.getByLabelText('Username or E-mail:'),
//        {target: {value: 'amelih6@gmail.com'}}
//    );
//    fireEvent.change(
//        screen.getByLabelText('Password:'),
//        {target: {value: 'MyPassword123%%'}}
//    );
//
//    fireEvent.click(screen.getByRole('button'))
//    await screen.findByText('Sign in success.')
//})
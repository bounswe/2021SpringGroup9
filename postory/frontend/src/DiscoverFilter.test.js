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

test('Clicks on add a keyword successfully', () => {
    render(<DiscoverFilter />, container);
    const input_keyword = screen.getByPlaceholderText('Enter a keyword')
    userEvent.type(input_keyword, 'keyword3')
    userEvent.click(screen.getByText('Add'))
    expect(screen.getByText('keyword3')).toBeInTheDocument();
})

test('Clicks on add a user successfully', () => {
    render(<DiscoverFilter />, container);
    const input_user = screen.getByPlaceholderText('Enter a user')
    userEvent.type(input_user, 'user1')
    userEvent.click(screen.getByText('Add User'))
    expect(screen.getByText('user1')).toBeInTheDocument();
})
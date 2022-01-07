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
    /*
    ID            : TC_F_4
    Title         : Discover/Filter - Render Test 
    Test Priority : High
    Module Name   : Frontend - Discover/Filter
    Description   : Checks whether the discovery page and filter components have been rendered successfully.
    */
    render(<DiscoverFilter />, container);
    expect(screen.getByText('Tags')).toBeInTheDocument();
    expect(screen.getByText('Date')).toBeInTheDocument();
    expect(screen.getByText('Keyword')).toBeInTheDocument();
    expect(screen.getByText('User')).toBeInTheDocument();
    expect(screen.getByText('Area (km)')).toBeInTheDocument();
});

test('Enters a tag successfully', () => {
    /*
    ID            : TC_F_5
    Title         : Discover/Filter - Input Filter Test
    Test Priority : High
    Module Name   : Frontend - Discover/Filter
    Description   : Simulates a user and tries to enter a tag as input
    */
    render(<DiscoverFilter />, container);
    const input = screen.getByPlaceholderText('Enter a tag')
    userEvent.type(input, 'tag1')
    expect(input.value).toBe('tag1')
})

test('Clicks on add a tag successfully', () => {
    /*
    ID            : TC_F_6
    Title         : Discover/Filter - Add Filter Test 1 
    Test Priority : High
    Module Name   : Frontend - Discover/Filter
    Description   : Simulates a user, tries to enter a tag as input and then by clicking add button adds it.
    */
    render(<DiscoverFilter />, container);
    const input = screen.getByPlaceholderText('Enter a tag')
    userEvent.type(input, 'tag1')
    userEvent.click(screen.getByText('Add Tag'))
    expect(screen.getByText('tag1')).toBeInTheDocument();
})

test('Clicks on add a keyword successfully', () => {
    /*
    ID            : TC_F_7
    Title         : Discover/Filter - Add Filter Test 2
    Test Priority : High
    Module Name   : Frontend - Discover/Filter
    Description   : Simulates a user, tries to enter a keyword as input and then by clicking add button adds it.
    */
    render(<DiscoverFilter />, container);
    const input_keyword = screen.getByPlaceholderText('Enter a keyword')
    userEvent.type(input_keyword, 'keyword3')
    userEvent.click(screen.getByText('Add'))
    expect(screen.getByText('keyword3')).toBeInTheDocument();
})

test('Clicks on add a user successfully', () => {
    /*
    ID            : TC_F_8
    Title         : Discover/Filter - Add Filter Test 3 
    Test Priority : High
    Module Name   : Frontend - Discover/Filter
    Description   : Simulates a user, tries to enter a user as input and then by clicking add button adds it.
    */
    render(<DiscoverFilter />, container);
    const input_user = screen.getByPlaceholderText('Enter a user')
    userEvent.type(input_user, 'user1')
    userEvent.click(screen.getByText('Add User'))
    expect(screen.getByText('user1')).toBeInTheDocument();
})

test('Clicks on clear filters and clears the filters successfully', () => {
    /*
    ID            : TC_F_9
    Title         : Discover/Filter - Clear Filters Test
    Test Priority : Medium
    Module Name   : Frontend - Discover/Filter
    Description   : Simulates a user and clicks on clear filters button. Expects all filters to be cleared.
    */
    render(<DiscoverFilter />, container);
    const input = screen.getByPlaceholderText('Enter a tag')
    userEvent.type(input, 'tag1')
    userEvent.click(screen.getByText('Add Tag'))
    expect(screen.getByText('tag1')).toBeInTheDocument();
    userEvent.click(screen.getByText('Clear Filters'))
    expect(screen.queryByText('tag1')).toBeNull();
})
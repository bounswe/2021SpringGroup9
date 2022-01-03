import {fireEvent, queryByText, screen, waitForElementToBeRemoved} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
import TimeChooser from "./TimeChooser";
import ActivityStream from "./ActivityStream";

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

test('ActivityStream component renders successfully.', () => {
    render(<ActivityStream />, container)
    expect(screen.getByText('Own')).not.toThrow()
})

test('ActivityStream is initially loading.', () => {
    render(<ActivityStream />, container)
    expect(screen.getByText('Loading...')).not.toThrow()
})

test('Loading text disappears at some point.', async () => {
    render(<ActivityStream />, container)

    // For every tab, it either gets replaced by a list of activities or by empty tab (no activities)
    await waitForElementToBeRemoved(() => screen.queryByText('Loading...'))
})

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

/*
    ID            : TC_F_19
    Title         : Rendering of ActivityStream component
    Test Priority : High
    Module Name   : Frontend - ActivityStream page
    Description   : Checks whether the ActivityStream component renders successfully.
*/
test('ActivityStream component renders successfully.', () => {
    render(<ActivityStream />, container)
    expect(screen.getByText('Own')).not.toThrow()
})

/*
    ID            : TC_F_20
    Title         : Initial loading status of ActivityStream component
    Test Priority : High
    Module Name   : Frontend - ActivityStream page
    Description   : Checks whether the ActivityStream is initially loading
*/
test('ActivityStream is initially loading.', () => {
    render(<ActivityStream />, container)
    expect(screen.getByText('Loading...')).not.toThrow()
})

/*
    ID            : TC_F_21
    Title         : Loading text disappearance of ActivityStream component
    Test Priority : High
    Module Name   : Frontend - ActivityStream page
    Description   : Checks whether the 'Loading...' text of the ActivityStream tabs are eventually disappearing
*/
test('Loading text disappears at some point.', async () => {
    render(<ActivityStream />, container)

    // For every tab, it either gets replaced by a list of activities or by empty tab (no activities)
    await waitForElementToBeRemoved(() => screen.queryByText('Loading...'))
})

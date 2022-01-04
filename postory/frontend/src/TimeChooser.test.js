import {fireEvent, screen} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
import TimeChooser from "./TimeChooser";
import SignUp from "./SignUp";

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

test('TimeChooser component renders successfully', () => {
    render(<TimeChooser />, container)
    expect(screen.getByText('Choose start and end years', {exact: false})).not.toThrow()
})

test('Month selection appears only after selecting start and end years', () => {
    render(<TimeChooser />, container)

    // Month selection doesn't appear
    expect(screen.getByAltText('Starting month')).toThrow()

    // Entering starting and ending years
    const enter = (field, text) => fireEvent.change(field, {target: {value: text}})
    enter(screen.getByAltText('Starting year'), '1995')
    enter(screen.getByAltText('Ending year'), '2018')

    // Month selection appears
    expect(screen.getByAltText('Starting month')).not.toThrow()
})


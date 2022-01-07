import {fireEvent, getByLabelText, screen} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
import ForgotPassword from "./ForgotPassword";

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
    ID            : TC_F_22
    Title         : Rendering of ForgotPassword component
    Test Priority : High
    Module Name   : Frontend - Forgot Password page
    Description   : Checks whether the ForgotPassword component renders successfully.
*/
test('ForgotPassword component renders successfully', () => {
    render(<ForgotPassword />, container);
    expect(screen.getByText('E-mail of your account', {exact: false})).not.toThrow()
});

/*
    ID            : TC_F_23
    Title         : Incorrect e-mail notice of ForgotPassword component
    Test Priority : High
    Module Name   : Frontend - Forgot Password page
    Description   : Checks whether the ForgotPassword component notifies user on invalid e-mail input
*/
test('User is notified at incorrect e-mail', () => {
    render(<ForgotPassword />, container);
    fireEvent.change(
        screen.getByLabelText('E-mail of your account'),
        {target: {value: 'NotAnEMail'}}
    );
    expect(screen.getAllByText('Please enter a valid e-mail', {exact: false})).toThrow()
})

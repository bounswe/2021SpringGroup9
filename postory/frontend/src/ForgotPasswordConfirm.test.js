import {fireEvent, screen} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
import ForgotPasswordConfirm from "./ForgotPasswordConfirm";

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
    ID            : TC_F_24
    Title         : Rendering of ForgotPasswordConfirm component
    Test Priority : High
    Module Name   : Frontend - Forgot Password Confirmation page
    Description   : Checks whether the ForgotPasswordConfirm component renders successfully.
*/
test('ForgotPasswordConfirm component renders successfully', () => {
    render(<ForgotPasswordConfirm />, container);
    expect(screen.getByText('Enter your new password', {exact: false})).not.toThrow()
});

/*
    ID            : TC_F_25
    Title         : Password mismatch check of ForgotPasswordConfirm component
    Test Priority : High
    Module Name   : Frontend - Forgot Password Confirmation page
    Description   : Checks whether the submit button is disabled when user enters two unequal passwords
*/
test('Two unequal new passwords disable the button', () => {
    render(<ForgotPasswordConfirm />, container);
    fireEvent.change(
        screen.getByLabelText('Enter your new password'),
        {target: {value: 'MyPassword00%'}}
    );
    fireEvent.change(
        screen.getByLabelText('Retype your new password'),
        {target: {value: 'MyPassword01%'}}
    );
    expect(screen.getByRole('button')).toBeDisabled()
});

/*
    ID            : TC_F_26
    Title         : Password match check of ForgotPasswordConfirm component
    Test Priority : High
    Module Name   : Frontend - Forgot Password Confirmation page
    Description   : Checks whether the submit button is enabled  when user enters two equal passwords
*/
test('Two equal new passwords enable the button', () => {
    render(<ForgotPasswordConfirm />, container);
    fireEvent.change(
        screen.getByLabelText('Enter your new password'),
        {target: {value: 'MyPassword00%'}}
    );
    fireEvent.change(
        screen.getByLabelText('Retype your new password'),
        {target: {value: 'MyPassword00%'}}
    );
    expect(screen.getByRole('button')).not.toBeDisabled()
});
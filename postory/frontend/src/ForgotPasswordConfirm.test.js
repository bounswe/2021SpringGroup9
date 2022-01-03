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

test('ForgotPasswordConfirm component renders successfully', () => {
    render(<ForgotPasswordConfirm />, container);
    expect(screen.getByText('Enter your new password', {exact: false})).not.toThrow()
});

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
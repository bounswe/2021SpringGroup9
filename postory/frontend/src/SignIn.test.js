import {findByText, fireEvent, screen} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
import SignIn from "./SignIn";

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

test('SignIn component renders successfully', () => {
    render(<SignIn />, container);
    expect(screen.getByText('Enter your new password', {exact: false})).not.toThrow()
});

test('Incorrect username & password doesnt result with success', async () => {
    render(<SignIn />, container);
    fireEvent.change(
        screen.getByLabelText('Username or E-mail:'),
        {target: {value: 'a_username_not_in_database'}}
    );
    fireEvent.change(
        screen.getByLabelText('Password:'),
        {target: {value: 'a_password_not_in_database'}}
    );
    fireEvent.click(screen.getByRole('button'))
    await screen.findByText('Incorrect username or password')
})

test('Correct username & password results with success', async () => {
    render(<SignIn />, container);
    fireEvent.change(
        screen.getByLabelText('Username or E-mail:'),
        {target: {value: 'amelih6@gmail.com'}}
    );
    fireEvent.change(
        screen.getByLabelText('Password:'),
        {target: {value: 'MyPassword123%%'}}
    );

    fireEvent.click(screen.getByRole('button'))
    await screen.findByText('Sign in success.')
})
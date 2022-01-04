import {fireEvent, screen} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
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

test('SignUp component renders successfully', () => {
    render(<SignUp />, container);
    expect(screen.getByText('Enter your new password', {exact: false})).not.toThrow()
});

test('Invalid input keeps the button disabled', () => {
    render(<SignUp />, container);

    const emailField = screen.getByLabelText('E-mail:')
    const usernameField = screen.getByLabelText('Username:')
    const nameField = screen.getByLabelText('Name:')
    const surnameField = screen.getByLabelText('Surname:')
    const password1Field = screen.getByLabelText('Password:')
    const password2Field = screen.getByLabelText('Repeat password:')
    const button = screen.getByRole('button')

    const enter = (field, text) => fireEvent.change(field, {target: {value: text}})

    // Progressively filling data
    enter(emailField, 'ahmetkaya@gmail.com')
    expect(button).toBeDisabled()
    enter(usernameField, 'ahmetkaya')
    expect(button).toBeDisabled()
    enter(nameField, 'Ahmet')
    expect(button).toBeDisabled()
    enter(surnameField, 'Kaya')
    expect(button).toBeDisabled()
    enter(password1Field, 'NerdenBileceksiniz123+')
    expect(button).toBeDisabled()
    enter(password2Field, 'NerdenBileceksiniz123')    // inequal passwords!!
    expect(button).toBeDisabled()

    // Equal passwords but no special character
    enter(password1Field, 'NerdenBileceksiniz123')
    expect(button).toBeDisabled()

    // Passwords are okay but invalid e-mail
    enter(password1Field, 'NerdenBileceksiniz123+')
    enter(password2Field, 'NerdenBileceksiniz123+')
    enter(emailField, 'ahmetttt')
})

test('Valid input makes the button enabled', () => {
    render(<SignUp />, container);

    const emailField = screen.getByLabelText('E-mail:')
    const usernameField = screen.getByLabelText('Username:')
    const nameField = screen.getByLabelText('Name:')
    const surnameField = screen.getByLabelText('Surname:')
    const password1Field = screen.getByLabelText('Password:')
    const password2Field = screen.getByLabelText('Repeat password:')
    const button = screen.getByRole('button')

    const enter = (field, text) => fireEvent.change(field, {target: {value: text}})

    // Progressively filling data
    enter(emailField, 'ahmetkaya@gmail.com')
    expect(button).toBeDisabled()
    enter(usernameField, 'ahmetkaya')
    expect(button).toBeDisabled()
    enter(nameField, 'Ahmet')
    expect(button).toBeDisabled()
    enter(surnameField, 'Kaya')
    expect(button).toBeDisabled()
    enter(password1Field, 'NerdenBileceksiniz123+')
    expect(button).toBeDisabled()
    enter(password2Field, 'NerdenBileceksiniz123+')
    expect(button).toBeEnabled()  // All is good

})
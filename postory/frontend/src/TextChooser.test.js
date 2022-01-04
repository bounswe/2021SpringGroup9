import {fireEvent, screen} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
import TextChooser from "./TextChooser";
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

test('TextChooser component renders successfully', () => {
    render(<TextChooser />, container)
    expect(screen.getByText("Post title", {exact: false})).not.toThrow()
})

test('User can enter story title and story body', () => {
    render(<TextChooser />, container)
    const postTitle = screen.getByLabelText('Post title')
    const postBody = screen.getByLabelText('Post body')
    const enter = (field, text) => fireEvent.change(field, {target: {value: text}})
    enter(postTitle, 'My Memory')
    enter(postBody, 'A very interesting thing happened today!')
    // expect nothing to throw
})
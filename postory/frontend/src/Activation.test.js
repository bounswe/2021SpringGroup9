import {screen} from '@testing-library/react';
import {unmountComponentAtNode, render} from 'react-dom'
import Activation from "./Activation";

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

test('Activation component renders successfully', () => {
    render(<Activation />, container);
    expect(screen.getByText('Activating your account', {exact: false})).not.toThrow()
});

import React from 'react';
import Icon from '@mdi/react'
import {createHash} from 'crypto'

function isEmail(str) {
    return /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(str)
}

class SignIn extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: null,
            email: null,
            password: null,
        }
        this.handleButtonClick = this.handleButtonClick.bind(this)
    }

    handleButtonClick() {
        const hashedPassword = createHash('sha256').update(this.state.password).digest('hex')
        //TODO handle button click
    }

    render() {
        return (<div>
            <label htmlFor={'signin-username'}>Username or E-mail: </label>
            <br />
            <input type={'text'} id={'signin-username'} onChange={
                e => {
                    let str = e.target.value;
                    if (isEmail(str)) {
                        this.setState(state => ({...state, email: str}))
                    } else {
                        this.setState(state => ({...state, username: str}));
                    }

                }
            }/>
            <br />
            <label htmlFor={'signin-password'}>Password: </label>
            <br />
            <input type={'password'} id={'signin-password'} onChange={
                e => {
                    this.setState(state => ({...state, password: e.target.value}))
                }
            }/>
            <br />
            <button disabled={(!(this.state.username || this.state.email) || !this.state.password) ? 'true' : ''} onClick={this.handleButtonClick}>
                Sign In
            </button>
            <a href={'/forgotPassword'}>
                Forgot password?
            </a>
        </div>)
    }
}

export default SignIn;
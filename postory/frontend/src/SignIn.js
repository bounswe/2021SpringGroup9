import React from 'react';
import Icon from '@mdi/react'
import {createHash} from 'crypto'
import './SignIn.css'
import { Link } from "react-router-dom";

function isEmail(str) {
    return /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(str)
}

function forgotPassword() {
    return <div>

    </div>
}

class SignIn extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: null,
            email: null,
            password: null,
            forgot: false,
        }
        this.handleButtonClick = this.handleButtonClick.bind(this)
    }

    handleButtonClick() {
        const hashedPassword = createHash('sha256').update(this.state.password).digest('hex')
        //TODO handle button click
    }

    render() {
        if (this.state.forgot) {
            return <header className={'App-header'}>
                <div id={'signin'}>
                    <label htmlFor={'signin-forgot-email'} id={'signin-forgot-email-text'}>
                        E-mail of your account
                    </label>
                    <br />
                    <input type={'text'} id={'signin-forgot-email'} value={this.state.email} onChange={
                        e => {
                            this.setState(state => ({...state, email: e.target.value}))
                        }
                    }/>
                    <br />
                    {
                        this.state.email && !isEmail(this.state.email) && <>
                            <span style={{fontSize: '50%'}}> Please enter a valid e-mail </span>
                            <br />
                        </>
                    }
                    <button id={'signin-forgot-button'} disabled={!isEmail(this.state.email) ? 'true' : ''}>
                        Send activation e-mail
                    </button>
                    <br />
                    <span id={'signin-forgot-link'} onClick={()=>{this.setState(state => ({...state, forgot: false}))}}>
                Go back
                </span>
                    <br />

                </div>
            </header>
        }

        return (
            <header className={'App-header'}>
                <div id={'signin'}>
                    <label htmlFor={'signin-username'} id={'signin-username-text'}>Username or E-mail: </label>
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
                    <label htmlFor={'signin-password'} id={'signin-password-text'}>Password: </label>
                    <br />
                    <input type={'password'} id={'signin-password'} onChange={
                        e => {
                            this.setState(state => ({...state, password: e.target.value}))
                        }
                    }/>
                    <br />
                    <button id={'signin-button'} disabled={(!(this.state.username || this.state.email) || !this.state.password) ? 'true' : ''} onClick={this.handleButtonClick}>
                        Sign In
                    </button>
                    <br />
                    <span id={'signin-forgot-link'} onClick={()=>{this.setState(state => ({...state, forgot: true}))}}>
                        Forgot password?
                    </span>
                    <br />
                    <Link to={'/signUp'} id={'signin-signup-link'} variant={'v6'}>
                        Sign Up
                    </Link>
                </div>
            </header>

            )
    }
}

export default SignIn;
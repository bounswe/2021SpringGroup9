import React from 'react';
import Icon from '@mdi/react'
import {createHash} from 'crypto'
import './SignIn.css'
import {Link, Navigate} from "react-router-dom";

function isEmail(str) {
    return /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(str)
}

const BACKEND_URL = 'http://' + window.location.hostname + ':8000'

class SignIn extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: null,
            email: null,
            password: null,
            incorrect: false,
            completed: false,
            redirect: false
        }
        this.handleButtonClick = this.handleButtonClick.bind(this)
    }

    handleButtonClick(e) {
        if (e.target.disabled) return;

        fetch(`${BACKEND_URL}/auth/jwt/create/`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: this.state.email,
                password: this.state.password
            })
        }).then(
            res => {
                if (res.status % 100 === 2) {
                    res.json().then(
                        data => {
                            const {refresh, access} = data
                            localStorage.setItem('refresh', refresh)
                            localStorage.setItem('access', access)
                        }
                    ).then(
                        () => this.setState(state => ({...state, completed: true}))
                    )
                } else {
                    this.setState(state => ({...state, incorrect: true}))
                }
            }
        )
    }

    render() {
        if (this.state.completed) {
            setTimeout(() => this.setState(state => ({...state, redirect: true})), 2000)

            return <header className={'App-header'}>
                <div id={'signin'}>
                    <span>Sign in success.</span>
                    <br />
                    <span>Redirecting to homepage..</span>
                    {this.state.redirect && <Navigate to={'/'} />}
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
                    { this.state.incorrect && <>
                    <span id={'signin-incorrect-text'} style={{fontSize: '50%'}}>
                        Incorrect username or password
                    </span>
                        <br />
                    </>}
                    <button id={'signin-button'} disabled={(!(this.state.username || this.state.email) || !this.state.password) ? 'true' : ''} onClick={this.handleButtonClick}>
                        Sign In
                    </button>
                    <br />

                    <Link to={'/forgotPassword'} id={'signin-forgot-link'} variant={'v6'}>
                        Forgot password?
                    </Link>
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
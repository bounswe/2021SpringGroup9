import React from 'react';
import Icon from '@mdi/react'
import {createHash} from 'crypto'
import './SignUp.css'
import {Link} from "react-router-dom";

function isEmail(str) {
    return /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(str)
}

function getUsernameProblem(username) {
    if (username.length < 6) {
        return "Username should be 6 characters or longer";
    } else if (! /^[a-zA-Z0-9\s]+$/.test(username)) {
        return "Username should only contain alphanumerical characters and whitespace"
    }
    return ""
}

function getPasswordProblem(password) {
    if (password.length < 8) {
        return "Password should be 8 characters or longer"
    } else if(! /[a-z]/.test(password)) {
        return "Password should contain a lowercase letter"
    } else if (! /[A-Z]/.test(password)) {
        return "Password should contain an uppercase letter"
    } else if (! /[0-9]/.test(password)) {
        return "Password should contain a number"
    } else if (/^[a-zA-Z0-9]+$/.test(password)) {
        return "Password should contain a special character"
    }
    return ""
}

class SignUp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: null,
            email: null,
            password1: null,
            password2: null,
        }
        this.handleButtonClick = this.handleButtonClick.bind(this)
    }

    handleButtonClick() {
        const hashedPassword = createHash('sha256').update(this.state.password1).digest('hex')
        //TODO handle button click
    }

    render() {
        return (<header className={'App-header'}>
            <div id={'signup'}>
                <label htmlFor={'signup-email'} id={'signup-email-text'}>E-mail: </label>
                <br />
                <input type={'text'} id={'signup-email'} onChange={
                    e => {
                        this.setState(state => ({...state, email: e.target.value}))
                    }
                }/>
                <br />
                { this.state.email && !isEmail(this.state.email) &&
                <>
                    <span id={'signup-email-problem-text'}> Please enter a vaild e-mail</span>
                    <br />
                </>
                }
                <label htmlFor={'signup-username'} id={'signup-username-text'}>Username: </label>
                <br />
                <input type={'text'} id={'signup-username'} onChange={
                    e => {
                        this.setState(state => ({...state, username: e.target.value}));
                    }
                }/>
                <br />
                { this.state.username && getUsernameProblem(this.state.username) &&
                <>
                    <span id={'signup-username-problem-text'}>{getUsernameProblem(this.state.username)}</span>
                    <br />
                </>
                }
                <label htmlFor={'signup-password1'} id={'signup-password1-text'}>Password: </label>
                <br />
                <input type={'password'} id={'signup-password1'} onChange={
                    e => {
                        this.setState(state => ({...state, password1: e.target.value}))
                    }
                }/>
                <br />
                { this.state.password1 && getPasswordProblem(this.state.password1) &&
                <>
                    <span id={'signup-password1-problem-text'}>{getPasswordProblem(this.state.password1)}</span>
                    <br />
                </>
                }
                <label htmlFor={'signup-password2'} id={'signup-password2-text'}>Repeat password: </label>
                <br />
                <input type={'password'} id={'signup-password2'} onChange={
                    e => {
                        this.setState(state => ({...state, password2: e.target.value}))
                    }
                }/>
                <br />
                { this.state.password2 && this.state.password1 !== this.state.password2 &&
                <>
                    <span id={'signup-password2-problem-text'}>Passwords don't match</span>
                    <br />
                </>
                }
                <button
                    id={'signup-button'}
                    disabled={(!this.state.email || !this.state.username || !this.state.password1 || !this.state.password2 || !isEmail(this.state.email) || getUsernameProblem(this.state.username) || getPasswordProblem(this.state.password1) || (this.state.password1 !== this.state.password2)) ? 'true' : ''}
                    onClick={this.handleButtonClick}>
                    Sign Up
                </button>
                <br />
                <Link to={'/signIn'} id={'signup-signin-link'} variant={'v6'}>
                    Sign in instead
                </Link>
            </div>
        </header>)
    }
}

export default SignUp;
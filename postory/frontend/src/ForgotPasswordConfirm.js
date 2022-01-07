import React from 'react'
import './ForgotPasswordConfirm.css'
import {Link, Navigate} from "react-router-dom";

const BACKEND_URL = 'http://' + (process.env.REACT_APP_BACKEND_API ? process.env.REACT_APP_BACKEND_API : '3.67.83.253') + ':8000'

/**
 * @class ForgotPasswordConfirm
 * Makes user choose their new password, and sends appropriate request to backend in order to set the new passwords
 * as user's password.
 * Notifies user on invalid input.
 */
class ForgotPasswordConfirm extends React.Component {
    constructor(props) {
        super(props);
        this.props = props
        this.state = {
            password1: null,
            password2: null,
            uid: this.props.match.params.uid,
            token: this.props.match.params.token,
        }
        this.handleButtonClick = this.handleButtonClick.bind(this)

    }

    handleButtonClick(e) {
        if (e.target.disabled) return;

        fetch(`${BACKEND_URL}/auth/users/password_reset_confirm/`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                uid: this.state.uid,
                token: this.state.token,
                new_password: this.state.password1,
                re_new_password: this.state.password2
            })
        }).then(
            res => {
                this.setState(state => ({...state, success: true}))
                setTimeout(() => this.setState(state => ({...state, redirect: true})))
            }
        )
    }

    render() {
        if (this.state.success) {
            return <header className={'App-header'}>
                <div id={'fpc'}>
                    <span>Password reset successfully</span>
                    <br />
                    <span>You are now being redirected to homepage</span>
                    <br />
                    <span>Don't forget to sign in with your new password</span>
                    {this.state.redirect && <Navigate to={'/'} />}
                </div>
            </header>
        }

        return <header className={'App-header'}>
            <div id={'fpc'}>
                <label htmlFor={'fpc-new-password1'} id={'fpc-new-password1-text'}>
                    Enter your new password
                </label>
                <br />
                <input type={'password'} id={'fpc-new-password1'} onChange={
                    e => this.setState(state => ({...state, password1: e.target.value}))
                }/>
                <br />
                <label htmlFor={'fpc-new-password2'} id={'fpc-new-password2-text'}>
                    Retype your new password
                </label>
                <br />

                <input type={'password'} id={'fpc-new-password2'} onChange={
                    e => this.setState(state => ({...state, password2: e.target.value}))
                }/>
                <br />
                <button id={'fpc-button'} disabled={(!this.state.password1 || !this.state.password2) ? 'true' : ''} onClick={this.handleButtonClick}>
                    Change password
                </button>
            </div>
        </header>
    }
}

export default ForgotPasswordConfirm;
import React from 'react'
import { Link } from "react-router-dom";
import './ForgotPassword.css'

function isEmail(str) {
    return /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(str)
}

const BACKEND_URL = 'http://' + (process.env.REACT_APP_BACKEND_API ? process.env.REACT_APP_BACKEND_API : '3.67.83.253') + ':8000'

/**
 * @class ForgotPassword
 * Gets e-mail of the user and sends backend a request to reset password of user with that e-mail.
 * Notifies user on invalid input.
 */
class ForgotPassword extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            email: null,
            password: null,
            incorrect: false,
            success: false
        }
        this.handleButtonClick = this.handleButtonClick.bind(this)
    }

    handleButtonClick(e) {
        if (e.target.disabled) return;

        fetch(`${BACKEND_URL}/auth/users/reset_password/`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: this.state.email,
            })
        }).then(
            res => {
                if (Math.floor(res.status / 100) === 2) {
                    this.setState(state => ({...state, success: true}))
                }
            }
        )
    }

    render() {
        if (this.state.success) {
            return <header className={'App-header'}>
                <div id={'forgot'}>
                    <span>Reset e-mail sent successfully.</span>
                    <br />
                    <span>Check your inbox to reset your password</span>
                    <br />
                    <Link to={'/signIn'} id={'forgot-link'} variant={'v6'}>
                        Go back
                    </Link>
                </div>
            </header>
        }

        return <header className={'App-header'}>
            <div id={'forgot'}>
                <label htmlFor={'forgot-email'} id={'forgot-email-text'}>
                    E-mail of your account
                </label>
                <br />
                <input type={'text'} id={'forgot-email'} value={this.state.email} onChange={
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
                {
                    this.state.email && !isEmail(this.state.email) && <>
                        <span style={{fontSize: '50%'}}> Please enter a valid e-mail </span>
                        <br />
                    </>
                }
                <button id={'forgot-button'} disabled={!isEmail(this.state.email) ? 'true' : ''} onClick={this.handleButtonClick}>
                    Send e-mail
                </button>
                <br />
                <Link to={'/signIn'} id={'forgot-link'} variant={'v6'}>
                    Go back
                </Link>
                <br />
            </div>
        </header>
    }
}

export default ForgotPassword
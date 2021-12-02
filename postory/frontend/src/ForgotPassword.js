import React from 'react'
import { Link } from "react-router-dom";
import './ForgotPassword.css'

function isEmail(str) {
    return /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/.test(str)
}

class ForgotPassword extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            email: null,
            password: null,
            nextStep: false
        }
    }


    render() {
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
                <button id={'forgot-button'} disabled={!isEmail(this.state.email) ? 'true' : ''}>
                    {this.state.nextStep ? 'Resend' : 'Send'}  e-mail
                </button>
                <br />
                { this.state.nextStep && <>
                <br />
                <label htmlFor={'forgot-new-password'} id={'forgot-new-password-text'}>
                    Enter your new password
                </label>
                <br />
                <input type={'password'} id={'forgot-new-password'} onChange={
                    e => {
                        this.setState(state => ({...state, password: e.target.value}))
                    }
                }/>
                <br />
                </>}
                <Link to={'/signIn'} id={'forgot-link'} variant={'v6'}>
                    Go back
                </Link>
                <br />
            </div>
        </header>
    }
}

export default ForgotPassword
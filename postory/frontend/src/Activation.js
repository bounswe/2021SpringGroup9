import React from 'react'
import './Activation.css'
import {Link, Navigate} from "react-router-dom";

const BACKEND_URL = 'http://' + window.location.hostname + ':8000'

class Activation extends React.Component {
    constructor(props) {
        super(props);
        this.props = props
        this.state = {
            activationCompleted: false,
            redirect: false
        }
        
        var regex = /\/[a-zA-Z0-9]*\//g;
        var url = window.location.pathname;
        var idx = [...url.matchAll(regex)];
        console.log('props', idx);
    }

    componentDidMount() {
        console.log(this.state);
        const {uid, token} = this.props.match.params;

        fetch(`${BACKEND_URL}/auth/users/activation/`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                uid: uid,
                token: token,
            })
        }).then(
            res => {
                this.setState(state => ({...state, activationCompleted: true}))
                setTimeout(() => this.setState(state => ({...state, redirect: true})))
            }
        )
    }

    render() {
        return <header className={'App-header'}>
            <div id={'activation'}>
                {!this.state.activationCompleted ? 'Activating your account...' :
                <>
                <span> Your account has been activated.</span>
                <br />
                <span>Now you are being redirected to homepage</span>
                <br />
                <span>Don't forget to sign in with your new account</span>
                {this.state.redirect && <Navigate to={'/'} />}
                </>
                }
            </div>
        </header>
    }
}

export default Activation;
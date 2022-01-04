import React from 'react'
import './Activation.css'
import {Link, Navigate} from "react-router-dom";
import { useParams } from 'react-router-dom'
import TopBar from './TopBar'

const BACKEND_URL = 'http://' + window.location.hostname + ':8000'

class Activation extends React.Component {
    constructor(props) {
        super(props);
        this.props = props
        this.state = {
            activationCompleted: false,
            redirect: false
        }
    }

    componentDidMount() {
        const pathname = window.location.pathname
        const [uid, token] = pathname.split('/').slice(2, 4)

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
                setTimeout(() => this.setState(state => ({...state, redirect: true})), 2000)
            }
        )
    }

    render() {
        return <>
            <TopBar />
            <header className={'App-header'}>
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
        </>
        return
    }
}

export default Activation;
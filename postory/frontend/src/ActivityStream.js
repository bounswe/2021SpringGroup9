import React from 'react'
import './ActivityStream.css'
import * as requests from './requests';
import {Link, Navigate} from "react-router-dom";

const IP = window.location.hostname

class Activity extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            type: props.type,
            user1_id: props.user1_id,
            user2_id: props.user2_id,
            post_id: props.post_id,
            user1_link: props.user1_id && `/profilePage?id=${props.user1_id}`,
            user2_link: props.user2_id && `/profilePage?id=${props.user2_id}`,
            post_link: props.post_id && `/viewPost?id=${props.post_id}`,
            user1_name: '...',
            user2_name: '...',
            accepted: false
        }
        this.getUser1 = this.getUser1.bind(this)
        this.getUser2 = this.getUser2.bind(this)
        this.getPost = this.getPost.bind(this)
    }

    getUser1() {
        return <Link to={this.state.user1_link}>{this.state.user1_name}</Link>
    }

    getUser2() {
        return <Link to={this.state.user2_link}>{this.state.user2_name}</Link>
    }

    getPost() {
        return <Link to={this.state.post_link}>post</Link>
    }

    getAccept() {
        if (this.state.accepted) {
            return <span> Accepted </span>
        } else {
            return <span className="astream-link" onClick={() => {
                // TODO
            }}>
                Click here to accept
            </span>
        }
    }

    componentDidMount() {
        requests.get_jwt(`/api/user/get${this.state.user1_id}`, {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, user1_name: data.username}))
            })
        requests.get_jwt(`/api/user/get${this.state.user2_id}`, {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, user2_name: data.username}))
            })
    }

    render() {
        if (this.state.type === "FolllowRequest") {
            return <li>
                {this.getUser1()} wants to follow you.
            </li>
        } else if (this.state.type === "Like") {
            return <li>
                {this.getUser1()} has liked {this.getPost()} of {this.getUser2()}
            </li>
        } else if (this.state.type === "Comment") {
            return <li>
                {this.getUser1()} has commented on {this.getPost()} of {this.getUser2()}
            </li>
        } // TODO cover all cases
    }
}


class ActivityStream extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            activities: []
        }
    }

    componentDidMount() {
        requests.get_jwt('TODO', {})
            .then(res => res.json())
            .then(data => {
                // TODO fill this.state.activities
            })
    }

    render() {
        return (
            <header className='App-header'>
                <div id="astream">
                    <ul>
                        {/* TODO map this.state.activities */}
                    </ul>
                </div>
            </header>
        )
    }
}

export default ActivityStream;
import React from 'react'
import './ActivityStream.css'
import * as requests from './requests';
import {Link, Navigate} from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.css';
import Tabs from 'react-bootstrap/Tabs';
import Tab from 'react-bootstrap/Tab';

class Activity extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            type: props.type,
            actor: props.actor,
            object: props.object,
            status: 'neither'  // neither, accepted, rejected
        }
        this.getUser1 = this.getUser1.bind(this)
        this.getUser2 = this.getUser2.bind(this)
        this.getPost = this.getPost.bind(this)
        this.getAccept = this.getAccept.bind(this)
    }

    componentDidMount() {
        if (this.state.type === "UserFollow") {
            requests.get_jwt(`/api/user/get/${this.object.id}`)
                .then(res => res.json())
                .then(data => {
                    this.setState(state => ({...state, object: {...state.object, username: data.username}}))
                })
        }
    }

    getUser1() {
        return <Link to={`/profilePage?id=${this.state.actor.id}`}>{this.state.actor.username}</Link>
    }

    getUser2() {
        return <Link to={`/profilePage?id=${this.state.object.id}`}>{this.state.object.username}</Link>
    }

    getPost() {
        return <Link to={`/viewPost?id=${this.state.object.id}`}>a post</Link>
    }

    getAccept() {
        if (this.state.status === "accepted") {
            return <span> Accepted. </span>
        } else if (this.state.status === "rejected") {
          return <span> Rejected.</span>
        } else {
            return <>
                <button onClick={() => {
                    requests.get_jwt(`/api/user/acceptRequest/${this.state.actor.id}`)
                        .then(() => this.setState(state => ({...state, status: 'accepted'})))
                }}>
                    Accept
                </button>
                &nbsp;
                <button onClick={() => {
                    requests.get_jwt(`/api/user/declineRequest/${this.state.actor.id}`)
                        .then(() => this.setState(state => ({...state, status: 'rejected'})))
                }}>
                    Reject
                </button>
            </>

        }
    }

    render() {
        if (this.state.type === "FollowRequest") {
            return <div>
                <img className="circle" width="50px" height="50px" style={{float: 'left'}} src={this.state.actor.userPhoto || "./static/media/postory_logo_no_text.ec3bad21.png"}/>
                {this.getUser1()}
                {this.getAccept()}
            </div>
        } else if (this.state.type === "PostLike") {
            return <div>
                <img className="circle" width="50px" height="50px" style={{float: 'left'}} src={this.state.actor.userPhoto || "./static/media/postory_logo_no_text.ec3bad21.png"}/>
                {this.getUser1()} has liked {this.getPost()}
            </div>
        } else if (this.state.type === "PostComment") {
            return <div>
                <img className="circle" width="50px" height="50px" style={{float: 'left'}} src={this.state.actor.userPhoto || "./static/media/postory_logo_no_text.ec3bad21.png"}/>
                {this.getUser1()} has commented on {this.getPost()}
            </div>
        } else if (this.state.type === "PostCreate") {
            return <div>
                <img className="circle" width="50px" height="50px" style={{float: 'left'}} src={this.state.actor.userPhoto || "./static/media/postory_logo_no_text.ec3bad21.png"}/>
                {this.getUser1()} has created {this.getPost()}
            </div>
        } else if (this.state.type === "PostUpdate") {
            return <div>
                <img className="circle" width="50px" height="50px" style={{float: 'left'}} src={this.state.actor.userPhoto || "./static/media/postory_logo_no_text.ec3bad21.png"}/>
                {this.getUser1()} has updated {this.getPost()}
            </div>
        } else if (this.state.type === "UserFollow") {
            return <div>
                <img className="circle" width="50px" height="50px" style={{float: 'left'}} src={this.state.actor.userPhoto || "./static/media/postory_logo_no_text.ec3bad21.png"}/>
                {this.getUser1()} has followed {this.getUser2()}
            </div>
        } else if (this.state.type === "UserAddPhoto") {
            return <div>
                <img className="circle" width="50px" height="50px" style={{float: 'left'}} src={this.state.actor.userPhoto || "./static/media/postory_logo_no_text.ec3bad21.png"}/>
                {this.getUser1()} has added a photo.
            </div>
        }
    }
}


class ActivityStream extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            own_activities: [],
            followed_activities: [],
            follow_requests: [],
        }
    }

    componentDidMount() {
        requests.get_jwt('/api/activitystream/own', {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, own_activities: data}))
            })
        requests.get_jwt('/api/activitystream/followed', {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, followed_activities: data}))
            })
        requests.get_jwt('/api/user/getRequests', {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, follow_requests: data}))
            })
    }

    render() {
        return <div className='App-header'>
            <Tabs defaultActiveKey="Own" className="mb-3" style={{backgroundColor: "rgb(235, 235, 235)"}}>
                <Tab eventKey="Own" title="Own">
                    <div id="astream">
                        {this.state.own_activities.map((activity, index) => (
                            <React.Fragment key={index}>
                                <Activity actor={activity.actor} type={activity.type} object={activity.object}/>
                                <br />
                            </React.Fragment>
                        ))}
                    </div>

                </Tab>
                <Tab eventKey="Followed" title="Followed">
                    <div id="astream">
                        {this.state.followed_activities.map((activity, index) => (
                            <React.Fragment key={index}>
                                <Activity actor={activity.actor} type={activity.type} object={activity.object}/>
                                <br />
                            </React.Fragment>
                        ))}
                    </div>
                </Tab>
                <Tab eventKey="Follow Requests" title="Follow Requests">
                    <div id="astream">
                        {this.state.follow_requests.map((user, index) => (
                            <React.Fragment key={index}>
                                <Activity actor={user} type="FollowRequest" />
                                <br />
                            </React.Fragment>
                        ))}
                    </div>
                </Tab>
            </Tabs>
        </div>

    }
}

export default ActivityStream;
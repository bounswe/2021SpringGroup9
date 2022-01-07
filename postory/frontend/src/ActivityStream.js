import React from 'react'
import './ActivityStream.css'
import * as requests from './requests';
import {Link, Navigate} from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.css';
import Tabs from 'react-bootstrap/Tabs';
import Tab from 'react-bootstrap/Tab';

/**
 * @class Activity
 * Represents an activity in the ActivityStream, or a follow request.
 * An activity is interactable (i.e: user can go to actor and object by clicking).
 * Additionally, a follow request has buttons for accepting and rejecting the request.
 */
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
                    requests.post_jwt(`/api/user/acceptRequest/${this.state.actor.id}`, {})
                        .then(() => this.setState(state => ({...state, status: 'accepted'})))
                }}>
                    Accept
                </button>
                &nbsp;
                <button onClick={() => {
                    requests.post_jwt(`/api/user/declineRequest/${this.state.actor.id}`, {})
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

/**
 * @class ActivityStream
 * ActivityStream shows activities of the user, activities of the followed users,
 * all public activities, and follow requests; all in separate tabs.
 * At every tab there is a list of Activity objects.
 */
class ActivityStream extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            own_activities: [],
            own_activities_loading: true,
            followed_activities: [],
            followed_activities_loading: true,
            all_activities: [],
            all_activities_loading: true,
            follow_requests: [],
            follow_requests_loading: true
        }
    }

    componentDidMount() {
        requests.get_jwt('/api/activitystream/own', {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, own_activities_loading: false, own_activities: data}))
            })
        requests.get_jwt('/api/activitystream/followed', {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, followed_activities_loading: false, followed_activities: data}))
            })
        requests.get_jwt('/api/activitystream/all', {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, all_activities_loading: false, all_activities: data}))
            })
        requests.get_jwt('/api/user/getRequests', {})
            .then(res => res.json())
            .then(data => {
                this.setState(state => ({...state, follow_requests_loading: false, follow_requests: data}))
            })
    }

    render() {
        return <div className='App-header'>
            <Tabs defaultActiveKey="Own" className="mb-3" style={{backgroundColor: "rgb(235, 235, 235)"}}>
                <Tab eventKey="Own" title="Own">
                    <div id="astream">
                        {this.state.own_activities_loading ? 'Loading...' : this.state.own_activities.map((activity, index) => (
                            <React.Fragment key={index}>
                                <Activity actor={activity.actor} type={activity.type} object={activity.object}/>
                                <br />
                            </React.Fragment>
                        ))}
                    </div>

                </Tab>
                <Tab eventKey="Followed" title="Followed">
                    <div id="astream">
                        {this.state.followed_activities_loading ? 'Loading...' : this.state.followed_activities.map((activity, index) => (
                            <React.Fragment key={index}>
                                <Activity actor={activity.actor} type={activity.type} object={activity.object}/>
                                <br />
                            </React.Fragment>
                        ))}
                    </div>
                </Tab>
                <Tab eventKey="All" title="All">
                    <div id="astream">
                        {this.state.all_activities_loading ? 'Loading...' : this.state.all_activities.map((activity, index) => (
                            <React.Fragment key={index}>
                                <Activity actor={activity.actor} type={activity.type} object={activity.object}/>
                                <br />
                            </React.Fragment>
                        ))}
                    </div>
                </Tab>
                <Tab eventKey="Follow Requests" title="Follow Requests">
                    <div id="astream">
                        {this.state.follow_requests_loading ? 'Loading...' :  this.state.follow_requests.map((user, index) => (
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
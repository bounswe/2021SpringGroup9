import { mdiAccount } from '@mdi/js';
import Button from 'react-bootstrap/Button';
import Icon from '@mdi/react';
import React, { useEffect } from "react";
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import Post from './Post';

export const ProfilePageUpper = () => {
    const [followingCount, setFollowingCount] = React.useState(0);
    const [followerCount, setFollowerCount] = React.useState(0);
    const [postCount, setPostCount] = React.useState(0);
    const [userPosts, setUserPosts] = React.useState([]);
    const [fetchedPosts, setFetchedPosts] = React.useState(false);
    const [showFollowButton, setShowFollowButton] = React.useState(true);
    const [userID, setUserID] = React.useState();
    const [sessionUserID, setSessionUserID] = React.useState();
    const [username, setUsername] = React.useState();

    useEffect(() => {
        fetch(`http://3.125.114.231:8000/api/post/all/user/${userID}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`
            }
        }).then(response => response.json())
            .then( (data) => {
                setUserPosts(data);
                setFetchedPosts(true)
                console.log(data);
            })
    }, [userID])

    useEffect(() => {
        fetch(`http://3.125.114.231:8000/api/user/get/${userID}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`
            }
        }).then(response => response.json())
            .then( (data) => {
                setUsername(data.username);
                setFollowingCount(data.followedUsers.length);
                setFollowerCount(data.followerUsers.length);
                console.log(data);
        })
    }, [userID])

    useEffect(() => {
        setUserID( () => {
            var regex = /id=/g;
            var url = window.location.href;
            var idx = url.search(regex);
            var id = parseInt(url.slice(idx+3));
            //var decoded = jwt_decode(localStorage.getItem('access'));

            console.log("ID: " + id);
            console.log("Decoded: " + localStorage.getItem('userID'));
            setSessionUserID(localStorage.getItem('userID'));
            if (localStorage.getItem('userID') == id){
                setShowFollowButton(false);
            }
            return id;
        })
    }, [])

    const onClickFollow = () =>{
        fetch(`http://3.125.114.231:8000/api/user/follow/${userID}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`
            },
            body: JSON.stringify({})
        })    
    }

    return ( 
        <div style={{ backgroundColor: `#EBEBEB`}}>
        <div style={{ height: window.innerHeight * 1/20, width: window.innerWidth }}/>
        <hr style={{ color: `black`, backgroundColor: `black`, height: 2 }}/>
        <Container>
            <Row style={{alignItems: `center`}}>
                <Col sm={4} >
                    <Icon path={mdiAccount} size={2}/>
                    <div style={{ fontSize: `16px`, fontColor: `#08233B`, fontWeight: `bold` }}>{username}</div>
                </Col>
                <Col  sm={2}>
                    <div style={{ fontSize: `16px`, fontColor: `#08233B` }}>followers: {followerCount}</div>
                </Col>
                <Col  sm={2}>
                    <div style={{ fontSize: `16px`, fontColor: `#08233B` }}>followings: {followingCount}</div>
                </Col>
                <Col  sm={2}>  
                    <div style={{ fontSize: `16px`, fontColor: `#08233B` }}>posts: {postCount}</div>
                </Col>
            </Row>
            <div style={{ height: window.innerHeight * 1/50, width: window.innerWidth }}/>
            {showFollowButton && <Button variant="secondary" size="sm" onClick={() => onClickFollow()} >Follow</Button>}
        </Container>
        <div className="App-header">
        {fetchedPosts && userPosts.map((obj, i) => {
            return <Post key = {i} {...obj}></Post>;
        })}
        </div>
        </div>
    );
}
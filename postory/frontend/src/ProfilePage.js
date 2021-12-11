import { mdiAccount } from '@mdi/js';
import Button from 'react-bootstrap/Button';
import Icon from '@mdi/react';
import React, { useEffect } from "react";
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import Post from './Post';
import jwt_decode from "jwt-decode";

export const ProfilePageUpper = () => {
    const [followingCount, setFollowingCount] = React.useState(0);
    const [followerCount, setFollowerCount] = React.useState(0);
    const [postCount, setPostCount] = React.useState(0);
    const [userPosts, setUserPosts] = React.useState([]);
    const [fetchedPosts, setFetchedPosts] = React.useState(false);
    const [showFollowButton, setShowFollowButton] = React.useState(true);
    const [username, setUserName] = React.useState();
    const [sessionUserName, setSessionUserName] = React.useState();

    useEffect(() => {
        fetch(`http://3.125.114.231:8000/api/post/all/user/${username}`, {
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
    }, [username])

    useEffect(() => {
        setUserName( () => {
            var regex = /id=/g;
            var url = window.location.href;
            var idx = url.search(regex);
            var id = parseInt(url.slice(idx+3));
            var decoded = jwt_decode(localStorage.getItem('access'));
            console.log("ID: " + id);
            console.log("Decoded: " + decoded.user_id);
            setSessionUserName(decoded.user_id);
            if (decoded.user_id == id){
                setShowFollowButton(false);
            }
            return id;
        })
    }, [])

    const onClickFollow = () =>{
        fetch(`http://3.125.114.231:8000'/api/user/follow/${username}`, {
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
        <div>
        <div style={{ height: window.innerHeight * 1/10, width: window.innerWidth }}/>
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
        <hr style={{ color: `black`, backgroundColor: `black`, height: 2 }}/>
        <div className="App-header">
        {fetchedPosts && userPosts.map((obj, i) => {
            return <Post key = {i} {...obj}></Post>;
        })}
        </div>
        </div>
    );
}
import { mdiAccount } from '@mdi/js';
import Button from 'react-bootstrap/Button';
import Icon from '@mdi/react';
import React, { useEffect } from "react";
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import Post from './Post';
import {Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';

const BACKEND_IP = '3.67.83.253';

export function ProfilePageUpper () {
    const [followingCount, setFollowingCount] = React.useState(0);
    const [followerCount, setFollowerCount] = React.useState(0);
    const [postCount, setPostCount] = React.useState(0);
    const [userPosts, setUserPosts] = React.useState([]);
    const [fetchedPosts, setFetchedPosts] = React.useState(false);
    const [showFollowButton, setShowFollowButton] = React.useState(true);
    const [userID, setUserID] = React.useState();
    const [sessionUserID, setSessionUserID] = React.useState();
    const [username, setUsername] = React.useState();
    const [popupState, setPopupState] = React.useState(false);
    const [photo, setPhoto] = React.useState(false);
    const [profilePhoto, setProfilePhoto] = React.useState(false);

    useEffect(() => {
        fetch(`http://${BACKEND_IP}:8000/api/post/all/user/${userID}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`
            }
        }).then(response => response.json())
            .then( (data) => {
                data.sort((a, b) => parseInt(b.id) - parseInt(a.id));
                setUserPosts(data);
                setPostCount(data.length);
                setFetchedPosts(true);
                console.log(data);
            })
    }, [userID])

    useEffect(() => {
        fetch(`http://${BACKEND_IP}:8000/api/user/get/${userID}`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`
            }
        }).then(response => response.json())
            .then( (data) => {
                setProfilePhoto(data.userPhoto);
                setUsername(data.username);
                setFollowingCount(data.followedUsers.length);
                setFollowerCount(data.followerUsers.length);
                for(let i =0 ; i< data.followerUsers.length; i++){
                    if(data.followerUsers[i] == sessionUserID){
                        setShowFollowButton(false);
                    }
                }
                console.log(data);
        })
    }, [userID, showFollowButton])

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
        fetch(`http://${BACKEND_IP}:8000/api/user/follow/${userID}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`
            },
            body: JSON.stringify({})
        }).then(setPopupState(true))

    }

    const closePopup = () => {
        setPopupState(false);
        setShowFollowButton(false);
      };

    return ( 
        <div style={{ backgroundColor: `#EBEBEB`}}>
        <div style={{ height: window.innerHeight * 1/20, width: window.innerWidth }}/>
        <hr style={{ color: `black`, backgroundColor: `black`, height: 2 }}/>
        <Container>
            <Row style={{alignItems: `center`}}>
                <Col sm={4} >
                    <div className = {'sliderContainer'} >
                        
                        <img onClick = {() => setPhoto(st => !st)} className = "circle" width = "50px" height = "50px" src = {profilePhoto? profilePhoto:"./static/media/postory_logo_no_text.ec3bad21.png"} />
                        {photo  && (parseInt(localStorage.getItem('userID')) == userID) && <div className = {'ppup'}>
                        <input onChange = {(e) => 
                        {
                            let formData = new FormData();
                            formData.append('image', e.target.files[0]);
                            
                            fetch(`http://${BACKEND_IP}:8000/api/user/addPhoto`, {
                            method: 'POST',
                            headers: {
                                'Authorization': `JWT ${localStorage.getItem('access')}`
                            },
                            body: formData
                                });
                            setPhoto(st => !st);}
                        }
                            type="file" id="file" accept=".jpg, .png"/>
                            </div>}
                    </div>
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
        <Snackbar open={popupState} autoHideDuration={3000} onClose={() => closePopup()} >
            <Alert onClose={() => closePopup()} severity="info" sx={{ width: '100%' }}>
              You have successfully followed {username}!
            </Alert>
        </Snackbar>
        </div>
    );
}
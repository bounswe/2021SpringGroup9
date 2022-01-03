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

import plus_thick from './plus-thick.png'

import * as requests from './requests'


const BACKEND_IP = '3.67.83.253';

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
    const [popupState, setPopupState] = React.useState(false);
    const [photo, setPhoto] = React.useState(false);
    const [profilePhoto, setProfilePhoto] = React.useState(false);
    const [followText, setFollowText] = React.useState('Follow');

    const [hoverPhoto, setHoverPhoto] = React.useState(false);

    const [isPrivate, setIsPrivate] = React.useState(false);
    const [bEnabled, setbEnabled] = React.useState(true);
    const [followbuttonEnabled, setFollowbuttonEnabled] = React.useState(true);


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
                console.log('TEEEEESTS',data);
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
                setIsPrivate(data.isPrivate);
                for(let i =0 ; i< data.followerUsers.length; i++){
                    if(data.followerUsers[i].id == sessionUserID){
                        closePopup();
                        //setShowFollowButton(false);
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
        setFollowbuttonEnabled(false);
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
        if(!isPrivate)
            setFollowbuttonEnabled(true);
        console.log(isPrivate);
        if(isPrivate)
            setFollowText('Request is sent');
        else if(followText == 'Follow')
            setFollowText('Unfollow');
        else
            setFollowText('Follow');
      };

      useEffect(()=> {
        console.log('test');
        if(photo && (parseInt(localStorage.getItem('userID')) == userID))
            console.log(document.getElementById('file').click());
    } ,[photo]);

    return ( 
        <div style={{ backgroundColor: `#EBEBEB`}}>
        <div style={{ height: window.innerHeight * 1/20, width: window.innerWidth }}/>
        <hr style={{ color: `black`, backgroundColor: `black`, height: 2 }}/>
        <Container>
            <Row style={{alignItems: `center`}}>
                <Col sm={4} >
                    <div className = {'sliderContainer'} >
                        
                        <img onMouseEnter = {() => setHoverPhoto(true)} onMouseLeave = {() => setHoverPhoto(false)}
                         onClick = {() => setPhoto(st => !st )} class = "circle" width = "50px" height = "50px" src = {(hoverPhoto && (parseInt(localStorage.getItem('userID')) == userID))? plus_thick:(profilePhoto? profilePhoto:"./static/media/postory_logo_no_text.ec3bad21.png")} />
                        {photo  && (parseInt(localStorage.getItem('userID')) == userID) && <div className = {'ppup'}>
                        <input style = {{display: 'none'}} onChange = {(e) => 
                        {
                            let formData = new FormData();
                            formData.append('image', e.target.files[0]);
                            
                            fetch(`http://${BACKEND_IP}:8000/api/user/addPhoto`, {
                            method: 'POST',
                            headers: {
                                'Authorization': `JWT ${localStorage.getItem('access')}`
                            },
                            body: formData
                                }).then(window.location.reload());
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
                {localStorage.getItem('userID') == userID && <Col  sm={2}>  
                    <button disabled = {!bEnabled} onClick = {() => {
                        //call backend enpoint
                        requests.put_jwt('/api/user/changeProfile').then(() => window.location.reload());
                        setbEnabled(false);
                        return;
                    }}> {`Make my account ` +  (isPrivate ? 'Public':'Private')}</button>
                </Col>}
            </Row>
            <div style={{ height: window.innerHeight * 1/50, width: window.innerWidth }}/>
            {showFollowButton && <Button disabled = {!followbuttonEnabled}variant="secondary" size="sm" onClick={() => onClickFollow()} >{followText}</Button>}
        </Container>
        <div className="App-header">
        {fetchedPosts && userPosts.map((obj, i) => {
            return <Post key = {i} {...obj}></Post>;
        })}
        </div>
        <Snackbar open={popupState} autoHideDuration={1000} onClose={() => closePopup()} >
            <Alert onClose={() => closePopup()} severity="info" sx={{ width: '100%' }}>
              You Have Successfully {followText}ed {username}!
            </Alert>
        </Snackbar>
        </div>
    );
}

import Button from 'react-bootstrap/Button';
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
    /* Profile page component. Fetches the profile information of users (e.g., follower count, posts, username, profile picture etc.)
    and renders them. */
    const [followingCount, setFollowingCount] = React.useState(0); // Stores the number of users a user follows
    const [followerCount, setFollowerCount] = React.useState(0); // Stores the number of users a user is followed by
    const [postCount, setPostCount] = React.useState(0); // Stores the number of posts a user has
    const [userPosts, setUserPosts] = React.useState([]); // Stores the posts of a user in a list
    const [fetchedPosts, setFetchedPosts] = React.useState(false); // True if posts of a users have been fetched succesfully, false otherwise
    const [showFollowButton, setShowFollowButton] = React.useState(true); // False if users sees her own profile page, true otherwise
    const [userID, setUserID] = React.useState(); // User ID of the profile page owner
    const [sessionUserID, setSessionUserID] = React.useState(); // User ID of the user currently logged in
    const [username, setUsername] = React.useState(); // Username of the profile page owner
    const [popupState, setPopupState] = React.useState(false); // True when pop up is displayed, false otherwise
    const [photo, setPhoto] = React.useState(false);
    const [profilePhoto, setProfilePhoto] = React.useState(false);
    const [followText, setFollowText] = React.useState('Follow'); // Text to be displayed on follow button
    const [hoverPhoto, setHoverPhoto] = React.useState(false); // True when user hovers on profile picture, false otherwise
    const [isPrivate, setIsPrivate] = React.useState(false); // Fetched from backend, tue if the user's account is private, false otherwise
    const [bEnabled, setbEnabled] = React.useState(true);
    const [followbuttonEnabled, setFollowbuttonEnabled] = React.useState(true);


    useEffect(() => {
        /* Called on mount. Fetches the posts of the user and assings them to proper states so that
        they can be used in render. */
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
                console.log('User posts: ',data);
            })
    }, [userID])

    useEffect(() => {
        /* Called on mount. Fetches the profile information of the user and assings them to proper states so that
        they can be used in render. */
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
                    }
                }
                console.log('User data: ',data);
        })
    }, [userID, showFollowButton])

    useEffect(() => {
        /* Called on mount. Gets the user ID of the user currently logged in from local storage, and checks whether
        it is same with the user ID of the porfile page owner. Sets follow button state accordingly. */
        setUserID( () => {
            var regex = /id=/g;
            var url = window.location.href;
            var idx = url.search(regex);
            var id = parseInt(url.slice(idx+3));

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
        /* Called when user clicks on . Gets the user ID of the user currently logged in from local storage, and checks whether
        it is same with the user ID of the porfile page owner. Sets follow button state accordingly. */
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
        /* Called when pop up is closed. Sets the follow button configuration (i.e., follow text and button enable/disable) */
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

import { mdiAccount } from '@mdi/js';
import Button from 'react-bootstrap/Button';
import Icon from '@mdi/react';
import React from "react";
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'

export const ProfilePageUpper = () => {
    const [username, setUsername] = React.useState("username");
    const [followingCount, setFollowingCount] = React.useState(0);
    const [followerCount, setFollowerCount] = React.useState(0);
    const [postCount, setPostCount] = React.useState(0);

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
            <Button variant="secondary" size="sm">Follow</Button>
        </Container>
        <hr style={{ color: `black`, backgroundColor: `black`, height: 2 }}/>
        </div>
    );
}
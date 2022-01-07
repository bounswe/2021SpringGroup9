import React from 'react';
import Dropdown from 'react-bootstrap/Dropdown';
import { Link } from "react-router-dom";
import { useNavigate } from 'react-router-dom';

/**
 * A component that renders various links on the application.
 * It is located on the top right of every page on the application.
 * @param {*} props 
 * @returns 
 */

const NavbarMenu = (props) => { 
    const navigate = useNavigate();
    return(<Dropdown.Menu show >
            <Dropdown.Item onClick = {() => navigate("/")}>Home</Dropdown.Item>
            <Dropdown.Item onClick = {() => navigate("/discover")}>Discover</Dropdown.Item>
            <Dropdown.Item onClick = {() => navigate("/activityStream")}>Activities</Dropdown.Item>
            <Dropdown.Item onClick = {() => navigate("/createPost")}>Create a Post</Dropdown.Item>
            <Dropdown.Item onClick = {() => {
                navigate(`/profilePage?id=${localStorage.getItem('userID')}`)
                window.location.reload();
            }
                }>My Profile Page</Dropdown.Item>
            <Dropdown.Item onClick = {() => navigate("/signIn")}>Sign In</Dropdown.Item>
            <Dropdown.Item onClick = {() => navigate("/signUp")}>Sign Up</Dropdown.Item>
            <Dropdown.Item onClick = {() => {
                localStorage.removeItem('access');
                window.location.reload();
            }}>Logout</Dropdown.Item>
            

        </Dropdown.Menu>);
}

export default NavbarMenu;
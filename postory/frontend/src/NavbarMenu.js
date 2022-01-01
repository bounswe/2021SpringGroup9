import React from 'react';
import Dropdown from 'react-bootstrap/Dropdown';
import { Link } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
const NavbarMenu = (props) => {
    let navigate; 
    if(!props.noNavigate)
        navigate = useNavigate();
    return(<Dropdown.Menu show >
            <Dropdown.Item onClick = {() => navigate("/")}>Home</Dropdown.Item>
            <Dropdown.Item onClick = {() => navigate("/discover")}>Discover</Dropdown.Item>
            <Dropdown.Item onClick = {() => navigate("/createPost")}>Create a Post</Dropdown.Item>
            <Dropdown.Item onClick = {() => navigate(`/profilePage?id=${localStorage.getItem('userID')}`)}>My Profile Page</Dropdown.Item>
        </Dropdown.Menu>);
}

export default NavbarMenu;
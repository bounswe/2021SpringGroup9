import React from 'react';
import postoryLogoNoText from './postory_logo_no_text.png'
import homeIcon from './home_icon.png'
import globeIcon from './globe_icon.png'
import menuIcon from './menu_icon.png'
import SearchBar from './SearchBar'
import {Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import SearchUserComponent from './SearchUser';
import NavbarMenu from './NavbarMenu';
import Col from 'react-bootstrap/Col'
import {Offcanvas} from 'react-bootstrap'

import { Link } from "react-router-dom";

const topBarColor = 'rgb(235, 235, 235)'

const topBarStyle = {
    position: 'absolute',
    width: '100%',
    height: '50px',
    backgroundColor: topBarColor,
    zindex: -1
}

const itemStyleLeft = {
    float: 'left',
    height: '100%',
    marginLeft: '10px',
    marginRight: '10px'
}

const itemStyleRight = {
    float: 'right',
    height: '100%',
    marginLeft: '10px',
    marginRight: '10px'
    
}

const menuStyle = {
    overflow: 'visible'
}

// fits to parent div
const imageStyle = {
    maxHeight: '100%',
    maxWidth: '100%'
}

// style of 'POSTORY' text
const textStyle = {
    margin: 0,
    fontSize: '2.5em',
    fontFamily: 'Bebas Neue',
    color: '#ff914d'
}


/**
 * @class TopBar
 *
 * The bar that we show at the top of our application.
 */
class TopBar extends React.Component {
    constructor(props) {
        super(props);
        this.props = props;

        this.state = {
            menu:false
          }
    }

    render() {
        
        return (<div >
            <div class = 'navb'>{this.state.menu && <NavbarMenu ></NavbarMenu>}</div><div style={topBarStyle}>
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&display=swap');
            </style>

            <div style = {{marginLeft: '200px'}}>
                <SearchUserComponent class = 'Dropdown_style'/>
            </div>

            <a href={'/'} style={itemStyleLeft}>
                <img src={postoryLogoNoText} alt={'Postory'} style={imageStyle}/>
            </a>
            <a href={'/'} style={{...itemStyleLeft, textDecoration: 'none'}}>
                <p style={textStyle}>POSTORY</p>
            </a>
            
            <div style={itemStyleRight}>

                <img src={menuIcon} alt={'Menu'} style={imageStyle} onClick={() => this.setState(st => {return {menu:!st.menu}})} />
                 
            </div>
            
            

            <Link class = "push" to= {`/discover`}>
            <div style={itemStyleRight}>
                <img src={globeIcon} alt={'Discover'} style={imageStyle}  />
            </div>
            </Link>
            <Link to="/" ariant = "v6">
                <a href={'/'} style={itemStyleRight}>
                    <img src={homeIcon} alt={'Postory'} style={imageStyle}/>
                </a>
            </Link>

        </div></div>)

    }
}

export default TopBar
import React from 'react';
import postoryLogoNoText from './postory_logo_no_text.png'
import homeIcon from './home_icon.png'
import globeIcon from './globe_icon.png'
import menuIcon from './menu_icon.png'
import SearchBar from './SearchBar'

const topBarColor = 'rgb(235, 235, 235)'

const topBarStyle = {
    position: 'fixed',
    width: '100%',
    height: '50px',
    backgroundColor: topBarColor
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


class TopBar extends React.Component {
    constructor(props) {
        super(props);
        this.props = props;
    }

    render() {
        return (<div style={topBarStyle}>
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Bebas+Neue&display=swap');
            </style>

            <div style={itemStyleLeft}>
                <SearchBar />
            </div>

            <a href={'/'} style={itemStyleLeft}>
                <img src={postoryLogoNoText} alt={'Postory'} style={imageStyle}/>
            </a>
            <a href={'/'} style={{...itemStyleLeft, textDecoration: 'none'}}>
                <p style={textStyle}>POSTORY</p>
            </a>
            <a href={'/'} style={itemStyleRight}>
                <img src={menuIcon} alt={'Menu'} style={imageStyle}/>
            </a>
            <a href={'/discover'} style={itemStyleRight}>
                <img src={globeIcon} alt={'Discover'} style={imageStyle}/>
            </a>
            <a href={'/'} style={itemStyleRight}>
                <img src={homeIcon} alt={'Postory'} style={imageStyle}/>
            </a>
        </div>)

    }
}

export default TopBar
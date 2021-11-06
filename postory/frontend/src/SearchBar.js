import React from 'react';
import magnifyIcon from './magnify_icon.png';

// text area
const itemStyleLeft = {
    float: 'left',
    height: '100%',
    width: '75%',
    marginLeft: '3px',
    padding: 0,
    border: 0,
    boxShadow: 'inset 0px 0px 0px 1px #000'
}

// submit button
const itemStyleRight = {
    float: 'right',
    height: '100%',
    width: '22%',
    marginRight: '3px',
    padding: 0,
    border: 0,
    backgroundColor: 'white',
    boxShadow: 'inset 0px 0px 0px 1px #000'
}

// fits to parent div
const imageStyle = {
    maxHeight: '100%',
    maxWidth: '100%'
}

class SearchBar extends React.Component {
    constructor(props) {
        super(props);
        this.props = props;
    }

    render() {
        return (<form action={'/search'} style={{height: '100%', width: '100%'}}>
            <input type={'text'} placeholder={' Search'} name={'input'} style={itemStyleLeft}/>
            <button type={'submit'} style={itemStyleRight}>
                <img src={magnifyIcon} style={imageStyle} />
            </button>
        </form>)
    }
}

export default SearchBar
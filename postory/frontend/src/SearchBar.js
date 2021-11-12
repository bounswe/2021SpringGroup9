import React from 'react';
import magnifyIcon from './magnify_icon.png';
import {Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';

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

        this.state = {
            popupState: false
          }
    }

    showPopup = () => {
        this.setState({ popupState: true });
      };
    
      closePopup = () => {
        this.setState({ popupState: false });
      };

    render() {
        return (
            <div style={{height: '100%', width: '100%'}}>
                <input type={'text'} placeholder={' Search'} name={'input'} style={itemStyleLeft}/>
                <button style={itemStyleRight}>
                    <img 
                        src={magnifyIcon} 
                        style={imageStyle}
                        onClick={this.showPopup} 
                        />
                </button>
                <Snackbar open={this.state.popupState} autoHideDuration={3000} onClose={this.closePopup} >
                    <Alert onClose={this.closePopup} severity="info" sx={{ width: '100%' }}>
                         This feature is not available now and coming soon, thanks heaps for your patience!
                    </Alert>
                </Snackbar>
            </div>
        )
    }
}

export default SearchBar
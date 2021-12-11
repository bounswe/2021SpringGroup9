import React from 'react'
import Icon from '@mdi/react'
import {Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import { mdiShareVariantOutline } from '@mdi/js';
import { mdiCardsHeartOutline } from '@mdi/js';
import { mdiCardsHeart } from '@mdi/js';
import { mdiCommentTextOutline } from '@mdi/js';
import { mdiAlertCircleOutline } from '@mdi/js';
import { mdiBookmarkOutline } from '@mdi/js';
import { mdiDragVerticalVariant } from '@mdi/js';
  
class LikeButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            liked: false,
            likeNumber: 0
        };
      }

  updateLikeNumber = () => {
    this.setState({ liked: !this.state.liked, likeNumber: this.state.liked ? 0 : 1 });
  }

  getIconPath = () => this.state.liked ? mdiCardsHeart : mdiCardsHeartOutline

  render() {
    const imageName = this.getIconPath();
    return (
      <div class= "row2">
        <Icon 
          path={imageName} 
          size={2}
          onClick={this.updateLikeNumber} 
        />
        <div>
          {this.state.likeNumber}
        </div>
      </div>
    );
  }
}

function VerticalSeperator() {
    return(
        <div>
          <Icon path={mdiDragVerticalVariant} size={2}/>
        </div>
    );
}

class PostButtons extends React.Component {
  constructor(props){
    super(props);

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

  render(){
    return(
      <div>
        <div class= "row2">
          <LikeButton></LikeButton>
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiCommentTextOutline} 
            size={2}
            onClick={this.showPopup} 
          />
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiShareVariantOutline} 
            size={2}
            onClick={this.showPopup} 
          />
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiBookmarkOutline} 
            size={2}
            onClick={this.showPopup} 
          />
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiAlertCircleOutline} 
            size={2}
            onClick={this.showPopup} 
          />
        </div>
        <Snackbar open={this.state.popupState} autoHideDuration={3000} onClose={this.closePopup} >
            <Alert onClose={this.closePopup} severity="info" sx={{ width: '100%' }}>
              This feature is not available now and coming soon, thanks heaps for your patience!
            </Alert>
        </Snackbar>
      </div>);
  }

}

export default PostButtons;
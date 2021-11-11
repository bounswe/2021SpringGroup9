import React from 'react'
import Icon from '@mdi/react'
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
      <div class= "row">
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

  render(){
    return(
      <div>
        <div class= "row">
          <LikeButton></LikeButton>
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiCommentTextOutline} 
            size={2}
          />
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiShareVariantOutline} 
            size={2}
          />
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiBookmarkOutline} 
            size={2}
          />
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiAlertCircleOutline} 
            size={2}
          />
        </div>
      </div>);
  }

}

export default PostButtons;
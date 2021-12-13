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
import { Link } from "react-router-dom";
class LikeButton extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        ...props,
        liked: false,
        likeCount: props.likeList ? props.likeList.length: 0,
        userID: localStorage.getItem('userID'),
        iconPath: mdiCardsHeartOutline
    };
    
  }

  componentDidMount() {
    if(this.state.likeList){
    for(let i = 0; i < this.state.likeList.length; i++) {
      let innerList = this.state.likeList[i];
      if (innerList[0] == this.state.userID){
        //console.log("This user has already liked this post")
        this.setState({liked: true, iconPath: mdiCardsHeart});
      }
    }
    setTimeout(() => {console.log(this.state.liked)}, 500);
  }
    //console.log(this.state.liked);
    //console.log(this.state.likeList.length)
  }

  updateLikeNumber = () => {
    console.log(this.state.userID)
    console.log(this.state.id)
    fetch(`http://3.125.114.231:8000/api/post/like/${this.state.id}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`
            }
    }).then(response => response.json())
    .then( (data) => {
      var likeList = data.likeList;
      console.log(data);
      console.log(likeList.length);
      setTimeout(() => {this.setState({likeCount: data.likeList.length})}, 300);
      if (!this.state.liked){
        setTimeout(() => {this.setState({liked: true, iconPath: mdiCardsHeart})}, 300);
      } else {
        setTimeout(() => {this.setState({liked: false, iconPath: mdiCardsHeartOutline})}, 300);
      }
    })
  }

  render() {
    return (
      <div class= "row2">
        <Icon 
          path={this.state.iconPath} 
          size={2}
          onClick={this.updateLikeNumber} 
        />
        <div>
          {this.state.likeCount}
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
      ...props,
      popupState: false
    }
    console.log(this.state);
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
          <LikeButton {...this.props}></LikeButton>
          <VerticalSeperator></VerticalSeperator>
          <Link class = "push" to= {`/viewPost?id=${this.state.id}`}>
          <Icon 
            path={mdiCommentTextOutline} 
            size={2}
          />
          </Link>
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
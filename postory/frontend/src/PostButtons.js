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
import * as requests from './requests'

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
    /*
    fetch(`http://3.125.114.231:8000/api/post/like/${this.state.id}`, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `JWT ${localStorage.getItem('access')}`
            }
    })*/
    requests.post_jwt(`/api/post/like/${this.state.id}`,{}).then(response => response.json())
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

  showPopup = (text) => {
    this.setState({text:text, popupState: true });
  };

  closePopup = () => {
    this.setState(st => {return {...st, popupState: false }});
  };

  render(){
    return(
      <div>
        <div class= "row2">
          <LikeButton {...this.props}></LikeButton>
          <VerticalSeperator></VerticalSeperator>
          {this.props.id ? 
          <Link class = "push" to= {`/viewPost?id=${this.state.id}`}>
          <Icon 
            path={mdiCommentTextOutline} 
            color = 'black'
            size={2}
          />
          </Link> : <Icon 
            path={mdiCommentTextOutline} 
            size={2}
          />}
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiShareVariantOutline} 
            size={2}
            onClick={() => {
              navigator.clipboard.writeText(String(window.location).split('/')[2] + '/viewPost?id=' + this.state.id);
              this.showPopup('The share link is copied to clipboard!');
            }} 
          />
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiBookmarkOutline} 
            size={2}
            onClick={() => {
              requests.post_jwt(`/api/post/save/${this.state.id}`, {});
              this.showPopup('The post is successfully saved.');
            }} 
          />
          <VerticalSeperator></VerticalSeperator>
          <Icon 
            path={mdiAlertCircleOutline} 
            size={2}
            onClick={() => {
              requests.post_jwt(`/api/user/report/story/${this.state.id}`, {});
            }} 
          />
        </div>
        <Snackbar open={this.state.popupState} autoHideDuration={3000} onClose={this.closePopup} >
            <Alert onClose={this.closePopup} severity="info" sx={{ width: '100%' }}>
              {this.state.text}
            </Alert>
        </Snackbar>
      </div>);
  }

}

export default PostButtons;
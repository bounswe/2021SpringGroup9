import React from 'react'
import Icon from '@mdi/react'
import { mdiMapMarker, mdiClockTimeEight, mdiTag, mdiCardsHeartOutline, mdiCardsHeart, mdiShareVariant,  mdiPencilOutline} from '@mdi/js'
import PostButtons from './PostButtons';

import { Link } from "react-router-dom";


class Post extends React.Component{
    constructor(props){
      super(props);
      this.props = props;
    }
    render(){
      return (<div class= "Post" ><PostUpper {...this.props}/> <PostButtons/></div>);
    }
}

class PostUpper extends React.Component{
  constructor(props){
    super(props);

    this.state = {
      ...props,
      contentSmall : (props.story.length > 200) ? props.story.slice(0,200): props.story,
      continueReading: props.story.length > 200,
    };
    console.log(this.state.tags);
    this.getMoreContent = this.getMoreContent.bind(this);
  }

  getMoreContent(){
    this.setState(state =>{
      //let newState = JSON.parse(JSON.stringify(state));
      let newState = {...state};
      newState.contentSmall = state.story;
      newState.continueReading = false;
      return newState;
    });
  }

  render(){
    return (<div >
      <div class= "row" style = {{paddingBottom: "10px"}}>
        <img class = "circle" width = "50px" height = "50px" src = "./static/media/postory_logo_no_text.ec3bad21.png" />
        <a style = {{margin: "10px"}}>{this.state.owner}</a>

        {this.state.id && <Link class = "push" to= {`/editPost?id=${this.state.id}`}>
          <Icon path={mdiPencilOutline}
            title="Edit Post"
            size={1}
            color='#FF8F49'
            />
        </Link>}
      </div>
      <div class= "row fitText">
      
        
        {this.state.locations.map((obj,i) => {
          return(
          <a key = {i} class = "tag">
              <Icon path={mdiMapMarker}
          title="Location"
          size={0.7}
          color="#53BEC6"
          />
             {obj[0]}
          </a>);
        })}

        <a class = "tag">
          <Icon path={mdiClockTimeEight}
          title="Time"
          size={0.7}
          color="#53BEC6"
          />
          {this.state.storyDate.slice(0,10)}
        </a>
        {this.state.tags.map((obj,i) => {
          return(
          <a key = {i} class = "tag">
              <Icon path={mdiTag}
          title="Tag"
          size={0.7}
          color="#53BEC6"
          />
             {obj}
          </a>);
        })}
      </div>
      <a class = "mainContent" >{this.state.contentSmall}</a>
      {this.state.continueReading && <a class = "mainContent" onClick = {this.getMoreContent}><b> ...Continue Reading</b></a>}
      <p></p>
      <div> 
        {this.state.preview &&<div class = "row image">
          <img width = "200px" src = {URL.createObjectURL(this.state.preview[0])} ></img>
        </div>}
        {!this.state.preview &&<div class = "row image">
          <img width = "200px" src = {(this.state.images && this.state.images[0]) ? this.state.images[0]: ""} ></img>
          <img width = "200px" src = {(this.state.images && this.state.images[0] && this.state.images[1]) ? this.state.images[1]: ""}></img>
        </div>}
      </div>
    </div>);
  }
}

class PostLower extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      clicked: false
    };
    this.clickHeart = this.clickHeart.bind(this);
  }

  clickHeart(){
    this.setState(state => {
      return {clicked: !state.clicked};
    })
  }

  render(){
    return(
      <div class = "row">
        {!this.state.clicked ?
        <Icon path={mdiCardsHeartOutline}
          title="Like"
          size={2}
          color="#53BEC6"
          onClick = {this.clickHeart}
          /> : 
          <Icon path={mdiCardsHeart}
          title="Like"
          size={2}
          color="#53BEC6"
          onClick = {this.clickHeart}
          /> }
        <Icon path={mdiShareVariant}
          title="Share"
          size={2}
          color="#53BEC6"
          />
      </div>
    )
  }
}



export default Post;
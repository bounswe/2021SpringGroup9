import React from 'react'
import Icon from '@mdi/react'
import { mdiMapMarker, mdiClockTimeEight, mdiTag, mdiCardsHeartOutline, mdiCardsHeart, mdiShareVariant} from '@mdi/js'
import PostButtons from './PostButtons';

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
      let newState = JSON.parse(JSON.stringify(state));
      newState.contentSmall = state.story;
      newState.continueReading = false;
      return newState;
    });
  }

  render(){
    return (<div >
      <div class= "row" style = {{paddingBottom: "10px"}}>
        <img class = "circle" width = "50px" height = "50px" src = "https://images.emojiterra.com/google/android-11/512px/1f9cd.png" />
        <a style = {{margin: "10px"}}>{this.state.owner}</a>
      </div>
      <div class= "row">
      
        
        {this.state.locations.map((obj,i) => {
          return(
          <a key = {i} class = "tag">
              <Icon path={mdiMapMarker}
          title="Location"
          size={0.7}
          color="#53BEC6"
          />
             {obj}
          </a>);
        })}

        <a class = "tag">
          <Icon path={mdiClockTimeEight}
          title="Time"
          size={0.7}
          color="#53BEC6"
          />
          {this.state.storyDate}
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
      <div class = "row image"> 
        <img width = "200px" src = "https://www.industrialempathy.com/img/remote/ZiClJf-1920w.jpg" ></img>
        <img width = "200px" src = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png"></img>
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
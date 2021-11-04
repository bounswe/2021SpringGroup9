import React from 'react'


class Post extends React.Component{
    constructor(props){
      super(props);
      this.props = props;
    }
    render(){
      return (<div class= "Post" ><PostUpper {...this.props}/></div>);
    }
}

class PostUpper extends React.Component{
  constructor(props){
    super(props);

    this.state = {
      ...props,
      contentSmall : props.content.slice(0,200),
      continueReading: props.content.length > 200,
    };
    console.log(this.state.tags);
    this.getMoreContent = this.getMoreContent.bind(this);
  }

  getMoreContent(){
    this.setState(state =>{
      let newState = JSON.parse(JSON.stringify(state));
      newState.contentSmall = state.content;
      newState.continueReading = false;
      return newState;
    });
  }

  render(){
    return (<div >
      <div class= "row">
        <img class = "circle" width = "50px" height = "50px" src = "https://images.emojiterra.com/google/android-11/512px/1f9cd.png" />
        <p>{this.props.author.username}</p>
      </div>
      <div class= "row">
        <a class = "tag">{this.state.location}</a>
        <a class = "tag">{this.state.time}</a>
        {this.state.tags.map((obj,i) => {
          return <a key = {i} class = "tag"> {obj}</a>;
        })}
      </div>
      <a class = "mainContent" >{this.state.contentSmall}</a>
      {this.state.continueReading && <a class = "mainContent" onClick = {this.getMoreContent}><b> ...Continue Reading</b></a>}
      <p></p>
      <img width = "200px" src = "https://www.industrialempathy.com/img/remote/ZiClJf-1920w.jpg" ></img>
      <img width = "200px" src = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1200px-Image_created_with_a_mobile_phone.png"></img>
    </div>);
  }
}



export default Post;
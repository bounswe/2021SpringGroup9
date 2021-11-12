import logo from './logo.svg';
import React from 'react'
import Post  from './Post'
import './App.css';
import PostButtons from './PostButtons';
import Icon from '@mdi/react';
import {mdiPencilPlusOutline } from '@mdi/js';

import { Link } from "react-router-dom";


class App extends React.Component{
  constructor(props){
    super(props);
    //Should fetch from the backend, needs post content, username, profile picture, post media, location, time, tags
    let dummyPost = {story : "Praesent eu libero et diam mollis placerat sed eget eros. Curabitur commodo purus in lorem fermentum, a suscipit tellus faucibus. Morbi justo nibh, iaculis sed porttitor id, faucibus in lorem. Aenean porttitor imperdiet velit id laoreet. Mauris tortor urna, fermentum eu eros vel, vestibulum malesuada mi. Vivamus venenatis magna nec eleifend hendrerit. Morbi lacinia ligula a quam varius, ac pretium libero semper. Fusce ultrices arcu ut augue sodales vehicula. Fusce pellentesque urna vel arcu facilisis, sed consectetur enim mollis. Nam suscipit euismod elit, ac cursus ex tempor eget. Curabitur aliquet ante orci, at vestibulum leo finibus vel. Praesent ullamcorper pharetra rhoncus. Vestibulum euismod nulla in ligula bibendum aliquam. Curabitur nec varius ligula. Duis feugiat mi risus, eget auctor lacus scelerisque sit amet.",
  owner : "Daniel Jones", locations: ["The World", "Ankara"], storyDate: "2021", tags: ["Cool"] };
    let posts = []
    for(let i = 0; i<10;i++){
      posts.push(dummyPost);
    }
    this.state = {
      posts: posts
    };
    fetch('http://35.158.95.81:8000/api/post/all').then(resp => resp.json()).then(data => this.setState(state => {
      let newState = JSON.parse(JSON.stringify(state));
      newState.posts = data;
      newState['fetched'] = true;
      console.log(newState);
      console.log(data);
      return newState;
    }))
    
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <p>
            POSTORY
          </p>
          {this.state.fetched && this.state.posts.map((obj, i) => {
            return <Post key = {i} {...obj}></Post>;
          })}
          <Link to= "/createPost" variant = "v6">
            <Icon class = "circle homePageCreatePostButton" path={mdiPencilPlusOutline}
            title="Location"
            size={2}
            color="black"
            />
          </Link>

        </header>
        
      </div>
    );
  }

}




export default App;

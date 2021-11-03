import logo from './logo.svg';
import React from 'react'
import Post  from './Post'
import './App.css';

class App extends React.Component{
  constructor(props){
    super(props);
    //Should fetch from the backend, needs post content, username, profile picture, post media
    let dummyPost = {content : "Praesent eu libero et diam mollis placerat sed eget eros. Curabitur commodo purus in lorem fermentum, a suscipit tellus faucibus. Morbi justo nibh, iaculis sed porttitor id, faucibus in lorem. Aenean porttitor imperdiet velit id laoreet. Mauris tortor urna, fermentum eu eros vel, vestibulum malesuada mi. Vivamus venenatis magna nec eleifend hendrerit. Morbi lacinia ligula a quam varius, ac pretium libero semper. Fusce ultrices arcu ut augue sodales vehicula. Fusce pellentesque urna vel arcu facilisis, sed consectetur enim mollis. Nam suscipit euismod elit, ac cursus ex tempor eget. Curabitur aliquet ante orci, at vestibulum leo finibus vel. Praesent ullamcorper pharetra rhoncus. Vestibulum euismod nulla in ligula bibendum aliquam. Curabitur nec varius ligula. Duis feugiat mi risus, eget auctor lacus scelerisque sit amet.",
  author : {username: "Daniel Jones"}};
    let posts = []
    for(let i = 0; i<10;i++){
      posts.push(dummyPost);
    }
    this.state = {
      posts: posts
    };
    
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src="https://user-images.githubusercontent.com/52619088/139734287-271be973-fb00-44c1-a1bc-d561cb6e8e42.jpeg" className="App-logo" alt="logo" />
          <p>
            POSTORY
          </p>
          {this.state.posts.map((obj, i) => {
            return <Post key = {i} content = {obj.content} author = {obj.author}></Post>;
          })}
          <a
          >
            POSTORY app
          </a>
        </header>
      </div>
    );
  }
}




export default App;

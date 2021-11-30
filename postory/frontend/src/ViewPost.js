import React from 'react';
import Post from './Post';
import "react-responsive-carousel/lib/styles/carousel.min.css"; // requires a loader
import { Carousel } from 'react-responsive-carousel';

const backendIP = '3.125.114.231:8000';

class ViewPost extends React.Component{
    constructor(props){
        super(props);
        let dummyPost = {story : "Praesent eu libero et diam mollis placerat sed eget eros. Curabitur commodo purus in lorem fermentum, a suscipit tellus faucibus. Morbi justo nibh, iaculis sed porttitor id, faucibus in lorem. Aenean porttitor imperdiet velit id laoreet. Mauris tortor urna, fermentum eu eros vel, vestibulum malesuada mi. Vivamus venenatis magna nec eleifend hendrerit. Morbi lacinia ligula a quam varius, ac pretium libero semper. Fusce ultrices arcu ut augue sodales vehicula. Fusce pellentesque urna vel arcu facilisis, sed consectetur enim mollis. Nam suscipit euismod elit, ac cursus ex tempor eget. Curabitur aliquet ante orci, at vestibulum leo finibus vel. Praesent ullamcorper pharetra rhoncus. Vestibulum euismod nulla in ligula bibendum aliquam. Curabitur nec varius ligula. Duis feugiat mi risus, eget auctor lacus scelerisque sit amet.",
        owner : "Daniel Jones", locations: ["The World", "Ankara"], storyDate: "2021", tags: ["Cool"] };

        //TODO: change this?
        console.log(window.location.href);
        const regex = /id=/g;
        const url = window.location.href;
        const idx = url.search(regex);
        const id = parseInt(url.slice(idx+3));
        console.log(url.slice(idx+3));

        this.state = {
            id: id
        }
    }

    componentDidMount(){
        fetch(`http://${backendIP}/api/post/get/${this.state.id}`).then(resp => resp.json()).then(
            data => {
                this.setState(state=>{return {
                    ...state,
                    post: data
                }});
                console.log("posdf", this.state.post);
            }
        );
    }

    render(){
        return(<div className="App App-header">
            <button class = "placeholder"></button>
        <div class = "row">
            <div>
                {this.state.post && <Post {...this.state.post} />}
                <CommentContainer />
            </div>
            <div>
                {this.state.post &&
                <Carousel>
                {this.state.post.images.map((obj,i) => {
                    return(
                    <div>
                    <img src={obj} />
                    <p className="legend">Legend 1</p>
                    </div>);
                })}

                        
                </Carousel>
                }
            </div>
        </div>
        </div>)
    }

}

class Comment extends React.Component{
    constructor(props){
        super(props);
        //get profile pic from the backend using username
        this.state = {
            userName: props.userName,
            userProfilePic: "",
            text: props.text
        };
    }
    render(){
        return(<div class = "Comment">
            <div class= "row">
                <img class = "circle" width = "50px" height = "50px" src = "./static/media/postory_logo_no_text.ec3bad21.png" />
                <a style = {{margin: "10px"}}>{this.state.userName}</a>
            </div>
            <a class = "mainContent">{this.state.text}</a>
            </div>
        )
    }

}

class CommentContainer extends React.Component{
    constructor(props){
        super(props);
        //get all comments of post from backend
        this.state = {
            comments: [
                {userName: 'USER1', text: 'a comment'},
                {userName: 'USER2', text: 'another comment comment comment comment comment comment comment'},
                {userName: 'USER1', text: 'a comment'},
                {userName: 'USER1', text: 'a comment'},
                {userName: 'USER1', text: 'a comment'},
            ],
            currentUserName : 'me'
        };
        
        this.textChange = this.textChange.bind(this);
        this.sendComment = this.sendComment.bind(this);
    }

    textChange(e){
        this.setState(st => {
            return {
                ...st,
                text: e.target.value
            }
        });
    }

    sendComment(){
        this.setState(state => {
            return {
                ...state,
                comments: state.comments.concat([{userName: "noname", text: state.text}])
            };

        });
        console.log(`Send ${this.state.text} to backend`);
    }

    render(){
        return (
        <div class = "CommentContainer"> 
            {this.state.comments.map( 
                (obj,i) => {return (<Comment {...obj} key = {i} ></Comment>);}
                )}
            <div class = "row">
                <input type = "text" onChange = {this.textChange} ></input>
                <button onClick = {this.sendComment}>SEND ICON</button>
            </div>
        </div>);
    }
}





export default ViewPost;
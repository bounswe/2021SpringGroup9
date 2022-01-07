import React from 'react';
import Post from './Post';
import "react-responsive-carousel/lib/styles/carousel.min.css"; // requires a loader
import { Carousel } from 'react-responsive-carousel';
import {mdiSend } from '@mdi/js';
import Icon from '@mdi/react';
import { TextField } from '@material-ui/core';
import * as requests from './requests'
import { mdiArrowLeftThick, mdiArrowRightThick } from '@mdi/js';


import { withScriptjs, withGoogleMap, GoogleMap, Marker } from "react-google-maps";




class ViewPost extends React.Component{
    /**
     * The component that represents the whole viewPost page.
     * It renders a Post component, the Comments component, a map for showing the locations, a Slider component for showing the Post images.
     * @param {*} props 
     */
    constructor(props){
        super(props);
        let dummyPost = {story : "Praesent eu libero et diam mollis placerat sed eget eros. Curabitur commodo purus in lorem fermentum, a suscipit tellus faucibus. Morbi justo nibh, iaculis sed porttitor id, faucibus in lorem. Aenean porttitor imperdiet velit id laoreet. Mauris tortor urna, fermentum eu eros vel, vestibulum malesuada mi. Vivamus venenatis magna nec eleifend hendrerit. Morbi lacinia ligula a quam varius, ac pretium libero semper. Fusce ultrices arcu ut augue sodales vehicula. Fusce pellentesque urna vel arcu facilisis, sed consectetur enim mollis. Nam suscipit euismod elit, ac cursus ex tempor eget. Curabitur aliquet ante orci, at vestibulum leo finibus vel. Praesent ullamcorper pharetra rhoncus. Vestibulum euismod nulla in ligula bibendum aliquam. Curabitur nec varius ligula. Duis feugiat mi risus, eget auctor lacus scelerisque sit amet.",
        owner : "Daniel Jones", locations: ["The World", "Ankara"], storyDate: "2021", tags: ["Cool"] };

        const regex = /id=/g;
        const url = window.location.href;
        const idx = url.search(regex);
        const id = parseInt(url.slice(idx+3));

        this.state = {
            id: id,
            api_key: ""
        }
        if(process.env.REACT_APP_GOOGLE_API_KEY != undefined)
            this.state = {
                id: id,
                api_key: `key=` + process.env.REACT_APP_GOOGLE_API_KEY + "&"
            }
        requests.get_jwt(`/api/post/get/${this.state.id}`, {}).then(resp => resp.json()).then(
                data => {
                    this.setState(state=>{return {
                        ...state,
                        post: data
                    }});
                }
            );
    }

    componentDidMount(){
        
    }

    render(){
        return(<div className="App App-header">
            <div style={{ height: window.innerHeight * 1/20, width: window.innerWidth }}/>
        <div class = "row2">
            <div>
                {this.state.post && <Post {...this.state.post} />}
                {this.state.post && <CommentContainer id = {this.state.id} comments = {this.state.post.comments}/>}
            </div>
            <div>
                {this.state.post &&
                <MapComponent 
                isMarkerShown
                googleMapURL={`https://maps.googleapis.com/maps/api/js?${this.state.api_key}v=3.exp&libraries=geometry,drawing,places`}
                loadingElement={<div style={{ height: `100%` }} />}
                containerElement={<div style={{ width: `500px`, height: `300px` }} />}
                mapElement={<div style={{ height: `100%` }} />}
                markers = 
                {this.state.post.locations.map((obj,i) => {return {name: obj[0], lat: obj[1], lng: obj[2]} })}
                />}
                
                {/*this.state.post &&
                <Carousel>
                {this.state.post.images.map((obj,i) => {
                    return(
                    <div>
                    <img class = "viewPostPicture" src={obj} />
                    </div>);
                })}
            </Carousel>*/}
                {this.state.post && this.state.post.images && this.state.post.images.length > 0
                && <SliderComponent images = {this.state.post.images}/>}
                
                
            </div>
        </div>
        </div>)
    }

}

class Comment extends React.Component{
    /**
     * This component represents a Comment on the CommentContainer component.
     * @param {*} props 
     */
    
    constructor(props){
        super(props);
        //get profile pic from the backend using username
        this.state = {
            userName: props.info[1],
            userProfilePic: "",
            text: props.info[2]
        };
    }
    render(){
        return(<div class = "Comment">
            <div class= "row2">
                <img class = "circle" width = "50px" height = "50px" src = "./static/media/postory_logo_no_text.ec3bad21.png" />
                <a style = {{margin: "10px"}}>{this.state.userName}</a>
            </div>
            <a style = {{fontSize:'small'}}>{this.state.text}</a>
            </div>
        )
    }

}

class CommentContainer extends React.Component{
    /**
     * This component lets the user see the comments of a post and add comments to the same post on the view post page.
     * @param {*} props 
     */
    constructor(props){
        super(props);
        //get all comments of post from backend
        this.props = props;
        this.state = {
            
            comments: props.comments,
            currentUserName : 'Me'
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
                comments: state.comments.concat([["x", "Me", state.text]])
            };

        });

        requests.post_jwt(`/api/post/comment/${this.props.id}`, {comment: this.state.text});
        console.log(`Send ${this.state.text} to backend`);
    }

    render(){
        return (
        <div class = "CommentContainer"> 
            {this.state.comments.map( 
                (obj,i) => {return (<Comment info = {obj} key = {i} ></Comment>);}
                )}
            <div class = "row2">
                <textarea style = {{width: 400}} type = "text" onChange = {this.textChange} ></textarea>
                <Icon onClick = {this.sendComment} path={mdiSend}
                        title="Post"
                        size={1}
                        color="black"
                    />
            </div>
        </div>);
    }
}

/**
 * This component renders the posts images. It lets the user search through the images using the left/right arrows.
 * @param {*} props 
 * @returns 
 */

const SliderComponent = (props ) => {
    const [curIdx, setCurIdx] = React.useState(0);
    return(
    <div className = "sliderContainer">
        <Icon className = "sliderLeft" onClick = {() =>{
            setCurIdx((idx)=> {
                idx = idx-1;
                if(idx<0)
                    idx += props.images.length;
                return idx;
            });
        }
                } path={mdiArrowLeftThick }
                        title="Slider"
                        size={2}
                        color="black"
                    />
        <Icon className = "sliderRight" onClick = {() =>{
            setCurIdx((idx)=> {
                idx = idx+1;
                idx %= props.images.length;
                return idx;
            });
        }
                } path={mdiArrowRightThick }
                        title="Slider"
                        size={2}
                        color="black"
                    />
        {props.images.length > 0 && <img src = {props.images[curIdx]}></img>}
    </div>);
}

/**
 * Google maps component that show the Post's locations on a map.
 */
const MapComponent = withScriptjs(withGoogleMap((props) =>{

return(<div>
<GoogleMap
        
        defaultZoom={0}
        defaultCenter={{ lat: 41, lng: 28 }}>
        {props.markers.map((obj,i) => {
            return (<Marker  position = {obj} options={{icon:`https://mt.google.com/vt/icon/text=${obj['name'] + '(' + (i+1) + ')'}&psize=16&font=fonts/arialuni_t.ttf&color=ff330000&name=icons/spotlight/spotlight-waypoint-b.png&ax=44&ay=48&scale=1`}} key = {i}/>);
        })}
    </GoogleMap>
    </div>);
  }
))


export default ViewPost;
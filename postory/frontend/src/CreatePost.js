import React from 'react';

import Icon from '@mdi/react';
import { mdiSend } from '@mdi/js';

import { Link } from "react-router-dom";
import TextChooser from './TextChooser'
import TimeChooser from './TimeChooser';
import TagChooser from './TagChooser';
import PeopleChooser from './PeopleChooser'
import LocationChooser from './LocationChooser'

import Post from './Post';
import {TextField, Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';


class CreatePost extends React.Component{
    constructor(props){
        super(props);
        
        this.state = {
            selected : "none",
            postData: {
                textChooser: {title : " ", body: " "},
                locationChooser: [],
                timeChooser: {startDate : " "},
                tagChooser: {selectedTags: []},
                owner:"USER"
            }
        };

        this.handleChildObjectSend = this.handleChildObjectSend.bind(this);
        this.select = this.select.bind(this);
        this.sendToBackend = this.sendToBackend.bind(this);
        this.handleSuccessClose = this.handleSuccessClose.bind(this);
        this.handleInfoClose = this.handleInfoClose.bind(this);
        this.prepareObjectToSend = this.prepareObjectToSend.bind(this);
    }

    handleChildObjectSend(whichComponent, childObj){
        const infoDict = {
            "textChooser": "Story text Content",
            "locationChooser": "Location Content",
            "timeChooser": "Time Content",
            "peopleChooser": "People Content",
            "tagChooser": "Tag Content"
        };
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj.postData[whichComponent] = childObj;

            //problem code
            if(whichComponent == 'locationChooser')
                newObj.postData[whichComponent] = childObj.map((obj) => [obj]);
            //problem code
            newObj['addedInformation'] = true;
            newObj['whichInfo'] = infoDict[whichComponent];
            console.log(newObj);

            return newObj;
        });

        
    }

    select(obj){
        console.log(obj);
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj['selected'] = obj;
            return newObj;
        });
    }

    handleSuccessClose(){
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj['success'] = false;
            return newObj;
        });
    }

    handleInfoClose(){
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj['addedInformation'] = false;
            return newObj;
        });
    }

    prepareObjectToSend(){
        return ({
            title: this.state.postData["textChooser"]["title"],
            story: this.state.postData["textChooser"]["body"],
            owner: this.state.postData["owner"],
            locations: this.state.postData["locationChooser"],
            storyDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z" ,
            editDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z",
            postDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z",
            tags: this.state.postData["tagChooser"]["selectedTags"],
        });
    }

    sendToBackend(){
        // send this.state.postData to backend
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj['success'] = true;
            return newObj;
        });

        const objectToSend = this.prepareObjectToSend();
        console.log(objectToSend);
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(objectToSend)
        };
        
        fetch('http://35.158.95.81:8000/api/post/create', requestOptions)
            .then(response => console.log(response)).catch(er => console.log(er));
        console.log(JSON.stringify(this.state.postData) + 'failed');
    }


    render(){

        return(
            <header className="App-header">
            <div class = "row someSpacing">
                <div class = "inputArea">

                    <div class = {(this.state['selected'] != 'Story')? "hide": ""}><TextChooser parentHandler = {this.handleChildObjectSend}/> </div>
                    <div class = {(this.state['selected'] != 'Location')? "hide": ""}><LocationChooser  parentHandler = {this.handleChildObjectSend}/></div>
                    <div class = {(this.state['selected'] != 'Time')? "hide": ""}><TimeChooser  parentHandler = {this.handleChildObjectSend}/></div>
                    <div class = {(this.state['selected'] != 'People')? "hide": ""}><PeopleChooser  parentHandler = {this.handleChildObjectSend}/></div>
                    <div class = {(this.state['selected'] != 'Tags')? "hide": ""}><TagChooser  parentHandler = {this.handleChildObjectSend}/></div>
                    {this.state['selected'] == 'Preview' && <div ><PostPreviewComponent  postObject = {this.prepareObjectToSend()} parentHandler = {this.handleChildObjectSend}/></div>}

                </div>
                <div class = "buttons col">
                    <button class = "createPostBtn" onClick = {() => {this.select('Story')}}>Story</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Location')}}>Location</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Time')}}>Time</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('People')}}>People</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Tags')}}>Tags</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Preview')}}>Preview</button>
                    <TextField id="userNameField" label="Enter Your Name" variant="filled" focused 
                        onChange = {(e) => this.setState(state => {
                            let postData = state.postData;
                            return {
                                ...state,
                                postData: {
                                    ...postData,
                                    owner: e.target.value
                                }
                            };
                        
                        })}/>
                </div>
                


                <Snackbar open={this.state.addedInformation} autoHideDuration={1000} onClose={this.handleInfoClose} >
                    <Alert onClose={this.handleInfoClose} severity="success" sx={{ width: '100%' }}>
                        Information about {this.state.whichInfo} is added to your post.
                    </Alert>
                </Snackbar>

                <Snackbar open={this.state.success} autoHideDuration={1000} onClose={this.handleSuccessClose} >
                    <Alert onClose={this.handleSuccessClose} severity="success" sx={{ width: '100%' }}>
                        Your post is successfully created.
                    </Alert>
                </Snackbar>
                
                <Link to= "/createPost" variant = "v6">
                    <Icon onClick = { this.sendToBackend} class = "circle homePageCreatePostButton" path={mdiSend}
                        title="Location"
                        size={2}
                        color="black"
                    />
                </Link>
                
            
            </div>
            </header>
        );
    }

}


class PostPreviewComponent extends React.Component{
    constructor(props){
        super(props);
        
    }


    render(){
        console.log(this.props.postObject);
        return(
            <div>
                <Post {...this.props.postObject} ></Post>
            </div>
        );
    }
}

export default CreatePost;
import React from 'react';

import Icon from '@mdi/react';
import { mdiSend } from '@mdi/js';

import { Link } from "react-router-dom";
import TextChooser from './TextChooser'
import TimeChooser from './TimeChooser';
import TagChooser from './TagChooser';
import PeopleChooser from './PeopleChooser'
import LocationChooser from './LocationChooser'
import {Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';


class CreatePost extends React.Component{
    constructor(props){
        super(props);
        
        this.state = {
            selected : "none",
            postData: {}
        };

        this.handleChildObjectSend = this.handleChildObjectSend.bind(this);
        this.select = this.select.bind(this);
        this.sendToBackend = this.sendToBackend.bind(this);
        this.handleSuccessClose = this.handleSuccessClose.bind(this);
        this.handleInfoClose = this.handleInfoClose.bind(this);
    }

    handleChildObjectSend(whichComponent, childObj){
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj.postData[whichComponent] = childObj;
            newObj['addedInformation'] = true;
            newObj['whichInfo'] = whichComponent;
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

    sendToBackend(){
        // send this.state.postData to backend
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj['success'] = true;
            return newObj;
        });
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
                </div>
                <div class = "buttons col">
                    <button class = "createPostBtn" onClick = {() => {this.select('Story')}}>Story</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Location')}}>Location</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Time')}}>Time</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('People')}}>People</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Tags')}}>Tags</button>
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


class TestComponent extends React.Component{
    constructor(props){
        super(props);
        this.testSend = this.testSend.bind(this);
    }

    testSend(e){
        this.props.parentHandler('testComponent', e.target.value);
    }

    render(){
        return(
            <div>
                <input onChange = {this.testSend}></input>
            </div>
        );
    }
}

export default CreatePost;
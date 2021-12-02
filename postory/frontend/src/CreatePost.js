import React from 'react';

import Icon from '@mdi/react';
import { mdiRhombusSplit, mdiSend } from '@mdi/js';

import { Link } from "react-router-dom";
import TextChooser from './TextChooser'
import TimeChooser from './TimeChooser';
import TagChooser from './TagChooser';
import PeopleChooser from './PeopleChooser'
//import LocationChooser from './LocationChooser'
import LocationChooser from './LocationMap'
import Post from './Post';
import {TextField, Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';

class CreatePost extends React.Component{
    constructor(props){
        super(props);
        
        this.refStory = React.createRef();
        this.refLocation = React.createRef();
        this.refTime = React.createRef();
        this.refTags = React.createRef();

        this.allRefs = [this.refStory, this.refLocation, this.refTime, this.refTags];

        this.state = {
            selected : "Story",
            postData: {
                textChooser: {title : " ", body: " "},
                locationChooser: [],
                timeChooser: {startDate : "", startTime: ""},
                tagChooser: {selectedTags: []},
                owner:"USER",
                imageComponent: null
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
            "tagChooser": "Tag Content",
            "imageComponent": "Image Content"
        };
        this.setState(state => {
            //let newObj =  JSON.parse(JSON.stringify(state));
            let newObj = {...state};
            newObj.postData[whichComponent] = childObj;

            //problem code
            if(whichComponent == 'locationChooser')
                newObj.postData[whichComponent] = childObj.map((obj) => [obj]);
            //problem code
            newObj['addedInformation'] = true;
            newObj['whichInfo'] = infoDict[whichComponent];

            return newObj;
        });

        
    }

    select(obj){
        this.setState(state => {
            //let newObj =  JSON.parse(JSON.stringify(state));
            //newObj['selected'] = obj;
            return {
                ...state,
                selected: obj
            };
        });
    }

    handleSuccessClose(){
        this.setState(state => {
            //let newObj =  JSON.parse(JSON.stringify(state));
            //newObj['success'] = false;
            return {
                ...state,
                success: null
            };
        });
    }


    handleInfoClose(){
        this.setState(state => {
            //let newObj =  JSON.parse(JSON.stringify(state));
            //newObj['addedInformation'] = false;
            return {
                ...state,
                addedInformation: false
            };
        });
    }

    prepareObjectToSend(){

        if(this.state.postData["timeChooser"]["startDate"] == null){
            
            return ({
                title: this.state.postData["textChooser"]["title"],
                story: this.state.postData["textChooser"]["body"],
                owner: this.state.postData["owner"],
                locations: this.state.postData["locationChooser"],
                storyDate: (new Date()).toISOString(),
                editDate: (new Date()).toISOString(),
                postDate: (new Date()).toISOString(),
                tags: this.state.postData["tagChooser"]["selectedTags"],
                //images: this.state.postData['imageComponent'],
                preview: this.state.postData['imageComponent']
            });
        }
        return ({
            title: this.state.postData["textChooser"]["title"],
            story: this.state.postData["textChooser"]["body"],
            owner: this.state.postData["owner"],
            locations: this.state.postData["locationChooser"],
            storyDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z" ,
            editDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z",
            postDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z",
            tags: this.state.postData["tagChooser"]["selectedTags"],
            //images: this.state.postData['imageComponent'],
            preview: this.state.postData['imageComponent']
        });
    }



    sendToBackend(){
        // send this.state.postData to backend
        

        const objectToSend = this.prepareObjectToSend();


        const getFormData = object => Object.keys(object).reduce((formData, key) => {
            if(key == 'locations'  || key == 'tags')
                for(let el in object[key])
                    formData.append(key, object[key][el]);
            else
                formData.append(key, object[key]);
            return formData;
        }, new FormData());

        let formData = getFormData(objectToSend);

        let at_least_one = 0;
        for(const key in objectToSend.preview){
            if(objectToSend.preview[key] instanceof File){
                at_least_one = 1;
                formData.append('images', objectToSend.preview[key]);
            }
        }

        if(!at_least_one)
            formData.set('images', []);
        
        


        
        fetch('http://35.158.95.81:8000/api/post/create', {
            method: 'POST',
            body: formData
        }).then(res => {
            if(res.status != 200){
                console.log("ERROR" + res.data);
                this.setState(state => {
                    return {
                        ...state,
                        success: false,
                        creationError: `Response Status:  ${res.status} \n` + JSON.stringify(res)
                    };
                });
            }else {
                console.log("Post SUCCESS.")
                this.setState(state => {
                    return {
                        ...state,
                        success: true
                    };
                });
            }

        })
        .catch(err => {
            this.setState(state => {
                return {
                    ...state,
                    success: false,
                    creationError: err
                };
            });
            console.log("ERRRor" + err);
        });
        
        
    }


    render(){

        return(
            <header className="App-header">
            <div class = "row someSpacing">
                <div class = "inputArea">
                    <div class = {(this.state['selected'] != 'Story')? "hide": ""}><TextChooser ref = {this.refStory} parentHandler = {this.handleChildObjectSend}/> </div>
                    <div class = {(this.state['selected'] != 'Location')? "hide": ""}><LocationChooser ref = {this.refLocation} parentHandler = {this.handleChildObjectSend}/></div>
                    <div class = {(this.state['selected'] != 'Time')? "hide": ""}><TimeChooser ref = {this.refTime} parentHandler = {this.handleChildObjectSend}/></div>
                    <div class = {(this.state['selected'] != 'People')? "hide": ""}><PeopleChooser  parentHandler = {this.handleChildObjectSend}/></div>
                    <div class = {(this.state['selected'] != 'Tags')? "hide": ""}><TagChooser ref = {this.refTags} parentHandler = {this.handleChildObjectSend}/></div>
                    <div class = {(this.state['selected'] != 'Images')? "hide": ""}><ImageComponent  parentHandler = {this.handleChildObjectSend}/></div>
                    {this.state['selected'] == 'Preview' && <div ><PostPreviewComponent key = {this.state.postData.owner} postObject = {this.prepareObjectToSend()} parentHandler = {this.handleChildObjectSend}/></div>}
                    
                </div>
                <div class = "buttons col">
                    <button class = "createPostBtn" onClick = {() => {this.select('Story')}}>Story</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Location')}}>Location</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Time')}}>Time</button>
                    {/*<button class = "createPostBtn" onClick = {() => {this.select('People')}}>People</button>*/}
                    <button class = "createPostBtn" onClick = {() => {this.select('Tags')}}>Tags</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Images')}}>Images</button>
                    <button class = "createPostBtn" onClick = {() => {
                        this.refStory.current.sendParent();
                        this.refLocation.current.sendParent();
                        this.refTime.current.sendParent();
                        this.refTags.current.sendParent();
                        

                        this.select('Preview');
                        }}>Preview</button>
                    
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
                

                {/*
                <Snackbar open={this.state.addedInformation} autoHideDuration={1000} onClose={this.handleInfoClose} >
                    <Alert onClose={this.handleInfoClose} severity="success" sx={{ width: '100%' }}>
                        Information about {this.state.whichInfo} is added to your post.
                    </Alert>
                </Snackbar>
                */}


                <Snackbar open={this.state.success == false} autoHideDuration={1000} onClose={this.handleSuccessClose} >
                    <Alert onClose={this.handleSuccessClose} severity="error" sx={{ width: '100%' }}>
                        Your post could not be created. See below for the error message:
                        {this.state.creationError}
                    </Alert>
                </Snackbar>

                <Snackbar open={this.state.success} autoHideDuration={1000} onClose={this.handleSuccessClose} >
                    <Alert onClose={this.handleSuccessClose} severity="success" sx={{ width: '100%' }}>
                        Your post is successfully created.
                    </Alert>
                </Snackbar>
                
                <Link to= "/createPost" variant = "v6">
                    <Icon onClick = {() =>{
                    this.refStory.current.sendParent();
                    this.refLocation.current.sendParent();
                    this.refTime.current.sendParent();
                    this.refTags.current.sendParent();
                    setTimeout( this.sendToBackend, 10);
                    }
                } class = "circle homePageCreatePostButton" path={mdiSend}
                        title="Post"
                        size={2}
                        color="black"
                    />
                </Link>
                
            
            </div>
            </header>
        );
    }

}

class ImageComponent extends React.Component{
    constructor(props){
        super(props);
        this.handleFileChange = this.handleFileChange.bind(this);
    }

    handleFileChange(e){
        this.props.parentHandler('imageComponent',e.target.files);
    }

    render(){
        return(
            <div>
            <p>Please select suitable images for the post using the button below.</p>
                <input 
            type="file" id="file" 
            accept=".jpg, .png"
            multiple
            onChange={this.handleFileChange}
            />
            </div>
        );
    }
}


class PostPreviewComponent extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            value: props
        };
    }

    static getDerivedStateFromProps(props, current_state) {
        if (current_state.value !== props) {
          return {
            value: props
          }
        }
        return null
      }


    render(){
        return(
            <div>
                <Post {...this.props.postObject} ></Post>
            </div>
        );
    }
}

export default CreatePost;
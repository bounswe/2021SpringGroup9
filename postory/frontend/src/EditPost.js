import React from 'react';

import Icon from '@mdi/react';
import { mdiRhombusSplit, mdiSend } from '@mdi/js';

import { Link } from "react-router-dom";
import TextChooser from './TextChooser'
import TimeChooser from './TimeChooser';
import TagChooser from './TagChooser';
import PeopleChooser from './PeopleChooser'
import LocationChooser from './LocationMap'
import Post from './Post';
import {TextField, Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';
import * as requests from './requests'

const BACKEND_IP = '3.67.83.253';

class EditPost extends React.Component{
    constructor(props){
        super(props);

        console.log(window.location.href);
        const regex = /id=/g;
        const url = window.location.href;
        const idx = url.search(regex);
        const id = parseInt(url.slice(idx+3));
        console.log(url.slice(idx+3));

        this.refStory = React.createRef();
        this.refLocation = React.createRef();
        this.refTime = React.createRef();
        this.refTags = React.createRef();

        this.allRefs = [this.refStory, this.refLocation, this.refTime, this.refTags];

        this.state = {
            id: id,
            selected : "Story",
            postData: {
                textChooser: {title : " ", body: " "},
                locationChooser: [],
                timeChooser: {
                    startYear: null,
                    endYear: null,
                    startMonth: null,
                    endMonth: null,
                    startDay: null,
                    endDay: null,
                    startTime: null,
                    endTime: null,
                },
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

    componentDidMount(){
        //fetch(`http://${backendIP}/api/post/get/${this.state.id}`).
        requests.get_jwt(`/api/post/get/${this.state.id}`,{}).then(resp => resp.json()).then(
            data => {
                this.setState(state=>{return {
                    ...state,
                    postData: {
                        ...state.postData,
                        owner: data.owner
                    }
                }});
                this.refLocation.current.getEditInfo(data);
                data.locations = data.locations.map( (obj, i) => obj[0]);
                console.log(data);
                this.refStory.current.getEditInfo(data);
                this.refTags.current.getEditInfo(data);
                
                this.refTime.current.getEditInfo(data);
            }
        );
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
                newObj.postData[whichComponent] = childObj;
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
        /*
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
        }*/
        let obj = {
            title: this.state.postData["textChooser"]["title"],
            story: this.state.postData["textChooser"]["body"],
            owner: this.state.postData["owner"],
            locations: this.state.postData["locationChooser"],
            startYear: this.state.postData["timeChooser"]["startYear"],
            endYear: this.state.postData["timeChooser"]["endYear"] , 
            startMonth: this.state.postData["timeChooser"]["startMonth"],
            endMonth: this.state.postData["timeChooser"]["endMonth"] , 
            startDay: this.state.postData["timeChooser"]["startDay"],
            endDay: this.state.postData["timeChooser"]["endDay"] , 
            startTime: this.state.postData["timeChooser"]["startTime"],
            endTime: this.state.postData["timeChooser"]["endTime"] , 
            //storyDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z" ,
            //editDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z",
            //postDate: this.state.postData["timeChooser"]["startDate"] + "T" +this.state.postData["timeChooser"]["startTime"] + ":0.0Z",
            tags: this.state.postData["tagChooser"]["selectedTags"],
            //images: this.state.postData['imageComponent'],
            preview: this.state.postData['imageComponent']
        };
        let objectToSend = {};
        for(let key in obj){
            if(obj[key])
                objectToSend[key] = obj[key];
        }


        return (objectToSend);
    }



    sendToBackend(){
        // send this.state.postData to backend
        

        const objectToSend = this.prepareObjectToSend();


        const getFormData = object => Object.keys(object).reduce((formData, key) => {
            if ( key.includes('Year')){
                formData.append('year', object[key]);
            }else if (key.includes('Month')){
                formData.append('month', object[key]);
            }else if (key.includes('Day')){
                formData.append('day', object[key]);
            }
            else if(key == 'locations'  )
                for(let el in object[key])
                    formData.append(key, JSON.stringify(object[key][el]));
            else if(key == 'tags')
                for(let el in object[key])
                    formData.append(key, object[key][el]);
            else
                formData.append(key, object[key]);
            return formData;
        }, new FormData());

        

        
        let formData = getFormData(objectToSend);

        console.log(formData.getAll('tags'));
        let at_least_one = 0;
        for(const key in objectToSend.preview){
            if(objectToSend.preview[key] instanceof File){
                at_least_one = 1;
                formData.append('images', objectToSend.preview[key]);
            }
        }

        if(!at_least_one)
            formData.set('images', []);


        console.log(...formData);
        fetch(`http://${BACKEND_IP}:8000/api/post/put/${this.state.id}`, {
            method: 'PUT',
            headers: {
                'Authorization': `JWT ${localStorage.getItem('access')}`
            },
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
                //http://35.158.95.81:8000/api/post/delete/<int:id>

                
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
            <div class = "row2 someSpacing">
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
                    
                    {/*<TextField id="userNameField" label="Enter Your Name" variant="filled" focused 
                        onChange = {(e) => this.setState(state => {
                            let postData = state.postData;
                            return {
                                ...state,
                                postData: {
                                    ...postData,
                                    owner: e.target.value
                                }
                            };
                        
                        })}/>*/}
                </div>
                

                {/*
                <Snackbar open={this.state.addedInformation} autoHideDuration={1000} onClose={this.handleInfoClose} >
                    <Alert onClose={this.handleInfoClose} severity="success" sx={{ width: '100%' }}>
                        Information about {this.state.whichInfo} is added to your post.
                    </Alert>
                </Snackbar>
                */}


                <Snackbar open={this.state.success == false} autoHideDuration={5000} onClose={this.handleSuccessClose} >
                    <Alert onClose={this.handleSuccessClose} severity="error" sx={{ width: '100%' }}>
                        Your post could not be created. See below for the error message:
                        {this.state.creationError}
                    </Alert>
                </Snackbar>

                <Snackbar open={this.state.success} autoHideDuration={5000} onClose={this.handleSuccessClose} >
                    <Alert onClose={this.handleSuccessClose} severity="success" sx={{ width: '100%' }}>
                        Your post is successfully created.
                    </Alert>
                </Snackbar>
                
                <Link to= {`/editPost?id=${this.state.id}`} variant = "v6">
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

export default EditPost;
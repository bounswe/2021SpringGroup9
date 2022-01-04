import React, { useEffect } from "react";
import { withScriptjs, withGoogleMap, GoogleMap, Marker} from "react-google-maps";
import InfoBox from "react-google-maps/lib/components/addons/InfoBox";
import Icon from '@mdi/react'
import { mdiGestureTap } from '@mdi/js';
import { Navigate} from "react-router-dom";
import * as requests from './requests'
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import Button from 'react-bootstrap/Button'
import InputGroup from 'react-bootstrap/InputGroup'
import FormControl from 'react-bootstrap/FormControl'
import Badge from 'react-bootstrap/Badge'
import Tooltip from '@mui/material/Tooltip';
import {Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';

import { mdiPound, mdiCalendarRange, mdiFountainPenTip, mdiAccount, mdiMapMarkerRadius } from '@mdi/js';

const MyMapComponent = withScriptjs(withGoogleMap((props) =>{
    const [selectedPost, setSelectedPost] = React.useState(null);
    const [currentLocation, setCurrentLocation] = React.useState({ lat: 41.048, lng: 29.0510 });
    const [searchLocation, setSearchLocation] = React.useState({ lat: 43, lng: 25 });
    const [displayPost, setDisplayPost] = React.useState(false);
    const [clickCount, setClickCount] = React.useState(0);
    const [clearInfoBox, setClearInfoBox] = React.useState(0);
    const [displayInfoBox, setDisplayInfoBox] = React.useState(false);



    useEffect(() => {
        if (selectedPost != null){
            setDisplayInfoBox(false);
            setTimeout(() => {setDisplayInfoBox(true)}, 500);
        }
        console.log("Selected post has been changed");
    }, [clickCount])

    useEffect(() => {
        setDisplayInfoBox(false);
    }, [clearInfoBox])

    const onClickMarker = (index, obj) =>{
        if (obj.lat==currentLocation.lat & obj.lng==currentLocation.lng){
            setClearInfoBox(clearInfoBox + 1);
        }else {
            setCurrentLocation({lat:obj.lat, lng:obj.lng});
            setSelectedPost(() =>{
                let newPost = null;
                for(let i =0 ; i< props.posts.length; i++){
                    if(props.posts[i].id == props.markerList[index].id){
                        newPost = props.posts[i];
                    }
                }
                console.log(newPost);
                setClickCount(clickCount + 1);
                return newPost;
            });    
        }   
    }

    const onDragMarkerSearch = (t) =>{
        setSearchLocation({lat: t.latLng.lat(), lng : t.latLng.lng()}); 
        console.log(t.latLng.lat());    
        console.log(t.latLng.lng());
        props.searchCenter({lat: t.latLng.lat(), lng : t.latLng.lng()})
    }

    const onClickInfoBox = () =>{
        if (selectedPost != null){
            setDisplayPost(false);
            setTimeout(() => {setDisplayPost(true)}, 500);
        }  
    }

    return( <GoogleMap
                defaultZoom={6}
                defaultCenter={{ lat: 41.048, lng: 29.0510 }}
                >
                {displayInfoBox && 
                <InfoBox
                defaultPosition={{lat:currentLocation.lat, lng:currentLocation.lng}}
                options={{ closeBoxURL: ``, enableEventPropagation: true }}
                >
                    <div style={{ backgroundColor: `white`, opacity: 1, padding: `12px` }} onClick = {() => onClickInfoBox()} >
                        <div style={{ fontSize: `16px`, fontColor: `#08233B`, fontWeight: `bold` }}>
                            {selectedPost.title}
                        </div>
                        <div style={{ fontSize: `12px`, fontColor: `#08233B`, fontStyle: `italic` }}>
                            by: {selectedPost.username}
                        </div>
                        <div class= "row2" onClick = {() => props.redirect(selectedPost.id)}>
                                <Icon 
                                    path={mdiGestureTap} 
                                    size={1}
                                />
                                <div style={{ fontSize: `10px`, fontColor: `#C40303` }}>
                                    Click here to be redirected to the post page.
                                </div>
                        </div>
                    </div>
                </InfoBox>
                } 
                {props.markerList.map((obj,i) => {
                    return (<Marker onClick = {() => onClickMarker(i, obj)} position = {{lat:obj.lat, lng:obj.lng}} key = {i}/>);
                })}
                <Marker draggable={true} position = {{lat:searchLocation.lat, lng:searchLocation.lng}} 
                        options={{icon: `https://user-images.githubusercontent.com/35606355/147581280-de0e8e59-2a80-4b79-aef8-c0f30be2a87a.png`}}
                        onDragEnd = {(t, map, coord)  => onDragMarkerSearch(t)}/>
            </GoogleMap>
            );
  }
))

class DiscoverPage extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            selectedPost : null,
            searchCenter: { lat: 43, lng: 25 },
            tagValue: '', // Holds the last entered tag as an input
            selectedTags: [], // Holds all the tags that are entered by the user
            userValue: '', // Holds the last entered user as an input
            selectedUsers: [], // Holds all the users that are entered by the user
            keyword: '',
            selectedKeywords: [],
            startYear: null,
            endYear: null,
            startMonth: null,
            endMonth: null,
            startDay: null,
            endDay: null,
            startTime: null,
            endTime: null,
            searchAreaKm: null,
            searchAreaKmCurrent: null,
            showKm: false,
            markerList: [],
            posts: [],
            differentPage: false,
            isRelatedSearch: false,
            wikiData: '',
            showWikiData: false
        };
    }

    componentDidMount() {
        requests.get_jwt('/api/post/all/discover',{})
            .then(response => response.json())
            .then( (data) => {
                this.setState({ posts: data });
                const markerList = [];
                for(let i = 0; i < data.length; i++) {
                    console.log(data[i]);
                    let id = data[i].id;
                    for(let j = 0; j < data[i].locations.length; j++) {
                        let lat = data[i].locations[j][1];
                        let lng = data[i].locations[j][2];
                        markerList.push({id: id, lat: lat, lng: lng});
                    }
                }
                console.log(markerList);
                this.setState({ markerList: markerList });
        })
    }

    onClickBrowse = () => {
        var filterURL = 'filter?'

        for(let k = 0; k < this.state.selectedKeywords.length; k++){
            if (k == 0)
                filterURL += 'keyword='
            filterURL +=  this.state.selectedKeywords[k];
            if (k+1 == this.state.selectedTags.length){
                filterURL += '&';
            }else{
                filterURL += ' '
            }
        }

        if(this.state.searchAreaKm){
            filterURL += 'latitude=' + this.state.searchCenter['lat'] + '&';
            filterURL += 'longitude=' + this.state.searchCenter['lng'] + '&';
            filterURL += 'distance=' + this.state.searchAreaKm + '&';
        }

        if(this.state.selectedTags){
            filterURL += 'related=' + this.state.isRelatedSearch + '&';
        }

        for(let k = 0; k < this.state.selectedTags.length; k++){
            filterURL += 'tag=' + this.state.selectedTags[k] + '&'; 
        }

        for(let k = 0; k < this.state.selectedUsers.length; k++){
            filterURL += 'user=' + this.state.selectedUsers[k] + '&'; 
        }

        if(this.state.startYear){
            filterURL += 'startYear=' + this.state.startYear + '&';
        }
        if(this.state.endYear){
            filterURL += 'endYear=' + this.state.endYear + '&';
        }
        if(this.state.startMonth){
            filterURL += 'startMonth=' + this.state.startMonth + '&';
        }
        if(this.state.endMonth){
            filterURL += 'endMonth=' + this.state.endMonth + '&';
        }
        if(this.state.startDay){
            filterURL += 'startDay=' + this.state.startDay + '&';
        }
        if(this.state.endDay){
            filterURL += 'endDay=' + this.state.endDay + '&';
        }
        if(this.state.startTime){
            filterURL += 'startTime=' + this.state.startTime + '&';
        }
        if(this.state.endTime){
            filterURL += 'endTime=' + this.state.endTime + '&';
        }

        filterURL = filterURL.slice(0, -1);
        requests.get_jwt(`/api/post/all/${filterURL}`,{})
            .then(response => response.json())
            .then( (data) => {
                this.setState({ posts: data });
                const markerList = [];
                for(let i = 0; i < data.length; i++) {
                    console.log(data[i]);
                    let id = data[i].id;
                    for(let j = 0; j < data[i].locations.length; j++) {
                        let lat = data[i].locations[j][1];
                        let lng = data[i].locations[j][2];
                        markerList.push({id: id, lat: lat, lng: lng});
                    }
                }
                console.log(markerList);
                this.setState({ markerList: markerList });
        })
    };

    onClickNewPage = () => {
        var filterURL = 'filter?'

        for(let k = 0; k < this.state.selectedKeywords.length; k++){
            if (k == 0)
                filterURL += 'keyword='
            filterURL +=  this.state.selectedKeywords[k];
            if (k+1 == this.state.selectedTags.length){
                filterURL += '&';
            }else{
                filterURL += ' '
            }
        }

        if(this.state.searchAreaKm){
            filterURL += 'latitude=' + this.state.searchCenter['lat'] + '&';
            filterURL += 'longitude=' + this.state.searchCenter['lng'] + '&';
            filterURL += 'distance=' + this.state.searchAreaKm + '&';
        }

        if(this.state.selectedTags){
            filterURL += 'related=' + this.state.isRelatedSearch + '&';
        }

        for(let k = 0; k < this.state.selectedTags.length; k++){
            filterURL += 'tag=' + this.state.selectedTags[k] + '&'; 
        }

        for(let k = 0; k < this.state.selectedUsers.length; k++){
            filterURL += 'user=' + this.state.selectedUsers[k] + '&'; 
        }

        if(this.state.startYear){
            filterURL += 'startYear=' + this.state.startYear + '&';
        }
        if(this.state.endYear){
            filterURL += 'endYear=' + this.state.endYear + '&';
        }
        if(this.state.startMonth){
            filterURL += 'startMonth=' + this.state.startMonth + '&';
        }
        if(this.state.endMonth){
            filterURL += 'endMonth=' + this.state.endMonth + '&';
        }
        if(this.state.startDay){
            filterURL += 'startDay=' + this.state.startDay + '&';
        }
        if(this.state.endDay){
            filterURL += 'endDay=' + this.state.endDay + '&';
        }
        if(this.state.startTime){
            filterURL += 'startTime=' + this.state.startTime + '&';
        }
        if(this.state.endTime){
            filterURL += 'endTime=' + this.state.endTime + '&';
        }

        filterURL = filterURL.slice(0, -1);
        console.log(filterURL)
        console.log(`/api/post/all/${filterURL}`)
        requests.get_jwt(`/api/post/all/${filterURL}`,{})
            .then(response => response.json())
            .then( (data) => {
                console.log(data)
                localStorage.setItem('filteredPosts', JSON.stringify(data));
                this.setState({ differentPage: true });
                
        })

    }


    onChangeRelatedCheckbox = () => {
        this.setState({ isRelatedSearch: !this.state.isRelatedSearch });
    }

    clearFilters = () => {
        this.setState({ 
            isRelatedSearch: !this.state.isRelatedSearch,
            selectedPost: null,
            tagValue: '',
            selectedTags: [], 
            userValue: '', 
            selectedUsers: [], 
            keyword: '',
            selectedKeywords: [],
            startYear: null,
            endYear: null,
            startMonth: null,
            endMonth: null,
            startDay: null,
            endDay: null,
            startTime: null,
            endTime: null,
            searchAreaKm: null,
            searchAreaKmCurrent: null,
            showKm: false,
            differentPage: false,
            isRelatedSearch: false,
            wikiData: '',
            showWikiData: false
         });
    }

    onClickTag = (tag) => {
        requests.get_jwt(`/api/post/related/${tag}`,{})
            .then(response => response.json())
            .then( (data) => {
                console.log(data);
                var wikiResponse = ''
                for(let i = 0; i < data.length; i++){
                    wikiResponse += data[i] + ' - '
                    if (i == 5)
                        break
                }
                this.setState({ showWikiData: true });
                this.setState({ wikiData: wikiResponse.slice(0, -2) });
        })

    }

    closeWikiData = () => {
        this.setState({ showWikiData: false });
    };

    onChangeTagValue = event => {
        {/* Called when when there is change in the input box allows user to enter tag*/}
        this.setState({ tagValue: event.target.value });
    };

    onChangeUserValue = event => {
        {/* Called when when there is change in the input box allows user to enter user*/}
        this.setState({ userValue: event.target.value });
    };

    onChangeKeywordValue = event => {
        {/* Called when when there is change in the input box allows user to enter keyword*/}
        this.setState({ keyword: event.target.value });
    };

    addTagToSelectedTags = () => {
        {/* Called when user clicks on add button to add the last entered tag (state.tagValue)
            It then concats the previous selectedTags list with the last entered tag and returns */}
        this.setState(state => {
            const selectedTags = state.selectedTags.concat(state.tagValue);
            return {
                selectedTags,
                tagValue: '',
            };
        });
    };

    addTagToSelectedKeywords = () => {
        {/* Called when user clicks on add button to add the last entered keyword (state.keyword)
            It then concats the previous selectedKeywords list with the last entered keyword and returns */}
        this.setState(state => {
            const selectedKeywords = state.selectedKeywords.concat(state.keyword);
            return {
                selectedKeywords,
                keyword: '',
            };
        });
    };

    addTagToSelectedUsers = () => {
        {/* Called when user clicks on add button to add the last entered user (state.userValue)
            It then concats the previous selectedUsers list with the last entered user and returns */}
        this.setState(state => {
          const selectedUsers = state.selectedUsers.concat(state.userValue);
     
          return {
            selectedUsers,
            userValue: '',
          };
        });
    };

    removeKeyword = i => {
        {/* Called when user clicks on x button next to each tag added previously to the post.
            It filters the tag that wanted to be removed from selectedTags array returns it */}
        this.setState(state => {
          const selectedKeywords = state.selectedKeywords.filter((item, j) => i !== j);
     
          return {
            selectedKeywords,
          };
        });
    };

    removeTag = i => {
        {/* Called when user clicks on x button next to each tag added previously to the post.
            It filters the tag that wanted to be removed from selectedTags array returns it */}
        this.setState(state => {
          const selectedTags = state.selectedTags.filter((item, j) => i !== j);
     
          return {
            selectedTags,
          };
        });
    };

    removeUser = i => {
        {/* Called when user clicks on x button next to each user added previously.
            It filters the user that wanted to be removed from selectedUsers array returns it */}
        this.setState(state => {
          const selectedUsers = state.selectedUsers.filter((item, j) => i !== j);
     
          return {
            selectedUsers,
          };
        });
    };


    render(){
        let key = process.env.REACT_APP_GOOGLE_API_KEY?(`key=` + process.env.REACT_APP_GOOGLE_API_KEY + "&") : "";
        return(   
        <div className="App" style={{ backgroundColor: `#EBEBEB`}}>
            <div style={{ height: window.innerHeight * 1/19, width: window.innerWidth }}/>
            <hr style={{ color: `black`, backgroundColor: `black`, height: 2 }}/>
            <Container>
                <Row style={{alignItems: `center`}}> 
                    <Col sm={2}> 
                        <Icon path={mdiPound} size={1}/> Tags &nbsp;&nbsp;
                        <Tooltip describeChild title="Check this box if you want to include all the related tags">
                        <input
                            type="checkbox"
                            checked={this.state.isRelatedSearch}
                            onChange={this.onChangeRelatedCheckbox}
                        />
                        </Tooltip>
                    </Col>
                    <Col sm={3} >
                        <Icon path={mdiCalendarRange} size={1}/> Date
                    </Col>
                    <Col sm={2} >
                        <Icon path={mdiFountainPenTip} size={1}/> Keyword
                    </Col>
                    <Col sm={3} >
                        <Icon path={mdiAccount} size={1}/> User
                    </Col>
                    <Col sm={2} >
                        <Icon path={mdiMapMarkerRadius} size={1}/> Area (km)
                    </Col>
                </Row>
                <Row style={{alignItems: `center`}}>
                    <Col sm={2}>
                        <InputGroup className="mb-3">
                            <FormControl
                                type="text"
                                value={this.state.tagValue}
                                onChange={this.onChangeTagValue}
                                style={{width: "100px"}}
                                placeholder="Enter a tag"
                                id='enterTagInput'
                            />
                            <Button 
                                variant="outline-secondary"
                                type="button"
                                onClick={this.addTagToSelectedTags}
                                disabled={!this.state.tagValue}
                                id='addTagButton'>
                            Add Tag
                            </Button>
                        </InputGroup>
                    </Col>
                    <Col sm={3}>
                        <InputGroup className="mb-3">
                            <InputGroup.Text>Year</InputGroup.Text>
                            <FormControl placeholder="Start year*" type="number" min="1900" max="2099" step="1" value={this.state.startYear || ""}  onChange={
                                e => this.setState(state => ({...state, startYear: e.target.value.toString()}))
                            }/>
                            <FormControl placeholder="End year" type="number" min="1900" max="2099" step="1" value={this.state.endYear || ""}  onChange={
                                e => this.setState(state => ({...state, endYear: e.target.value.toString()}))
                            }/>
                        </InputGroup>
                    </Col>
                    <Col sm={2}>
                        <InputGroup className="mb-3">
                            <FormControl
                                type="text"
                                value={this.state.keyword}
                                onChange={this.onChangeKeywordValue}
                                style={{width: "100px"}}
                                placeholder="Enter a keyword"
                            />
                            <Button 
                                variant="outline-secondary"
                                type="button"
                                onClick={this.addTagToSelectedKeywords}
                                disabled={!this.state.keyword}>
                            Add
                            </Button>
                        </InputGroup>
                    </Col>
                    <Col sm={3}>
                        <InputGroup className="mb-3">
                            <FormControl
                                type="text"
                                value={this.state.userValue}
                                onChange={this.onChangeUserValue}
                                style={{width: "100px"}}
                                placeholder="Enter a user"
                            />
                            <Button 
                                variant="outline-secondary"
                                type="button"
                                onClick={this.addTagToSelectedUsers}
                                disabled={!this.state.userValue}>
                            Add User
                            </Button>
                        </InputGroup>
                    </Col>
                    <Col sm={2}>
                        <InputGroup className="mb-3">
                            <FormControl
                                type="number"
                                value={this.state.searchAreaKmCurrent || ""}
                                onChange={
                                    e => this.setState(state => ({...state, searchAreaKmCurrent: e.target.value.toString()}))
                                }
                                style={{width: "100px"}}
                                placeholder="Enter km"
                            />
                            <Button 
                                variant="outline-secondary"
                                type="button"
                                onClick={() => this.setState(state => ({...state, showKm: true, searchAreaKm: this.state.searchAreaKmCurrent, searchAreaKmCurrent: ''}))}
                                disabled={!this.state.searchAreaKmCurrent}>
                            Enter
                            </Button>
                        </InputGroup>
                    </Col>
                </Row>
                <Row style={{alignItems: `center`}}>
                    <Col sm={2} style={{maxHeight: 70, overflow: 'auto'}}>
                        {this.state.selectedTags.map((item, index) => (
                            <div key={item}>
                            <Tooltip describeChild title='Click to see the related tags'>
                            <Badge bg="primary" onClick={() => this.onClickTag(item)}>
                                {item}
                            </Badge>
                            </Tooltip>
                            <Button
                                type="button"
                                variant="outline-secondary"
                                size="sm"
                                onClick={() => this.removeTag(index)}
                            >
                                x
                            </Button>
                            </div>
                        ))}
                    </Col>
                    <Col sm={3}>
                        {this.state.startYear && this.state.endYear &&
                            <InputGroup className="mb-3">
                                <InputGroup.Text>Month</InputGroup.Text>
                                <FormControl placeholder="Start month" type="number" min="1" max="12" step="1" value={this.state.startMonth || ""}  onChange={
                                    e => this.setState(state => ({...state, startMonth: e.target.value.toString()}))
                                }/>
                                <FormControl placeholder="End month" type="number" min="1" max="12" step="1" value={this.state.endMonth || ""}  onChange={
                                    e => this.setState(state => ({...state, endMonth: e.target.value.toString()}))
                                }/>
                            </InputGroup>
                        }
                        
                    </Col>
                    <Col sm={2} style={{maxHeight: 70, overflow: 'auto'}}>
                        {this.state.selectedKeywords.map((item, index) => (
                            <div key={item}>
                            <Badge bg="success">
                                {item}
                            </Badge>
                            <Button
                                type="button"
                                variant="outline-secondary"
                                size="sm"
                                onClick={() => this.removeKeyword(index)}
                            >
                                x
                            </Button>
                            </div>
                        ))}
                        
                    </Col>
                    <Col sm={3} style={{maxHeight: 70, overflow: 'auto'}}>
                        {this.state.selectedUsers.map((item, index) => (
                            <div key={item}>
                            <Badge bg="secondary">
                                {item}
                            </Badge>
                            <Button
                                type="button"
                                variant="outline-secondary"
                                size="sm"
                                onClick={() => this.removeUser(index)}
                            >
                                x
                            </Button>
                            </div>
                        ))}
                    </Col>
                    <Col sm={2}>
                    <div style={{ fontSize: `14px`, fontColor: `#C40303`, fontStyle: `italic` }}>
                        You can drag the blue marker on the map representing the center of the area will be searched
                    </div>
                    </Col>
                </Row>
                <Row style={{alignItems: `center`}}>
                    <Col sm={2}></Col>
                    <Col sm={3}>
                        {this.state.startMonth && this.state.endMonth &&
                        <InputGroup className="mb-3">
                            <InputGroup.Text>Day</InputGroup.Text>
                            <FormControl placeholder="Start day" type="number" min="1" max="31" step="1" value={this.state.startDay || ""}  onChange={
                                e => this.setState(state => ({...state, startDay: e.target.value.toString()}))
                            }/>
                            <FormControl placeholder="End month" type="number" min="1" max="31" step="1" value={this.state.endDay || ""}  onChange={
                                e => this.setState(state => ({...state, endDay: e.target.value.toString()}))
                            }/>
                        </InputGroup>
                        }
                    </Col>
                    <Col sm={7}> </Col>
                </Row>
                <Row style={{alignItems: `center`}}>
                    <Col sm={2}></Col>
                    <Col sm={3}>
                        {this.state.startDay && this.state.endDay &&
                        <InputGroup className="mb-3">
                            <InputGroup.Text>Hour</InputGroup.Text>
                            <FormControl placeholder="Start hour" type="number" min="1" max="31" step="1" value={this.state.startTime || ""}  onChange={
                                e => this.setState(state => ({...state, startTime: e.target.value.toString()}))
                            }/>
                            <FormControl placeholder="End hour" type="number" min="1" max="31" step="1" value={this.state.endTime || ""}  onChange={
                                e => this.setState(state => ({...state, endTime: e.target.value.toString()}))
                            }/>
                        </InputGroup>
                        }
                    </Col>
                    <Col sm={5}> </Col>
                    <Col sm={2}>
                    {this.state.showKm &&
                        <div>
                            {this.state.searchAreaKm} km2
                        
                        <Button
                            type="button"
                            variant="outline-secondary"
                            size="sm"
                            onClick={() => this.setState(state => ({...state, showKm: false, searchAreaKm: null}))}
                        >
                            x
                        </Button>
                        </div>
                    }          
                    </Col>
                </Row>
                <Row style={{alignItems: `center`}}>
                    <Col sm={4}>
                        <Button 
                            variant="primary"
                            onClick={this.onClickBrowse}
                            >Filter & Browse on Map  
                        </Button>
                    </Col>
                    <Col sm={4}>
                        <Button 
                            variant="primary"
                            onClick={this.onClickNewPage}
                            >Show Resulting Posts
                        </Button>
                    </Col>
                    <Col sm={4}>
                        <Button 
                            variant="danger"
                            onClick={this.clearFilters}
                            >Clear Filters
                        </Button>
                    </Col>
                </Row>
               
            </Container>
            <div style={{ height: window.innerHeight * 1/40, width: window.innerWidth }}/>
        <header className="App-header">
        <MyMapComponent 
                    redirect = {(id) => this.setState({selectedPost : id})}
                    searchCenter = {({lat, lng}) => this.setState({searchCenter : {lat, lng}})}
                    markerList = {this.state.markerList}
                    posts = {this.state.posts}
                    isMarkerShown
                    googleMapURL={`https://maps.googleapis.com/maps/api/js?${key}v=3.exp&libraries=geometry,drawing,places`}
                    loadingElement={<div style={{ height: `100%` }} />}
                    containerElement={<div style={{ height: window.innerHeight, width: window.innerWidth }} />} //`600px`
                    mapElement={<div style={{ height: `100%` }} />}
                />
        </header>
        {this.state.selectedPost && <Navigate class = "push" to= {`/viewPost?id=${this.state.selectedPost}`}> click here to see the full post</Navigate>}
        {this.state.differentPage && <Navigate class = "push" to= {`/filteredPosts`}>Navigating to filteredPosts page</Navigate>}
        <Snackbar open={this.state.showWikiData} autoHideDuration={3000} onClose={this.closeWikiData}>
            <Alert onClose={this.closeWikiData} severity="info" sx={{ width: '100%' }}>
              {this.state.wikiData}
            </Alert>
        </Snackbar>
        </div>      
        )
    }
}
export default DiscoverPage;
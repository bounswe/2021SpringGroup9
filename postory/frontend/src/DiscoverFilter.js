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

import { mdiPound, mdiCalendarRange, mdiFountainPenTip, mdiAccount, mdiMapMarkerRadius } from '@mdi/js';

const MyMapComponent = withScriptjs(withGoogleMap((props) =>{
    const [markers, setMarkers] = React.useState([]);
    const [posts, setPosts] = React.useState([]);
    const [selectedPost, setSelectedPost] = React.useState(null);
    const [currentLocation, setCurrentLocation] = React.useState({ lat: 41.048, lng: 29.0510 });
    const [searchLocation, setSearchLocation] = React.useState({ lat: 43, lng: 25 });
    const [displayPost, setDisplayPost] = React.useState(false);
    const [clickCount, setClickCount] = React.useState(0);
    const [displayInfoBox, setDisplayInfoBox] = React.useState(false);


    useEffect(() => {
        requests.get_jwt('/api/post/all/discover',{})
            .then(response => response.json())
            .then( (data) => {
                setPosts(data);
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
                setMarkers(markerList);
            })
    }, [])

    useEffect(() => {
        if (selectedPost != null){
            setDisplayInfoBox(false);
            setTimeout(() => {setDisplayInfoBox(true)}, 500);
        }
        console.log("Selected post has been changed");
    }, [clickCount])

    const onClickMarker = (index, obj) =>{
        setCurrentLocation({lat:obj.lat, lng:obj.lng});
        setSelectedPost(() =>{
            let newPost = null;
            for(let i =0 ; i< posts.length; i++){
                if(posts[i].id == markers[index].id){
                    newPost = posts[i];
                }
            }
            console.log(newPost);
            setClickCount(clickCount + 1);
            return newPost;
        });       
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
                {markers.map((obj,i) => {
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
        };
    }

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
                        <Icon path={mdiPound} size={1}/> Tags 
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
                            />
                            <Button 
                                variant="outline-secondary"
                                type="button"
                                onClick={this.addTagToSelectedTags}
                                disabled={!this.state.tagValue}>
                            Add
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
                            Add
                            </Button>
                        </InputGroup>
                    </Col>
                    <Col sm={2}>
                        <InputGroup className="mb-3">
                            <FormControl
                                type="number"
                                value={this.state.searchAreaKm || ""}
                                onChange={
                                    e => this.setState(state => ({...state, searchAreaKm: e.target.value.toString()}))
                                }
                                style={{width: "100px"}}
                                placeholder="Enter km"
                            />
                            <Button 
                                variant="outline-secondary"
                                type="button"
                                onClick={() => this.setState(state => ({...state, showKm: true}))}
                                disabled={!this.state.searchAreaKm}>
                            Enter
                            </Button>
                        </InputGroup>
                    </Col>
                </Row>
                <Row style={{alignItems: `center`}}>
                    <Col sm={2} style={{maxHeight: 70, overflow: 'auto'}}>
                        {this.state.selectedTags.map((item, index) => (
                            <div>
                            <Badge pill bg="primary">
                                {item}
                            </Badge>
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
                            <div>
                            <Badge pill bg="success">
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
                            <div>
                            <Badge pill bg="secondary">
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
               
            </Container>
            <div style={{ height: window.innerHeight * 1/40, width: window.innerWidth }}/>
        <header className="App-header">
        <MyMapComponent 
                    redirect = {(id) => this.setState({selectedPost : id})}
                    searchCenter = {({lat, lng}) => this.setState({searchCenter : {lat, lng}})}
                    isMarkerShown
                    googleMapURL={`https://maps.googleapis.com/maps/api/js?${key}v=3.exp&libraries=geometry,drawing,places`}
                    loadingElement={<div style={{ height: `100%` }} />}
                    containerElement={<div style={{ height: window.innerHeight, width: window.innerWidth }} />} //`600px`
                    mapElement={<div style={{ height: `100%` }} />}
                />
        </header>
        {this.state.selectedPost && <Navigate class = "push" to= {`/viewPost?id=${this.state.selectedPost}`}> click here to see the full post</Navigate>}
        </div>      
        )
    }
}
export default DiscoverPage;
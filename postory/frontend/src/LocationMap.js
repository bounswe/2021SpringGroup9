import React, { useEffect } from 'react'
import './LocationChooser.css'
import {TextField, Snackbar} from '@material-ui/core';
import Alert from '@material-ui/lab/Alert';


import { withScriptjs, withGoogleMap, GoogleMap, Marker } from "react-google-maps";

/**
 * A component for adding removing marker by clicking on a map for the LocationMap component.
 */
const MapComponent = withScriptjs(withGoogleMap((props) =>{
    const [markers, setMarkers] = React.useState([]);
    const [tooManyMarkers, setTooManyMarkers] = React.useState(false);
    const markersLimit = 7;


    useEffect(() => {
        if(props.editLocations)
        setAllMarkers(props.editLocations);
    },[]);

    const deleteMarker = (index) =>{
        setMarkers(state =>{
            let newState = [];
            for(let i =0 ; i< state.length; i++)
                if(i != index)
                    newState.push(state[i]);
            props.setParentLocation(newState);
            return newState;
        });

        
    }

    const addMarker = (e) =>{
        if(markers.length  < markersLimit)
            setMarkers(state => {
                const newState =  [...state, {name : '', lat: e.latLng.lat(), lng : e.latLng.lng()}]; 
                props.setParentLocation(newState);
                return newState;
            });
        else {
            setTooManyMarkers(true);
        }
    }

    const setAllMarkers = (locations) =>{
        setMarkers(locations);
    }

return(<div>
    <Snackbar open={tooManyMarkers} autoHideDuration={1000} onClose={() => setTooManyMarkers(false)} >
    <Alert onClose={() => setTooManyMarkers(false)} severity="error" sx={{ width: '100%' }}>
        You can add at most {markersLimit} Locations.
    </Alert>
</Snackbar><GoogleMap
        
        defaultZoom={8}
        defaultCenter={{ lat: 41, lng: 28 }}
   onClick = {addMarker} >
        {markers.map((obj,i) => {
            return (<Marker draggable={true} onDragEnd = {(t, map, coord)  => setMarkers(markers => {markers[i] = {lat: t.latLng.lat(), lng : t.latLng.lng()}; return markers;})} options={{icon:`https://mt.google.com/vt/icon/text=${i+1}&psize=16&font=fonts/arialuni_t.ttf&color=ff330000&name=icons/spotlight/spotlight-waypoint-b.png&ax=44&ay=48&scale=1`}} onClick = {() => deleteMarker(i)}position = {obj} key = {i}/>);
        })}
    </GoogleMap>
    </div>);
  }
))



class LocationChooser extends React.Component{
    /**
     * This component is used on create/edit post pages to select a Post's locations.
     * It uses the MapComponent to render the markers on the page.
     * @param {*} props 
     */
    constructor(props){
        super(props);

        this.parentHandler = props.parentHandler;
        this.sendParent = this.sendParent.bind(this);

        this.state = {
            selectedLocations: [] // Holds all the locations that are entered by the user
        };

        this.getEditInfo = this.getEditInfo.bind(this);
        this.mapRef = React.createRef();
    }

    getEditInfo(info){

        this.setState({
            edit: true,
            selectedLocations : info.locations.map((obj) => {return {name: obj[0], lng: obj[2], lat: obj[1]}})
        });

    }

    sendParent() {
        {/* Called when user clicks on plus button plcaed below the selected locations.
            It sends all of the entered locations to the parent component (Create Post)*/}
        this.props.parentHandler('locationChooser', this.state.selectedLocations.map((obj) => {
            return {name:obj['name'] ? obj['name']: "",longitude: obj['lng'], latitude: obj['lat'] };
        }))
    }


    setLocations = (locations) => {
        this.setState({
              selectedLocations : locations
          });
    }

    render(){
        let key = process.env.REACT_APP_GOOGLE_API_KEY?(`key=` + process.env.REACT_APP_GOOGLE_API_KEY + "&") : "";
        return(
            <div id={'locationchooser-div'}>
                <div>
            {this.state.edit && <MapComponent 
                editLocations = {this.state.selectedLocations.map((obj) => {return {name:obj['name'] ,lat: obj['lat'], lng: obj['lng']};})}
                setParentLocation = {this.setLocations}
                isMarkerShown
                googleMapURL={`https://maps.googleapis.com/maps/api/js?${key}v=3.exp&libraries=geometry,drawing,places`}
                loadingElement={<div style={{ height: `100%` }} />}
                containerElement={<div style={{ height: `200px` }} />}
                mapElement={<div style={{ height: `100%` }} />}
                />}
            {!this.state.edit && <MapComponent 
                setParentLocation = {this.setLocations}
                isMarkerShown
                googleMapURL={`https://maps.googleapis.com/maps/api/js?${key}v=3.exp&libraries=geometry,drawing,places`}
                loadingElement={<div style={{ height: `100%` }} />}
                containerElement={<div style={{ height: `200px` }} />}
                mapElement={<div style={{ height: `100%` }} />}
                />}
        </div>
                
                <a>Please use below if you want to add names to the locations.</a>
                <div style = {{height: "200px"}}class = "overflow">
                <ul>
                    {this.state.selectedLocations.map((item, index) => (
                        <li key={index}>{index + 1} 
                            <input value = {this.state.selectedLocations[index]['name']} type='text' onChange = {e => this.setState(
                                state => {
                                    let newState = state;
                                    newState.selectedLocations[index]['name'] = e.target.value;
                                    return newState;
                                    
                                }
                            )}></input>
                        </li>
                    ))}
                 </ul>
                 </div>
            </div>
        );
    }

}

export default LocationChooser;
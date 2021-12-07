import React from 'react'
import './LocationChooser.css'
import Icon from '@mdi/react'
import { mdiPlus } from '@mdi/js';
import { mdiArrowDown } from '@mdi/js';


import { withScriptjs, withGoogleMap, GoogleMap, Marker } from "react-google-maps";

const MapComponent = withScriptjs(withGoogleMap((props) =>{
    const [markers, setMarkers] = React.useState([]);

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
        setMarkers(state => {
            const newState =  [...state, {lat: e.latLng.lat(), lng : e.latLng.lng()}]; 
            props.setParentLocation(newState);
            return newState;
        });

        
    }
return(<GoogleMap
        
        defaultZoom={8}
        defaultCenter={{ lat: 41, lng: 28 }}
   onClick = {addMarker}>
        {markers.map((obj,i) => {
            return (<Marker options={{icon:`https://mt.google.com/vt/icon/text=${i}&psize=16&font=fonts/arialuni_t.ttf&color=ff330000&name=icons/spotlight/spotlight-waypoint-b.png&ax=44&ay=48&scale=1`}} onClick = {() => deleteMarker(i)}position = {obj} key = {i}/>);
        })}
    </GoogleMap>);
  }
))



class LocationChooser extends React.Component{
    constructor(props){
        super(props);

        this.parentHandler = props.parentHandler;
        this.sendParent = this.sendParent.bind(this);

        this.state = {
            selectedLocations: [] // Holds all the locations that are entered by the user
        };

        this.getEditInfo = this.getEditInfo.bind(this);
    }

    getEditInfo(info){
        this.setState({
            selectedLocations : info.locations
        });
    }

    sendParent() {
        {/* Called when user clicks on plus button plcaed below the selected locations.
            It sends all of the entered locations to the parent component (Create Post)*/}
        this.props.parentHandler('locationChooser', this.state.selectedLocations)
    }


    setLocations = (locations) => {
        this.setState({
              selectedLocations : locations
          });
    }

    render(){
        return(
            <div id={'locationchooser-div'}>
                <div>
            <MapComponent 
                setParentLocation = {this.setLocations}
                isMarkerShown
                googleMapURL="https://maps.googleapis.com/maps/api/js?v=3.exp&libraries=geometry,drawing,places"
                loadingElement={<div style={{ height: `100%` }} />}
                containerElement={<div style={{ height: `200px` }} />}
                mapElement={<div style={{ height: `100%` }} />}
                />
        </div>
                
                <a>Please use below if you want to add names to the locations.</a>
                <div style = {{height: "200px"}}class = "overflow">
                <ul>
                    {this.state.selectedLocations.map((item, index) => (
                        <li key={index}>{index} 
                            <input type='text' onChange = {e => this.setState(
                                state => {
                                    let newState = state;
                                    newState.selectedLocations[index]['name'] = e.target.value;
                                    console.log(newState);
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
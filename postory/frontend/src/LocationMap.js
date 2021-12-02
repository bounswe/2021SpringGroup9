import React from 'react'
import './LocationChooser.css'
import Icon from '@mdi/react'
import { mdiPlus } from '@mdi/js';


import { withScriptjs, withGoogleMap, GoogleMap, Marker } from "react-google-maps";

const MyMapComponent = withScriptjs(withGoogleMap((props) =>{
    const [markers, setMarkers] = React.useState([]);

    const deleteMarker = (index) =>{
        setMarkers(state =>{
            let newState = [];
            for(let i =0 ; i< state.length; i++)
                if(i != index)
                    newState.push(state[i]);
            props.setParentLocation(newState.map(obj => JSON.stringify(obj)));
            return newState;
        });

        
    }

    const addMarker = (e) =>{
        setMarkers(state => {
            const newState =  [...state, {lat: e.latLng.lat(), lng : e.latLng.lng()}]; 
            props.setParentLocation(newState.map(obj => JSON.stringify(obj)));
            return newState;
        });

        
    }
return(<GoogleMap
        defaultZoom={8}
        defaultCenter={{ lat: -34.397, lng: 150.644 }}
   onClick = {addMarker}>
        {markers.map((obj,i) => {
            return (<Marker onClick = {() => deleteMarker(i)}position = {obj} key = {i}/>);
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
            value: '', // Holds the last entered location as an input
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

    clearAllSelectedLocations = () => {
        {/* Called when user clicks on clear all button to clear all the locations that added to the post before*/}
        this.setState({ selectedLocations: [] });
    };

    removeLocation= i => {
        {/* Called when user clicks on x button next to each location added previously to the post.
            It filters the location that wanted to be removed from selectedLocation array returns it */}
        this.setState(state => {
          const selectedLocations = state.selectedLocations.filter((item, j) => i !== j);

          return {
            selectedLocations,
          };
        });
      };

    onChangeValue = event => {
        {/* Called when when there is change in the input box allows user to enter location*/}
        this.setState({ value: event.target.value });
      };

    
    addTagToSelectedLocations = () => {
        {/* Called when user clicks on add button to add the last entered location (state.value) the post
            It then contats the previous selectedLocations list with the last entered location and returns */}
        this.setState(state => {
          const selectedLocations = state.selectedLocations.concat(state.value);

          return {
            selectedLocations,
            value: '',
          };
        });
      };


    setLocations = (locations) => {
        this.setState({
  
              selectedLocations : locations,
              value: '',

          });
    }

    render(){
        return(
            <div id={'locationchooser-div'}>
                <div>
            <MyMapComponent 
                setParentLocation = {this.setLocations}
                isMarkerShown
                googleMapURL="https://maps.googleapis.com/maps/api/js?v=3.exp&libraries=geometry,drawing,places"
                loadingElement={<div style={{ height: `100%` }} />}
                containerElement={<div style={{ height: `200px` }} />}
                mapElement={<div style={{ height: `100%` }} />}
                />
        </div>
                <label htmlFor={'locationchooser-title'} id={'locationchooser-title-label'}>Location</label>
                <div class= "row">
                <input
                    type="text"
                    value={this.state.value}
                    onChange={this.onChangeValue}
                />
                <button
                    type="button"
                    onClick={this.addTagToSelectedLocations}
                    disabled={!this.state.value}
                >
                Add
                </button>
                <button 
                    type="button" 
                    onClick={this.clearAllSelectedLocations}
                >
                Clear All 
                </button>       
                </div>
                Selected Locations
                <ul>
                    {this.state.selectedLocations.map((item, index) => (
                        <li key={item}>{item}
                            <button
                                type="button"
                                onClick={() => this.removeLocation(index)}
                            >
                                x
                            </button>
                        </li>
                    ))}
                 </ul>
                 {/*
                 <button id={'locationchooser-plus-button'} onClick={this.sendParent}>
                    <Icon path={mdiPlus} size={1} id={'locationchooser-plus-icon'}/>
                </button>   
                 */}
            </div>
        );
    }

}

export default LocationChooser;
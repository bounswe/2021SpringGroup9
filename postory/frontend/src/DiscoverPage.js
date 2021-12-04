import React, { useEffect } from "react";
import { withScriptjs, withGoogleMap, GoogleMap, Marker } from "react-google-maps";


const MyMapComponent = withScriptjs(withGoogleMap((props) =>{
    const [markers, setMarkers] = React.useState([]);
    const [posts, setPosts] = React.useState([]);



    useEffect(() => {
        fetch('http://3.125.114.231:8000/api/post/all')
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

    return( 
            <GoogleMap
                defaultZoom={6}
                defaultCenter={{ lat: 41.048, lng: 29.0510 }}
                >
                {markers.map((obj,i) => {
                    return (<Marker position = {{lat:obj.lat, lng:obj.lng}} key = {i}/>);
                })}
            </GoogleMap>
            );
  }
))

class DiscoverPage extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        return(         
                <MyMapComponent 
                    isMarkerShown
                    googleMapURL="https://maps.googleapis.com/maps/api/js?AIzaSyCObbHDNSykqMsThft-aQljY99z9RErUsI&v=3.exp&libraries=geometry,drawing,places"
                    loadingElement={<div style={{ height: `100%` }} />}
                    containerElement={<div style={{ height: window.innerHeight }} />} //`600px`
                    mapElement={<div style={{ height: `100%` }} />}
                />
        )
    }
}
export default DiscoverPage;
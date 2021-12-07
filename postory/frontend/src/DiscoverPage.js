import React, { useEffect } from "react";
import { withScriptjs, withGoogleMap, GoogleMap, Marker} from "react-google-maps";
import InfoBox from "react-google-maps/lib/components/addons/InfoBox";
import Icon from '@mdi/react'
import { mdiGestureTap } from '@mdi/js';
import { Link } from "react-router-dom";


const MyMapComponent = withScriptjs(withGoogleMap((props) =>{
    const [markers, setMarkers] = React.useState([]);
    const [posts, setPosts] = React.useState([]);
    const [selectedPost, setSelectedPost] = React.useState(null);
    const [currentLocation, setCurrentLocation] = React.useState({ lat: 41.048, lng: 29.0510 });
    const [displayPost, setDisplayPost] = React.useState(false);
    const [displayInfoBox, setDisplayInfoBox] = React.useState(false);


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

    useEffect(() => {
        if (selectedPost != null){
            setDisplayInfoBox(false);
            setTimeout(() => {setDisplayInfoBox(true)}, 500);
        }
        console.log("Selected post has been changed");
    }, [selectedPost])

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
            return newPost;
        });       
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
                            by: {selectedPost.owner}
                        </div>
                        <div class= "row">
                            <Link class = "push" to= {`/viewPost?id=${selectedPost.id}`}>
                                <Icon 
                                    path={mdiGestureTap} 
                                    size={1}
                                />
                                <div style={{ fontSize: `10px`, fontColor: `#C40303` }}>
                                    click here to see the full post
                                </div>
                            </Link>
                        </div>
                    </div>
                </InfoBox>
                } 
                {markers.map((obj,i) => {
                    return (<Marker onClick = {() => onClickMarker(i, obj)} position = {{lat:obj.lat, lng:obj.lng}} key = {i}/>);
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
        <div className="App">
        <header className="App-header">
          <MyMapComponent 
                    isMarkerShown
                    googleMapURL="https://maps.googleapis.com/maps/api/js?AIzaSyCObbHDNSykqMsThft-aQljY99z9RErUsI&v=3.exp&libraries=geometry,drawing,places"
                    loadingElement={<div style={{ height: `100%` }} />}
                    containerElement={<div style={{ height: window.innerHeight * 5/6, width: window.innerWidth }} />} //`600px`
                    mapElement={<div style={{ height: `100%` }} />}
                />
        </header>
      </div>      
        )
    }
}
export default DiscoverPage;
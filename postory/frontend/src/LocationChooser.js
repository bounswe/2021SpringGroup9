import React from 'react'
import './LocationChooser.css'
import Icon from '@mdi/react'
import { mdiPlus } from '@mdi/js';

class LocationChooser extends React.Component{
    constructor(props){
        super(props);

        this.parentHandler = props.parentHandler;
        this.sendParent = this.sendParent.bind(this);

        this.state = {
            value: '', // Holds the last entered location as an input
            selectedLocations: [] // Holds all the locations that are entered by the user
        };

    }

    sendParent() {
        this.props.parentHandler('locationChooser', this.state)
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

    render(){
        return(
            <div>
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
                 <button  onClick={this.sendParent}>
                    <Icon path={mdiPlus} size={1} />
                </button>   
            </div>
        );
    }

}

export default LocationChooser;
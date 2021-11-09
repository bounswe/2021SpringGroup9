import React from 'react'
import './LocationChooser.css'

class LocationChooser extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            value: '',
            selectedLocations: [] 
        };

    }

    onChangeValue = event => {
        this.setState({ value: event.target.value });
      };

    addTagToSelectedLocations = () => {
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
                </div>
                Selected Locations
                <ul>
                    {this.state.selectedLocations.map(item => (
                        <li key={item}>{item}</li>
                    ))}
                 </ul>  
            </div>
        );
    }

}

export default LocationChooser;
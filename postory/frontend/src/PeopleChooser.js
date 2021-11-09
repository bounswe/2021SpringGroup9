import React from 'react'
import './PeopleChooser.css'

class PeopleChooser extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            value: '',
            selectedPeople: [] 
        };

    }

    onChangeValue = event => {
        this.setState({ value: event.target.value });
      };

    clearAllSelectedPeople = () => {
        this.setState({ selectedPeople: [] });
    };

    removePeople= i => {
        this.setState(state => {
          const selectedPeople = state.selectedPeople.filter((item, j) => i !== j);

          return {
            selectedPeople,
          };
        });
      };

    addTagToSelectedPeople = () => {
        this.setState(state => {
          const selectedPeople = state.selectedPeople.concat(state.value);

          return {
            selectedPeople,
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
                    onClick={this.addTagToSelectedPeople}
                    disabled={!this.state.value}
                >
                Add
                </button>
                <button 
                    type="button" 
                    onClick={this.clearAllSelectedPeople}
                >
                Clear All 
                </button>    
                </div>
                Selected People
                <ul>
                    {this.state.selectedPeople.map((item, index) => (
                        <li key={item}>{item}
                            <button
                                type="button"
                                onClick={() => this.removePeople(index)}
                            >
                                x
                            </button>
                        </li>
                    ))}
                </ul>  
            </div>
        );
    }

}

export default PeopleChooser;
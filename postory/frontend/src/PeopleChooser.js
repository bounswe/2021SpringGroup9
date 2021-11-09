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
                </div>
                Selected People
                <ul>
                    {this.state.selectedPeople.map(item => (
                        <li key={item}>{item}</li>
                    ))}
                 </ul>  
            </div>
        );
    }

}

export default PeopleChooser;
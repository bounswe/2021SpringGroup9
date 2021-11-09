import React from 'react'
import './PeopleChooser.css'
import Icon from '@mdi/react'
import { mdiPlus } from '@mdi/js';

class PeopleChooser extends React.Component{
    constructor(props){
        super(props);

        this.parentHandler = props.parentHandler;
        this.sendParent = this.sendParent.bind(this);

        this.state = {
            value: '',
            selectedPeople: [] 
        };

    }

    sendParent() {
        this.props.parentHandler('peopleChooser', this.state)
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
            <div id={'peoplechooser-div'}>
                <label htmlFor={'peoplechooser-title'} id={'peoplechooser-title-label'}>People</label>
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
                <button id={'peoplechooser-plus-button'} onClick={this.sendParent}>
                    <Icon path={mdiPlus} size={1} id={'peoplechooser-plus-icon'}/>
                </button>  
            </div>
        );
    }

}

export default PeopleChooser;
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
            value: '', // Holds the last entered tag as an input
            selectedPeople: [] // Holds all the tags that are entered by the user
        };

    }

    sendParent() {
        {/* Called when user clicks on the plus button placed below the selected people.
            It sends all of the entered people to the parent component (Create Post)*/}
        this.props.parentHandler('peopleChooser', this.state)
    }

    onChangeValue = event => {
        {/* Called when when there is change in the input box allows user to enter a person*/}
        this.setState({ value: event.target.value });
      };

    clearAllSelectedPeople = () => {
        {/* Called when user clicks on clear all button to clear all the people that added to the post before*/}
        this.setState({ selectedPeople: [] });
    };

    removePeople= i => {
        {/* Called when user clicks on x button next to each person added previously to the post.
            It filters the person that wanted to be removed from selectedPeople array returns it */}
        this.setState(state => {
          const selectedPeople = state.selectedPeople.filter((item, j) => i !== j);

          return {
            selectedPeople,
          };
        });
      };

    addTagToSelectedPeople = () => {
        {/* Called when user clicks on add button to add the last entered person (state.value) to the post.
            It then concats the previous selectedPeople list with the last entered person and returns */}
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
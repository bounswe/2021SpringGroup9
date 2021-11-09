import React from 'react'
import Icon from '@mdi/react'
import { mdiPlus } from '@mdi/js';
import './TagChooser.css'

class TagChooser extends React.Component{
    constructor(props){
        super(props);
        
        this.state = {
            value: '',
            selectedTags: [] 
        };

    }

    onChangeValue = event => {
        this.setState({ value: event.target.value });
      };

    addTagToSelectedTags = () => {
        this.setState(state => {
          const selectedTags = state.selectedTags.concat(state.value);
     
          return {
            selectedTags,
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
                    onClick={this.addTagToSelectedTags}
                    disabled={!this.state.value}
                >
                Add
                </button>     
                </div>
                Selected Tags
                <ul>
                    {this.state.selectedTags.map(item => (
                        <li key={item}>{item}</li>
                    ))}
                 </ul>  
            </div>
        );
    }
}

export default TagChooser;
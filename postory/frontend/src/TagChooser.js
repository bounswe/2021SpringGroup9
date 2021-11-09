import React from 'react'
import Icon from '@mdi/react'
import { mdiPlus } from '@mdi/js';
import './TagChooser.css'

class TagChooser extends React.Component{
    constructor(props){
        super(props);

        this.parentHandler = props.parentHandler;
        this.sendParent = this.sendParent.bind(this);
        
        this.state = {
            value: '',
            selectedTags: [] 
        };

    }

    sendParent() {
        this.props.parentHandler('tagChooser', this.state)
    }

    onChangeValue = event => {
        this.setState({ value: event.target.value });
      };

    clearAllSelectedTags = () => {
        this.setState({ selectedTags: [] });
    };

    removeTag = i => {
        this.setState(state => {
          const selectedTags = state.selectedTags.filter((item, j) => i !== j);
     
          return {
            selectedTags,
          };
        });
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
            <div id={'tagchooser-div'}>
                <label htmlFor={'tagchooser-title'} id={'textchooser-title-label'}>#Tags</label>
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
                <button 
                    type="button" 
                    onClick={this.clearAllSelectedTags}
                >
                Clear All
                </button>     
                </div>
                Selected Tags
                <ul>
                    {this.state.selectedTags.map((item, index) => (
                        <li key={item}>{item}
                            <button
                                type="button"
                                onClick={() => this.removeTag(index)}
                            >
                                x
                            </button>
                        </li>
                    ))}
                 </ul>
                 <button id={'tagchooser-plus-button'} onClick={this.sendParent}>
                    <Icon path={mdiPlus} size={1} id={'tagchooser-plus-icon'}/>
                </button>  
            </div>
        );
    }
}

export default TagChooser;
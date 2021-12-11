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
            value: '', // Holds the last entered tag as an input
            selectedTags: [] // Holds all the tags that are entered by the user
        };

        this.getEditInfo = this.getEditInfo.bind(this);
    }

    getEditInfo(info){
        this.setState({
            selectedTags : info.tags
        });
    }

    sendParent() {
        {/* Called when user clicks on the plus button placed below the selected tags.
            It sends all of the entered tags to the parent component (Create Post)*/}
        this.props.parentHandler('tagChooser', this.state)
    }

    onChangeValue = event => {
        {/* Called when when there is change in the input box allows user to enter tag*/}
        this.setState({ value: event.target.value });
      };

    clearAllSelectedTags = () => {
        {/* Called when user clicks on clear all button to clear all the tags that added to the post before*/}
        this.setState({ selectedTags: [] });
    };

    removeTag = i => {
        {/* Called when user clicks on x button next to each tag added previously to the post.
            It filters the tag that wanted to be removed from selectedTags array returns it */}
        this.setState(state => {
          const selectedTags = state.selectedTags.filter((item, j) => i !== j);
     
          return {
            selectedTags,
          };
        });
      };

    addTagToSelectedTags = () => {
        {/* Called when user clicks on add button to add the last entered location (state.value) the post
            It then contats the previous selectedLocations list with the last entered location and returns */}
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
                <div class= "row2">
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
                 {/*
                 <button id={'tagchooser-plus-button'} onClick={this.sendParent}>
                    <Icon path={mdiPlus} size={1} id={'tagchooser-plus-icon'}/>
                </button>  
                 */}
            </div>
        );
    }
}

export default TagChooser;
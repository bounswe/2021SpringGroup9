import React from 'react'
import Icon from '@mdi/react'
import { mdiPlus } from '@mdi/js';
import './TextChooser.css'

/**
 * @class TextChooser
 * Lets user enter title and body of their story.
 * Sends parent the entered info whenever needed.
 */
class TextChooser extends React.Component {
    constructor(props) {
        super(props);
        this.props = props;
        this.parentHandler = props.parentHandler;
        this.sendParent = this.sendParent.bind(this);
        this.state = {
            title: '',
            body: '',
        }

        this.getEditInfo = this.getEditInfo.bind(this);
    }

    getEditInfo(info){
        this.setState({
            title: info.title,
            body: info.story
        });
    }

    sendParent() {
        this.props.parentHandler('textChooser', this.state)
    }

    render() {
        return (
            <div id={'textchooser-div'}>
                <label htmlFor={'textchooser-title'} id={'textchooser-title-label'}>Post title</label>
                <input value = {this.state.title} type={'text'} id={'textchooser-title'} onChange={
                    (e) => {
                        this.setState(state => ({title: e.target.value, body: state.body}))
                    }
                }/>
                <label htmlFor={'textchooser-body'} id={'textchooser-body-label'}>Post body</label>
                <textarea value = {this.state.body} id={'textchooser-body'} onChange={
                    (e) => {
                        this.setState(state => ({title: state.title, body: e.target.value}))
                    }
                }/>
                {/*
                <button id={'textchooser-plus-button'} onClick={this.sendParent}>
                    <Icon path={mdiPlus} size={1} id={'textchooser-plus-icon'}/>
                </button>
                */}
            </div>
        )
    }
}

export default TextChooser;
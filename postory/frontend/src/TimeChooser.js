import React from 'react'
import Icon from '@mdi/react'
import { mdiPlus } from '@mdi/js';
import './TimeChooser.css'

class TimeChooser extends React.Component {
    constructor(props) {
        super(props);
        this.props = props;
        this.parentHandler = props.parentHandler;
        this.sendParent = this.sendParent.bind(this);
        this.state = {
            startDate: null,
            startTime: null,
            endDate: null,
            endTime: null,
            checked: false
        }
    }

    sendParent() {
        let result;
        if (this.state.checked) {
            result = {
                date: this.state.startDate,
                time: this.state.startTime
            }
        } else {
            result = {
                startDate: this.state.startDate,
                startTime: this.state.startTime,
                endDate: this.state.endDate,
                endTime: this.state.endTime,
            }
        }
        this.props.parentHandler('timeChooser', result)
    }

    render() {
        return (
            <div id={'timechooser-div'}>
                <div style={{display: 'flex', flexDirection: 'row', gap: '10px'}}>
                    <div style={{display: 'flex', flexDirection: 'column'}}>
                        <label htmlFor={'timechooser-s-date'} id={'timechooser-s-date-label'}>
                            {this.state.checked ? 'Post date' : 'Starting date'}
                        </label>
                        <input type={'date'} id={'timechooser-s-date'} onChange={
                            (e) => {
                                this.setState(state => ({...state, startDate: e.target.value}))
                            }
                        }/>
                    </div>
                    <div style={{display: 'flex', flexDirection: 'column'}}>
                        <label htmlFor={'timechooser-s-time'} id={'timechooser-s-time-label'}>
                            {this.state.checked ? 'Post time' : 'Starting time'}
                        </label>
                        <input type={'time'} id={'timechooser-s-time'} onChange={
                            (e) => {
                                this.setState(state => ({...state, startTime: e.target.value}))
                            }
                        }/>
                    </div>
                    <input id={'timechooser-checkbox'} type={'checkbox'} onChange={
                        (e) => {
                            this.setState(state => ({...state, checked: e.target.checked}))
                        }
                    } />
                    <label id={'timechooser-checkbox-text'} htmlFor={'timechooser-checkbox'} >Use single time</label>
                </div>

                {!this.state.checked &&<div style={{display: 'flex', flexDirection: 'row', gap: '10px'}}>
                    <div style={{display: 'flex', flexDirection: 'column'}}>
                        <label htmlFor={'timechooser-e-date'} id={'timechooser-e-date-label'}>Ending date</label>
                        <input type={'date'} id={'timechooser-e-date'} onChange={
                            (e) => {
                                this.setState(state => ({...state, endDate: e.target.value}))
                            }
                        }/>
                    </div>
                    <div style={{display: 'flex', flexDirection: 'column'}}>
                        <label htmlFor={'timechooser-e-time'} id={'timechooser-e-time-label'}>Ending time</label>
                        <input type={'time'} id={'timechooser-e-time'} onChange={
                            (e) => {
                                this.setState(state => ({...state, endTime: e.target.value}))
                            }
                        }/>
                    </div>
                </div>}
                {/*
                <button id={'timechooser-plus-button'} onClick={this.sendParent}>
                    <Icon path={mdiPlus} size={1} id={'timechooser-plus-icon'}/>
                </button>
                */}
            </div>
        )
    }
}

export default TimeChooser;
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
<<<<<<< HEAD
            startYear: null,
            endYear: null,
            startMonth: null,
            endMonth: null,
            startDay: null,
            endDay: null,
            startTime: null,
            endTime: null,
=======
            startDate: null,
            startTime: null,
            endDate: null,
            endTime: null,
            checked: true
>>>>>>> master
        }

        this.getEditInfo = this.getEditInfo.bind(this);
    }

    getEditInfo(info){
<<<<<<< HEAD
        const [startYear, endYear] = info.year || [null, null];
        const [startMonth, endMonth] = info.month || [null, null];
        const [startDay, endDay] = info.day || [null, null];
        const [startHour, endHour] = info.hour || [null, null];
        const [startMinute, endMinute] = info.minute || [null, null];
        const startTime = startHour && startMinute && `${startHour}:${startMinute}`
        const endTime = endHour && endMinute && `${endHour}:${endMinute}`
        this.setState(state => ({startYear, endYear, startMonth, endMonth, startDay, endDay, startTime, endTime}));
    }

    sendParent() {
        this.props.parentHandler('timeChooser', this.state)
=======
        this.setState({
            startDate : info.storyDate.slice(0,10),
            startTime : '00:00'
        });
    }

    sendParent() {
        let result;
        if (this.state.checked) {
            result = {
                startDate: this.state.startDate,
                startTime: this.state.startTime
                // date: this.state.startDate,
                // time: this.state.startTime
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
>>>>>>> master
    }

    render() {
        return (
            <div id={'timechooser-div'}>
<<<<<<< HEAD
                <div>Choose start and end years</div>
                <div style={{display: 'flex', flexDirection: 'row', gap: '20px'}}>
                    <input type="number" min="1900" max="2099" step="1" value={this.state.startYear || ""} onChange={
                        e => this.setState(state => ({...state, startYear: e.target.value.toString()}))
                    }/>
                    <input type="number" min="1900" max="2099" step="1" value={this.state.endYear || ""}  onChange={
                        e => this.setState(state => ({...state, endYear: e.target.value.toString()}))
                    }/>
                    <button onClick={() => {
                        this.setState(state => ({...state, startYear: null, endYear: null, startMonth: null, endMonth: null, startDay: null, endDay: null, startTime: null, endTime: null}))
                    }}>
                        Clear
                    </button>
                </div>
                { this.state.startYear && this.state.endYear && <>
                <div>Choose start and end months</div>
                <div style={{display: 'flex', flexDirection: 'row', gap: '20px'}}>
                    <input type="number" min="1" max="12" step="1" value={this.state.startMonth || ""} onChange={
                        e => this.setState(state => ({...state, startMonth: e.target.value.toString()}))
                    }/>
                    <input type="number" min="1" max="12" step="1" value={this.state.endMonth || ""}  onChange={
                        e => this.setState(state => ({...state, endMonth: e.target.value.toString()}))
                    }/>
                    <button onClick={() => {
                        this.setState(state => ({...state, startMonth: null, endMonth: null, startDay: null, endDay: null, startTime: null, endTime: null}))
                    }}>
                        Clear
                    </button>
                </div>
                </>}
                { this.state.startMonth && this.state.endMonth && <>
                <div>Choose start and end days</div>
                <div style={{display: 'flex', flexDirection: 'row', gap: '20px'}}>
                    <input type="number" min="1" max="31" step="1" value={this.state.startDay || ""} onChange={
                        e => this.setState(state => ({...state, startDay: e.target.value.toString()}))
                    }/>
                    <input type="number" min="1" max="31" step="1" value={this.state.endDay || ""}  onChange={
                        e => this.setState(state => ({...state, endDay: e.target.value.toString()}))
                    }/>
                    <button onClick={() => {
                        this.setState(state => ({...state, startDay: null, endDay: null, startTime: null, endTime: null}))
                    }}>
                        Clear
                    </button>
                </div>
                </>}
                { this.state.startDay && this.state.endDay && <>
                <div>Choose start and end time</div>
                <div style={{display: 'flex', flexDirection: 'row', gap: '20px'}}>
                    <input type="time" value={this.state.startTime || ""} onChange={
                        e => this.setState(state => ({...state, startTime: e.target.value.toString()}))
                    }/>
                    <input type="time" value={this.state.endTime || ""}  onChange={
                        e => this.setState(state => ({...state, endTime: e.target.value.toString()}))
                    }/>
                    <button onClick={() => {
                        this.setState(state => ({...state, startTime: null, endTime: null}))
                    }}>
                        Clear
                    </button>
                </div>
                </>}
=======
                <div style={{display: 'flex', flexDirection: 'row', gap: '10px'}}>
                    <div style={{display: 'flex', flexDirection: 'column'}}>
                        <label htmlFor={'timechooser-s-date'} id={'timechooser-s-date-label'}>
                            {this.state.checked ? 'Post date' : 'Starting date'}
                        </label>
                        <input value = {this.state.startDate} type={'date'} id={'timechooser-s-date'} onChange={
                            (e) => {
                                this.setState(state => ({...state, startDate: e.target.value}))
                            }
                        }/>
                    </div>
                    <div style={{display: 'flex', flexDirection: 'column'}}>
                        <label htmlFor={'timechooser-s-time'} id={'timechooser-s-time-label'}>
                            {this.state.checked ? 'Post time' : 'Starting time'}
                        </label>
                        <input value = {this.state.startTime} type={'time'} id={'timechooser-s-time'} onChange={
                            (e) => {
                                this.setState(state => ({...state, startTime: e.target.value}))
                            }
                        }/>
                    </div>
                    {/*<input id={'timechooser-checkbox'} type={'checkbox'} onChange={*/}
                    {/*    (e) => {*/}
                    {/*        this.setState(state => ({...state, checked: e.target.checked}))*/}
                    {/*    }*/}
                    {/*} />*/}
                    {/*<label id={'timechooser-checkbox-text'} htmlFor={'timechooser-checkbox'} >Use single time</label>*/}
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
>>>>>>> master
            </div>
        )
    }
}

export default TimeChooser;
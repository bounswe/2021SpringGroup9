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
            startYear: null,
            endYear: null,
            startMonth: null,
            endMonth: null,
            startDay: null,
            endDay: null,
            startTime: null,
            endTime: null,
        }

        this.getEditInfo = this.getEditInfo.bind(this);
    }

    getEditInfo(info){
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
    }

    render() {
        return (
            <div id={'timechooser-div'}>
                <div>Choose start and end years</div>
                <div style={{display: 'flex', flexDirection: 'row', gap: '20px'}}>
                    <input type="number" min="1900" max="2099" step="1" value={this.state.startYear || ""} onChange={
                        e => this.setState(state => ({...state, startYear: e.target.value.toString()}))
                    } alt="Starting year"/>
                    <input type="number" min="1900" max="2099" step="1" value={this.state.endYear || ""}  onChange={
                        e => this.setState(state => ({...state, endYear: e.target.value.toString()}))
                    } alt="Ending year"/>
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
                    } alt="Starting month"/>
                    <input type="number" min="1" max="12" step="1" value={this.state.endMonth || ""}  onChange={
                        e => this.setState(state => ({...state, endMonth: e.target.value.toString()}))
                    } alt="Ending month"/>
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
                    } alt="Starting day"/>
                    <input type="number" min="1" max="31" step="1" value={this.state.endDay || ""}  onChange={
                        e => this.setState(state => ({...state, endDay: e.target.value.toString()}))
                    } alt="Ending day"/>
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
                    } alt="Starting time"/>
                    <input type="time" value={this.state.endTime || ""}  onChange={
                        e => this.setState(state => ({...state, endTime: e.target.value.toString()}))
                    } alt="Ending time"/>
                    <button onClick={() => {
                        this.setState(state => ({...state, startTime: null, endTime: null}))
                    }}>
                        Clear
                    </button>
                </div>
                </>}
            </div>
        )
    }
}

export default TimeChooser;
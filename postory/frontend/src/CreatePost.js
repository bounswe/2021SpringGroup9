import React from 'react';

class CreatePost extends React.Component{
    constructor(props){
        super(props);
        
        this.state = {
            selected : "none",
            postData: {}
        };

        this.handleChildObjectSend = this.handleChildObjectSend.bind(this);
        this.select = this.select.bind(this);
        this.sendToBackend = this.sendToBackend.bind(this);
    }

    handleChildObjectSend(whichComponent, childObj){
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj.postData[whichComponent] = childObj;

            console.log(newObj);

            return newObj;
        });

        
    }

    select(obj){
        console.log(obj);
        this.setState(state => {
            let newObj =  JSON.parse(JSON.stringify(state));
            newObj['selected'] = obj;
            return newObj;
        });
    }

    sendToBackend(){
        // send this.state.postData to backend
        console.log(JSON.stringify(this.state.postData) + 'failed');
    }


    render(){

        return(
            <header className="App-header">
            <div class = "row someSpacing">
                <div class = "inputArea">
                    {this.state['selected'] == 'Title' && <TestComponent parentHandler = {this.handleChildObjectSend}>Text</TestComponent >}
                    {this.state['selected'] == 'Story' && <TestComponent parentHandler = {this.handleChildObjectSend}>Text</TestComponent >}
                    {this.state['selected'] == 'Location' && <TestComponent parentHandler = {this.handleChildObjectSend}>Text</TestComponent >}
                    {this.state['selected'] == 'Time' && <TestComponent parentHandler = {this.handleChildObjectSend}>Text</TestComponent >}
                    {this.state['selected'] == 'People' && <TestComponent parentHandler = {this.handleChildObjectSend}>Text</TestComponent >}
                    {this.state['selected'] == 'Tags' && <TestComponent parentHandler = {this.handleChildObjectSend}>Text</TestComponent >}
                </div>
                <div class = "buttons col">
                    <button class = "createPostBtn" onClick = {() => {this.select('Title')}}>Title</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Story')}}>Story</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Location')}}>Location</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Time')}}>Time</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('People')}}>People</button>
                    <button class = "createPostBtn" onClick = {() => {this.select('Tags')}}>Tags</button>
                    <button class = "createPostBtn" onClick = { this.sendToBackend}> Send</button>
                </div>
            </div>
            </header>
        );
    }

}


class TestComponent extends React.Component{
    constructor(props){
        super(props);
        this.testSend = this.testSend.bind(this);
    }

    testSend(e){
        this.props.parentHandler('testComponent', e.target.value);
    }

    render(){
        return(
            <div>
                <input onChange = {this.testSend}></input>
            </div>
        );
    }
}

export default CreatePost;
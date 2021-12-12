
import React from "react";
import { Link, Navigate} from "react-router-dom";
class Redirector extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            hasJWT: localStorage.getItem("access")
        }
    }

    render(){

        return (<div>{!this.state.hasJWT && <Navigate class = "push" to= {`/signIn`}> </Navigate>}</div>);
    }
}

export default Redirector;
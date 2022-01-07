
import React from "react";
import { Link, Navigate} from "react-router-dom";
import * as requests from './requests'


class Redirector extends React.Component{
    /**
     * A component that is used to check if a user is properly logged in.
     * If the user is not logged in they are redirected to the login page.
     * @param {*} props 
     */
    constructor(props){
        super(props);
        let error = false;
        this.state = {
            hasJWT: !error && localStorage.getItem("access")
        }
        // Redirect if not able to get all posts.
        requests.get_jwt('/api/post/all',{}).
        then(resp => 
            this.setState( {hasJWT: (resp.status==200) && localStorage.getItem("access")})
            ).catch(err => this.setState( {hasJWT: false}));
        
    }

    render(){

        return (<div>{!this.state.hasJWT && <Navigate class = "push" to= {`/signIn`}> </Navigate>}</div>);
    }
}

export default Redirector;
import React from 'react'
import './PeopleChooser.css'

class PeopleChooser extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            value: '',
            selectedPeople: [] 
        };

    }

}
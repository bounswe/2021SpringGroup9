import Dropdown from 'react-bootstrap/Dropdown';
import FormControl from 'react-bootstrap/FormControl';
import Row from 'react-bootstrap/Row';
import React, { useEffect } from 'react';
import * as requests from './requests';

const SearchUserComponent = (props) => {
    const [value, setValue] = React.useState('');
    const [users, setUsers] = React.useState([]);
    const [menu, setMenu] = React.useState(false);
    const [req, setReq] = React.useState(null);

    useEffect(() => {
        if(req != null){
            clearTimeout(req);
        }
        setReq(setTimeout(() => requests.post_jwt(`/api/user/search/${value}`,{}).then(resp=> 
            {
                console.log(resp.status)
            if(resp.status == 404)
            return []; 
            else 
            return resp.json()}).then( data =>setUsers(data)
        ), 500));
        
    },[value]);

    return(<div>
            
            <Dropdown.Menu 
            onFocus = {(e) => setMenu(true)}
                onBlur = {(e) => {setMenu(false); setUsers([]); setValue('')}} show class = 'Dropdown_style'>
                <FormControl
                className="mx-1 pt-0 w-auto"
                placeholder="Type to search users..."
                onChange={(e) => setValue(e.target.value)}
                
                value={value}
                />
                {menu && users && Array.isArray(users) && users.map((obj,i) => <Dropdown.Item >{obj.name}</Dropdown.Item>)}
            </Dropdown.Menu>
        
    </div>);
}

export default SearchUserComponent;
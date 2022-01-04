import React from 'react'
import Post  from './Post'

class FilteredPosts extends React.Component{
  constructor(props){
    super(props);
    this.state = {
        posts: JSON.parse(localStorage.getItem('filteredPosts'))
      };
  }

  render() {
    return (
      <div className="App">
        <header className="App-header">
            <p>
                POSTORY
            </p>
            {this.state.posts.map((obj, i) => {
                return <Post key = {i} {...obj}></Post>;
             })}

        </header>
        
      </div>
    );
  }

}

export default FilteredPosts;

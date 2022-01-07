import React from 'react'
import Post  from './Post'

class FilteredPosts extends React.Component{
  /* Component that is used to display the resulting posts from the filtering operation.
    It gets the posts from local storage and renders them using the Post component. */
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

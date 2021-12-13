import React from 'react'
import Icon from '@mdi/react'
import { mdiMapMarker, mdiClockTimeEight, mdiTag, mdiCardsHeartOutline, mdiCardsHeart, mdiShareVariant,  mdiPencilOutline} from '@mdi/js'
import PostButtons from './PostButtons';
import Badge from 'react-bootstrap/Badge'
import { Link } from "react-router-dom";
import Container from 'react-bootstrap/Container'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'


class Post extends React.Component{
    constructor(props){
      super(props);
      this.props = props;
    }
    render(){
      return (<div class= "Post" ><PostUpper {...this.props}/> <PostButtons/></div>);
    }
}

class PostUpper extends React.Component{
  constructor(props){
    super(props);

    this.state = {
      ...props,
      contentSmall : (props.story.length > 200) ? props.story.slice(0,200): props.story,
      continueReading: props.story.length > 200,
    };
    console.log(this.state);
    this.getMoreContent = this.getMoreContent.bind(this);
  }

  getMoreContent(){
    this.setState(state =>{
      //let newState = JSON.parse(JSON.stringify(state));
      let newState = {...state};
      newState.contentSmall = state.story;
      newState.continueReading = false;
      return newState;
    });
  }

  render(){
    //let style = {paddingBottom: "10px"};
    return (
    <Container>
      <Container>
      <Row style={{alignItems: `center`}}>
        <Col sm={11} style={{alignItems: `center`}}>
          {this.state.owner && <Link class = "push" to= {`/profilePage?id=${this.state.owner}`} style={{ textDecoration: 'none', color: '#000' }}>
            <img class = "circle" width = "50px" height = "50px" src = "./static/media/postory_logo_no_text.ec3bad21.png" />
            <a style = {{margin: "10px"}}>{this.state.owner}</a>
          </Link>}
        </Col>
        <Col sm={1}>
          {this.state.id && <Link class = "push" to= {`/editPost?id=${this.state.id}`}>
            <Icon path={mdiPencilOutline}
              title="Edit Post"
              size={1}
              color='#FF8F49'
              />
          </Link>}
        </Col> 
      </Row>
      </Container>
      <div class= "row2 fitText">
      
        {this.state.locations.filter(obj => typeof obj[0] === 'string' && obj[0] != '').map((obj,i) => {
          return(
            <Badge pill bg="secondary" style={{ fontSize: `10px`}}>
              <Icon path={mdiMapMarker}
                title="Location"
                size={0.7}
                color="#53BEC6"
              />
              {obj[0]}
             </Badge>);
        })}
        {this.state.year && 
        <Badge pill bg="warning" style={{ fontSize: `10px`}} text="dark">
          <Icon path={mdiClockTimeEight}
            title="Years"
            size={0.7}
            color="#53BEC6"
          />
          {this.state.year[0]} 
          {this.state.year.length > 1 && '-' + this.state.year[1]} 
        </Badge>}
        {this.state.tags.map((obj,i) => {
          return(
            <Badge pill bg="danger" style={{ fontSize: `10px`}} >
              <Icon path={mdiTag}
                title="Tag"
                size={0.7}
                color="#53BEC6"
              />
             {obj}
            </Badge>);
        })}
      </div>
      {this.state.id && <Link class = "push" to= {`/viewPost?id=${this.state.id}`} style={{ textDecoration: 'none', color: '#000' }}>
      <p style= {{width: '100%'}}class = "mainContent" ><b style={{ fontSize: `16px`, fontWeight: `bold`}}>{this.state.title}</b></p>
      </Link>}
      {!this.state.id && <p style= {{width: '100%'}}class = "mainContent" ><b style={{ fontSize: `16px`, fontWeight: `bold`}}>{this.state.title}</b></p>}
      <a style={{ fontSize: `14px`}} >{this.state.contentSmall}</a>
      {this.state.continueReading && <a class = "mainContent" onClick = {this.getMoreContent}><b> ...Continue Reading</b></a>}
      <p></p>
      <div> 
        {this.state.preview &&<div class = "row2 image">
          {this.state.preview.length > 0 && <img width = "200px" src = {URL.createObjectURL(this.state.preview[0])} ></img>}
          {this.state.preview.length > 1 && <img width = "200px" src = {URL.createObjectURL(this.state.preview[1])} ></img>}
        </div>}
        {!this.state.preview &&<div class = "row2 image">
          <img width = "200px" src = {(this.state.images && this.state.images[0]) ? this.state.images[0]: ""} ></img>
          <img width = "200px" src = {(this.state.images && this.state.images[0] && this.state.images[1]) ? this.state.images[1]: ""}></img>
        </div>}
      </div>
    </Container>);
  }
}

class PostLower extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      clicked: false
    };
    this.clickHeart = this.clickHeart.bind(this);
  }

  clickHeart(){
    this.setState(state => {
      return {clicked: !state.clicked};
    })
  }

  render(){
    return(
      <div class = "row2">
        {!this.state.clicked ?
        <Icon path={mdiCardsHeartOutline}
          title="Like"
          size={2}
          color="#53BEC6"
          onClick = {this.clickHeart}
          /> : 
          <Icon path={mdiCardsHeart}
          title="Like"
          size={2}
          color="#53BEC6"
          onClick = {this.clickHeart}
          /> }
        <Icon path={mdiShareVariant}
          title="Share"
          size={2}
          color="#53BEC6"
          />
      </div>
    )
  }
}



export default Post;
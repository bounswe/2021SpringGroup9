import React from 'react'
import { render } from 'react-dom'

const imagesPath = {
    disliked: "https://user-images.githubusercontent.com/35606355/140415129-e3f06692-9bf1-4796-878f-b59126102994.png",
    liked: "https://user-images.githubusercontent.com/35606355/140415133-6eea3c0e-1a3b-400b-b049-a47fa5805588.png",
    comment: "https://user-images.githubusercontent.com/35606355/140415650-a57c1153-ae17-44db-92be-2a74f7f6f1c1.png",
    share: "https://user-images.githubusercontent.com/35606355/140418385-09486b42-d1f6-4d8c-9edc-408f80fea3d1.png",
    save: "https://user-images.githubusercontent.com/35606355/140419635-2f3f4992-c95a-470c-baba-c54252f0de5c.png",
    report: "https://user-images.githubusercontent.com/35606355/140419056-b4f8af60-f0ed-48cb-83e2-0c18923cbe8a.png",
    verticalSeperator: "https://user-images.githubusercontent.com/35606355/140555305-60463bba-b681-4c1b-8c17-9b29e6126933.png",
  }
  
class LikeButton extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            liked: false,
            likeNumber: 0
        };
      }
//   state = {
//     liked: false,
//     likeNumber: 0
//   }
  toggleImage = () => {
    this.setState({ liked: !this.state.liked, likeNumber: this.state.liked ? 0 : 1 });
  }

  getImageName = () => this.state.liked ? 'liked' : 'disliked'

  render() {
    const imageName = this.getImageName();
    return (
      <div class= "row">
        <img style={{maxWidth: '50px'}} src={imagesPath[imageName]} onClick={this.toggleImage} />
        <div>
          {this.state.likeNumber}
        </div>
      </div>
    );
  }
}
  
class CommentButton extends React.Component {
  
    render() {
      return (
        <div>
          <img style={{maxWidth: '50px'}} src={imagesPath["comment"]} />
        </div>
      );
    }
}

class ShareButton extends React.Component {
  
    render() {
      return (
        <div>
          <img style={{maxWidth: '50px'}} src={imagesPath["share"]} />
        </div>
      );
    }
}

class SaveButton extends React.Component {
  
    render() {
      return (
        <div>
          <img style={{maxWidth: '50px'}} src={imagesPath["save"]} />
        </div>
      );
    }
}

class ReportButton extends React.Component {
  
    render() {
      return (
        <div>
          <img style={{maxWidth: '50px'}} src={imagesPath["report"]} />
        </div>
      );
    }
}

function VerticalSeperator() {
    return(
        <div>
          <img style={{maxWidth: '50px'}} src={imagesPath["verticalSeperator"]} />
        </div>
    );
    
}


class PostButtons extends React.Component {
    render(){
        return(<div >
            <div class= "row">
                <LikeButton></LikeButton>
                <VerticalSeperator></VerticalSeperator>
                <CommentButton></CommentButton>
                <VerticalSeperator></VerticalSeperator>
                <ShareButton></ShareButton>
                <VerticalSeperator></VerticalSeperator>
                <SaveButton></SaveButton>
                <VerticalSeperator></VerticalSeperator>
                <ReportButton></ReportButton>
            </div>
        </div>);
    }

}

export default PostButtons;
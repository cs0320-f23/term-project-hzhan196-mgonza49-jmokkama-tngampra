import defaultPhoto from "../assets/blank-profile.jpeg";
import "../style/interface.css";
import "../style/App.css";

import "../style/interface.css";

interface CommentProps {
  user: string;
  content: string;
  // yearTaken: string;
}

function Comment({
  user,
  content,
}: // , yearTaken
CommentProps) {
  return (
    <div className="comment-container">
      <div className="comment-container2">
        <img className="profile-circle" src={defaultPhoto}></img>
        <p
          className="text-lg font-bold mt-2 ml-2"
          style={{ fontWeight: "bold" }}
        >
          {user}
        </p>
        {/* <p className="mt-2 ml-2" style={{ color: 'gray'}}> | {yearTaken} Program Alum</p> */}
      </div>

      <p className="comment-body">{content}</p>
    </div>
  );
}

export default Comment;

import commentData from "./mockCommentData";
import "../style/interface.css";

interface CommentProps {
  user: string;
  content: string;
  yearTaken: string;
}


function Comment({ user, content, yearTaken }: CommentProps) {
  return (
    <div className="comment-container">
      <p className="text-lg font-bold mb-2">User: {user}</p>
      <p className="text-gray-800">{content}</p>
      <p className="text-gray-500 mt-2">Year Taken: {yearTaken}</p>
    </div>
  );
}

export default Comment

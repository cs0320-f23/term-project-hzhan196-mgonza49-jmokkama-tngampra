import commentData from "./mockCommentData";
import "../style/interface.css";

interface CommentProps {
  user: string;
  content: string;
}

export default function CommentStore({ user, content }: CommentProps) {
  return (
    <div className="comment-container">
      <p className="username">User: {user}</p>
      <p className="comment">{content}</p>
    </div>
  );
}

import React, { useState } from "react";
import "../style/interface.css";

interface CommentProps {
  user: string;
  content: string;
}

function showComments({ user, content }: CommentProps) {
  return (
    <div>
      <p>User: {user}</p>
      <p>{content}</p>
    </div>
  );
}

function Comment() {
  const [commentString, setCommentString] = useState("");

  const handleKeyPress = (event: any) => {
    if (event.key === "Enter") {
      //   if (user == null) {
      //     console.log("Error: You do not have access to this function");
      //   } else {
      //     showComments([user, commentString]);
      //   }
      console.log(commentString);
      setCommentString("");
    }
  };

  function handleSubmit(comment: string) {}

  return (
    <div className="flex items-center justify-center">
      <div className="search-container">
        <input
          type="text"
          id="searchInput"
          className="border rounded-full p-2 w-full h-12"
          placeholder="Taken this program? Share your thoughts!"
          value={commentString}
          onChange={(e) => setCommentString(e.target.value)}
          onKeyDown={handleKeyPress}
        />
      </div>
    </div>
  );
}

export default Comment;

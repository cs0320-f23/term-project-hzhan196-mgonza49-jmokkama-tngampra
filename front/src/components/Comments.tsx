import React, { useEffect, useState } from "react";
import { loginStatus } from "./Login";
import "../style/interface.css";

function Comment() {
  const [commentString, setCommentString] = useState("");

  const handleKeyPress = (event: any) => {
    if (event.key === "Enter") {
      handleSubmit(commentString);
      console.log(commentString);
      setCommentString("");
    }
  };

  function handleSubmit(comment: string) {}

  return (
    <div className="flex items-center justify-center">
      <div className="search-container">
        {" "}
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

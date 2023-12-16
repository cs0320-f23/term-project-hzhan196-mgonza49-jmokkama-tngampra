import React, { useState } from "react";
import "../style/interface.css";

function Search() {
  const [searchString, setSearchString] = useState("");

  const handleKeyPress = (event: any) => {
    if (event.key === "Enter") {
      handleSearch();
    }
  };

  function handleSearch() {
    console.log(searchString);
    setSearchString("");
  }

  return (
    <div className="flex items-center justify-center">
      <div className="search-container">
        <input
          type="text"
          id="searchInput"
          className="border rounded-full p-2 w-full h-12 shadow-lg"
          placeholder="Search for Programs..."
          value={searchString}
          onChange={(e) => setSearchString(e.target.value)}
          onKeyDown={handleKeyPress}
        />
      </div>
    </div>
  );
}

export default Search;

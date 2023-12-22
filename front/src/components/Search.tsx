import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../style/interface.css";

interface SearchProps {
  updateIcons: (searchString: string) => Promise<void>;
}

function Search({ updateIcons }: SearchProps) {
  const [searchString, setSearchString] = useState("");
  const navigate = useNavigate();

  const handleKeyPress = (event: any) => {
    if (event.key === "Enter") {
      // navigate("/browse/")
      updateIcons(searchString);
      setSearchString("");
    }
  };

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

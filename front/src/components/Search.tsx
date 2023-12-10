import React from 'react'
import "../style/interface.css"

function Search() {

    function handleSearch(){

    }


  return (
    <div className="search-container">
          {/* <input type="text" onChange={e => setSearchString(e.target.value)} />
          <button className="button" onClick={handleSearch}>Search</button>
          {notFound && <p> Movie not found </p>} */}


            <input type="text" id="searchInput" className="search-input"/> 
            <button className="button" onClick={handleSearch}> Search </button>

          </div>
  )
}

export default Search
import React from 'react'
import Navbar from './Navbar'
import Search from './Search'
import "../style/interface.css"


function Browse() {
  return (
    <div>
        <div className="navbar-container">
            <Navbar />
 
        </div>

        <div>
            <Search />
        </div>
    
      </div>
  )
}

// const Browse = () => {
//     return <div>Browse Page</div>;
//   };

export default Browse
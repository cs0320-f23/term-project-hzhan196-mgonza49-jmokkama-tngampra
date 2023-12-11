import React from 'react'
import Navbar from '../components/Navbar'
import Search from '../components/Search'
import "../style/interface.css"


// TODO probs just make a bunch of cards for each country here
function Browse() { 
  return (
    <div>
        <div className="navbar-container">
            <Navbar />

        </div>

        <div>
        <div className="bg-blue-500 text-white p-4">
  This should have a blue background and white text.
</div>
            <Search />
        </div>
    
      </div>
  )
}

// const Browse = () => {
//     return <div>Browse Page</div>;
//   };

export default Browse
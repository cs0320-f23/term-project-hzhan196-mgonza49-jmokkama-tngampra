import React from 'react'
import Navbar from '../components/Navbar'
import Search from '../components/Search'
import ProgramData from '../components/mockProgramData'
import { Link , useParams, Outlet, useNavigate} from 'react-router-dom';
import "../style/interface.css"



// TODO probs just make a bunch of cards for each country here
function BrowseList() { 
  const navigate = useNavigate();
  
  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>
  
      <div className="bg-blue-500 text-white p-4">  
        This should have a blue background and white text.
      </div>
  
      <div>
        <Search />
      </div>
  
      {ProgramData.map((program) => (
        <Link 
        key={program.id} 
        to={`/browse/${program.id}`}
        > 
        
        {program.name} 
        </Link>
      
      ))}

      {/* <ProgramList /> */}
    </div>
  )
}

// const Browse = () => {
//     return <div>Browse Page</div>;
//   };

export default BrowseList
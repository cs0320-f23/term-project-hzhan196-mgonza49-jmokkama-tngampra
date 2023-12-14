import React from 'react'
import Navbar from '../components/Navbar'
import BarChart from '../components/BarChart'
import ProgramData from '../components/mockProgramData'
import { Link , useParams, Outlet, useNavigate} from 'react-router-dom';
import "../style/interface.css"


interface Program {
    tempId: number;
    name: string;
    // country: string;
  }


function ProgramDisplay() {
  const { id } = useParams();
  const programId = id ? parseInt(id, 10) : 0;

  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>
      <div className="program-box">
        <div className="program-display">

          <div className="flex justify-center items-center">
              {/* <img src={ProgramData[programId-1].image} className="circular-image">
              </img> */}
            </div>

            <Link to="/browse/" className="ml-2 flex text-left mt-2 text-gray-300">
          Back to Browse
             </Link>
 
          <div className="display-title">
            {ProgramData[programId-1].name}
          </div>
        </div>


      {/* White block scroll contents  */}
      <div className="big-card-holder">
        <div className="display-info">
          <div className="text-lg font-bold">
              {ProgramData[programId-1].name}
            </div>
          <p> {ProgramData[programId-1].country}</p>
          <p> {ProgramData[programId-1].term}</p>
          <p>{ProgramData[programId-1].description}</p>
        </div>

        <div className="display-stats">
          <BarChart />
        </div>
      </div>
      

      </div>

    </div>
  );
}

export default ProgramDisplay;

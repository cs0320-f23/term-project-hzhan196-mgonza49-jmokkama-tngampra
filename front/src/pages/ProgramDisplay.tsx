import React from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import ProgramData from "../components/mockProgramData";
import { Link, useParams, Outlet, useNavigate } from "react-router-dom";
import "../style/interface.css";
import Comment from "../components/Comments";

interface Program {
  id: number;
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
      <h1> Program List </h1>
      {/* <ul>
            {Programs.map((prog:any) => (
                <li key= {prog.id}>
                    <Link to={`/programs/${prog.id}`}> 
                        {prog.name}
                    </Link>
                </li>
            ))}
        </ul> */}
      <h1>{ProgramData[programId - 1].name}</h1>{" "}
      <p>{ProgramData[programId - 1].description}</p>{" "}
      <img src={ProgramData[programId - 1].image}></img>
      <Comment />
    </div>
  );
}

export default ProgramDisplay;

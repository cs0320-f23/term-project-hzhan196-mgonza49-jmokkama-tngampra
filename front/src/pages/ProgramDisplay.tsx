import React, { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import ProgramData from "../components/mockProgramData";
import { Link, useParams, Outlet, useNavigate } from "react-router-dom";
import "../style/interface.css";
import Comment from "../components/Comments";
import { loginStatus } from "../components/Login";

interface Program {
  id: number;
  name: string;
  // country: string;
}

function commentDisplay() {
  const [commentStatus, setCommentStatus] = useState<Boolean>();
  useEffect(() => {
    loginStatus()
      .then((name) => {
        if (name === "Sign Out") {
          setCommentStatus(true);
        } else {
          setCommentStatus(false);
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);
  if (commentStatus) {
    return <Comment />;
  }
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
      {commentDisplay()}
    </div>
  );
}

export default ProgramDisplay;

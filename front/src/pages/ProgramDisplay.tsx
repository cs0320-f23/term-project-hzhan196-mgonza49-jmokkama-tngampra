import React, { ReactNode, useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import ProgramData from "../components/mockProgramData";
import { Link, useParams, Outlet, useNavigate } from "react-router-dom";
import "../style/interface.css";
// import Comment from "../components/Comments";
import { loginStatus } from "../components/Login";
import commentData from "../components/mockCommentData";
import Comment from "../components/Comment";
import BarChart from "../components/ChartComponent";
import Chart from 'chart.js/auto';
import ChartDataLabels from 'chartjs-plugin-datalabels';

interface Program {
  tempId: number;
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

  function setupComments() {
    const totalComments: ReactNode[] = [];
  
    commentData.forEach((comment) => {
      totalComments.push(
        <Comment user={comment.user} content={comment.comment} yearTaken={comment.yearTaken} />
      );
    });
  
    return totalComments;
  }

  if (commentStatus) {
    return (
      <div>

        <div className="comment-box-store">
        <p className= "comment-box-title"> 
            Program Reviews: 
            <Link to="/review" className="ml-2 button">
            Leave a Review!
          </Link></p>
          {setupComments()}
        </div>
        
        
      </div>
    
    );
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
      <div className="program-box">
        <div className="program-display">
          <div className="flex justify-center items-center">
            {/* <img src={ProgramData[programId-1].image} className="circular-image">
              </img> */}
          </div>

          <Link
            to="/browse/"
            className="ml-2 flex text-left mt-2 text-gray-300"
          >
            Back to Browse
          </Link>

          <div className="display-title">{ProgramData[programId - 1].name}</div>
        </div>

        {/* White block scroll contents  */}
        <div className="big-card-holder">
          <div className="display-info">
            <div className="text-lg font-bold">
              {ProgramData[programId - 1].name}
            </div>
            <p> {ProgramData[programId - 1].country}</p>
            <p> {ProgramData[programId - 1].term}</p>
            <p>{ProgramData[programId - 1].description}</p>
          </div>

          <div className="display-stats">
            Ratings:
            <BarChart programRating={ProgramData[programId-1].rating}/>
          </div>
        </div>
      </div>
      {commentDisplay()}
    </div>
  );
}

export default ProgramDisplay;

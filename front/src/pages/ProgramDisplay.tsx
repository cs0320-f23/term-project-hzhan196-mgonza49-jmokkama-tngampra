import React, { ReactNode, useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
// import ProgramData from "../components/mockProgramData";
import { Link, useParams, Outlet, useNavigate } from "react-router-dom";
import "../style/interface.css";
// import Comment from "../components/Comments";
import { loginStatus } from "../components/Login";
import commentData from "../components/mockCommentData";
import Comment from "../components/Comment";
import BarChart from "../components/ChartComponent";
import Chart from "chart.js/auto";
import ChartDataLabels from "chartjs-plugin-datalabels";

const tempRating = [1, 3, 2, 5, 3];

interface Program {
  tempId: number;
  name: string;
  country: string;
  link: string;
  rating: number[];
}

interface CommentList {
  key: number;
  user: string;
  comment: string;
  // yearTaken: string;
}

function commentDisplay(data: CommentList[]) {
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

    data.forEach((comment) => {
      totalComments.push(
        <Comment
          user={comment.user}
          content={comment.comment}
          // yearTaken={comment.yearTaken}
        />
      );
    });

    return totalComments;
  }

  if (commentStatus) {
    return (
      <div>
        <div className="comment-box-store">
          <p className="comment-box-title">Program Reviews: </p>
          {setupComments()}
        </div>
        <Link to="/review" className="ml-2 button">
          Leave a Review!
        </Link>
      </div>
    );
  }
}

function ProgramDisplay() {
  useEffect(() => {
    // scroll to top
    window.scrollTo(0, 0);
  }, []);

  const { id } = useParams();
  const programId = id ? parseInt(id, 10) : 0;
  // const data = ProgramData();
  const [data, setData] = useState<Program[]>([]);
  const [commentData, setCommentData] = useState<CommentList[]>([]);

  function toProgram(res: any) {
    const programArray: Program[] = [];
    const commentArray: CommentList[] = [];
    if (res.result === "success") {
      const programs: any = res.data;
      programs.forEach((program: any, index: number) => {
        const id = index + 1;
        programArray.push({
          tempId: id,
          name: program.name,
          country: program.location,
          link: program.link,
          rating: program.average,
        });
      });
      commentArray.push({
        key: programId - 1,
        user: programs[programId - 1].comments[0].username,
        comment: programs[programId - 1].comments[0].comment,
      });
    }
    setCommentData(commentArray);
    return programArray;
  }

  useEffect(() => {
    async function fetchPrograms() {
      const url = "http://localhost:3232/viewdata";
      try {
        const res = await fetch(url);
        if (!res.ok) {
          throw new Error("Error");
        }
        const jsonData = await res.json();
        const programs = toProgram(jsonData);
        setData(programs);
      } catch (error) {
        console.error("Error fetching programs:", error);
      }
    }

    fetchPrograms();
  }, []);
  const selectedProgram = data.find((program) => program.tempId === programId);

  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>
      {selectedProgram && (
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

            <div className="display-title">{selectedProgram.name}</div>
          </div>

          {/* White block scroll contents  */}
          <div className="big-card-holder">
            <div className="display-info">
              <div className="text-lg font-bold">
                {data[programId - 1].name}
              </div>

              <div className="">
                <p> {data[programId - 1].country}</p>
              </div>
            </div>
            <div className="display-stats">
              Ratings:
              {/* <BarChart programRating={data[programId].rating} /> */}
              <BarChart programRating={tempRating} />
            </div>
          </div>
        </div>
      )}

      {commentDisplay(commentData)}
    </div>
  );
}

export default ProgramDisplay;

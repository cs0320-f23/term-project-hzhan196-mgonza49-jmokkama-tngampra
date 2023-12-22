import { ReactNode, useEffect, useState } from "react";
import Navbar from "../components/Navbar";
// import ProgramData from "../components/mockProgramData";
import { Link, useParams } from "react-router-dom";
import "../style/interface.css";
// import Comment from "../components/Comments";
import { loginStatus } from "../components/Login";
// import commentData from "../components/mockCommentData";
import Comment from "../components/Comment";
import BarChart from "../components/ChartComponent";
import { ActualProgram } from "./Homepage";

let tempRating = [0, 0, 0, 0, 0];

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

    data.forEach((comment, index: number) => {
      totalComments.push(
        <Comment
          key={index}
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
  const rateCounted: string[] = [];

  function toProgram(res: any) {
    const programArray: Program[] = [];
    const commentArray: CommentList[] = [];

    if (res.result === "success") {
      const programs: any = res.data;
      programs.forEach((program: ActualProgram, index: number) => {
        const id = index + 1;
        programArray.push({
          tempId: id,
          name: program.name,
          country: program.location,
          link: program.link,
          rating: program.average,
        });
      });
      if (programs[programId - 1].comments.length !== 0) {
        programs[programId - 1].comments.forEach(
          (comment: { username: string; comment: string }) =>
            commentArray.push({
              key: programId - 1,
              user: comment.username,
              comment: comment.comment,
            })
        );
      }
      Object.entries(programs[programId - 1].userScores).forEach(
        ([user, rating]) => {
          if (rating && typeof [user, rating] === "object") {
            Object.entries(rating).forEach(([key, value]) => {
              if (key === "overall" && !rateCounted.includes(user)) {
                tempRating[value - 1] = tempRating[value - 1] + 1;
                rateCounted.push(user);
              }
            });
          }
        }
      );
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
                <a href={data[programId - 1].link}>Learn more here!</a>
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

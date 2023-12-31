"use client";
import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import Icons from "../components/Icons";
import Recommended from "../mockedData/mockRecommended";
import { ReactNode } from "react";
import { forms, userCounted } from "../components/Form";
import "../style/interface.css";
import { profileEmail } from "../components/Login";
import React from "react";
import defaultPhoto from "../assets/blank-profile.jpeg";
import { handleSearch } from "./BrowseList";
import { countries } from "../components/Countries";

interface UserProps {}
export interface ActualProgram {
  name: string;
  link: string;
  location: string;
  userScores: {
    username: {
      acceptance: number;
      safety: number;
      overall: number;
      learning: number;
      minority: number;
    };
  };
  comments: { comment: string; username: string }[];
  average: number[];
}

export default function Homepage({}: UserProps) {
  const [icons, setIcons] = useState<React.ReactNode[]>([]);

  function getCountryFlag(location: string): string {
    function capitalizeWords(str: string): string {
      return str
        .split(" ")
        .map(
          (word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
        )
        .join(" ");
    }
    // just to change ex ISRAEL to Israel so i can search it in Countries.tsx
    const uncapitalizedLocation = capitalizeWords(location);
    const flagMapping = countries;
    const flagURL = flagMapping[uncapitalizedLocation] || defaultPhoto;
    return flagURL;
  }

  useEffect(() => {
    async function fetchPrograms() {
      try {
        const data = await getPrograms();
        setIcons(data);
      } catch (error) {
        console.error("Error fetching programs:", error);
      }
    }

    fetchPrograms();
  }, []);

  const [email, setEmail] = useState<string>("");
  useEffect(() => {
    profileEmail().then((name) => {
      setEmail(name);
    });
  });

  const [isMember, setIsMember] = useState<boolean>(false);
  useEffect(() => {
    userCounted(email).then((hasTaken) => {
      setIsMember(hasTaken);
    });
  }, [email]);

  function getPrograms() {
    let url;
    if (isMember) {
      url = "http://localhost:3232/viewdata?email=" + email;
    } else {
      url = "http://localhost:3232/viewdata";
    }
    return fetch(url)
      .then((res) => {
        if (!res.ok) {
          return Promise.reject("Error");
        }
        return res.json();
      })
      .then((res) => setupIcons(res))
      .catch((error) => {
        console.error(error);
        return Promise.reject("Error: " + error);
      });
  }

  function setupIcons(res: any) {
    const totalIcons: ReactNode[] = [];
    if (res.result === "success") {
      const programs: any = res.data;
      programs.forEach((program: ActualProgram, index: number) => {
        const id = index + 1;
        totalIcons.push(
          <Icons
            key={id}
            name={program.name}
            image={getCountryFlag(program.location)}
            link={`/browse/${id}`}
            id={id}
            country={program.location}
          />
        );
      });
    }
    setIcons(totalIcons);
    return totalIcons;
  }
  const updateIcons = async (searchString: string) => {
    try {
      const data = await handleSearch(searchString);
      setIcons(data);
    } catch (error) {
      console.error("Error fetching programs:", error);
    }
  };
  return (
    <div>
      <div id="navbar" className="navbar-container">
        <Navbar />
      </div>

      <div id="homepage" className="h-screen">
        <h1 className="shadowed-text main-title text-2xl lg:text-6xl text-white font-semibold mt-2">
          Study Abroad @ Brown
        </h1>

        <h2 className="shadowed-text-small text-white mt-2">
          Helping Students Navigate Brown's Study Abroad Programs
        </h2>
        <Search updateIcons={updateIcons} />

        <div className="flex flex-row justify-center mt-2">
          <span className="shadowed-text-small text-white"> or</span>
          <a
            href="/browse"
            className="shadowed-text-small underlined-text ml-2 text-white hover:text-white"
          >
            Browse Programs
          </a>
        </div>
      </div>

      <div className="rec-container-wrap">
        <div className="shadowed-text-small main ">Your Recommended: </div>

        <div className="rec-icon-container">{icons}</div>
      </div>

      <div>{forms()}</div>
    </div>
  );
}

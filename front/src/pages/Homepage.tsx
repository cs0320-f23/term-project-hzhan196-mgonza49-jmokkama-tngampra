"use client";
import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import Icons from "../components/Icons";
import Recommended from "../components/mockRecommended";
import { Link, useParams, Outlet } from "react-router-dom";
import { ReactNode } from "react";
import { forms } from "../components/Form";
import "../style/interface.css";
import { loginStatus } from "../components/Login";
import React from "react";
import defaultPhoto from "../assets/blank-profile.jpeg";

interface UserProps {}

export default function Homepage({}: UserProps) {
  const [icons, setIcons] = useState<React.ReactNode[]>([]);

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

  function getPrograms() {
    const url = "http://localhost:3232/viewdata";
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
    console.log(res);
    if (res.result === "success") {
      const programs: any = res.data;
      programs.forEach((program: any, index: number) => {
        const id = index;
        icons.push(
          <Icons
            key={id}
            name={program.name}
            image={defaultPhoto}
            link={`/browse/${id}`}
            id={id}
            country={program.location}
            // term={program.term}
          />
        );
      });
    }
    return icons;
  }
  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>

      <div className="h-screen">
        <h1 className="shadowed-text main-title text-2xl lg:text-6xl text-white font-semibold mt-2">
          Study Abroad @ Brown
        </h1>

        <h2 className="shadowed-text-small text-white mt-2">
          Helping Students Navigate Brown's Study Abroad Programs
        </h2>
        <Search />

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

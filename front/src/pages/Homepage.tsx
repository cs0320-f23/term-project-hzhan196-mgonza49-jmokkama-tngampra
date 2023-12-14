"use client";
import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import Icons from "../components/Icons.tsx";
import Recommended from "../components/mockRecommended.tsx";
import { Link, useParams, Outlet } from "react-router-dom";
import { ReactNode } from "react";
import { forms } from "../components/Form.tsx";
import "../style/interface.css";
import { loginStatus } from "../components/Login.tsx";

interface UserProps {}

function setupIcons() {
  const totalIcons: ReactNode[] = [];

  Recommended.forEach((program) => {
    totalIcons.push(
      <Icons
        key={program.id} // Make sure to add a unique key when rendering components in a loop
        image={program.image}
        name={program.name}
        country={program.country}
        term={program.term}
        link={`/browse/${program.id}`}
        id={program.id}
      />
    );
  });

  return totalIcons;
}

export default function Homepage({}: UserProps) {
  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>

      <div className="bg-gray-500 flex flex-col h-screen">
        <h1 className="main-title text-2xl lg:text-6xl text-white font-semibold mt-2">
          Study Abroad @ Brown
        </h1>

        <h2 className="text-gray-300 mt-2">
          some placeholder text here, gray bg is also temporary
        </h2>
        <Search />

        <div className="flex flex-row justify-center mt-2">
          <span className="text-gray-300"> or</span>
          <a
            href="/browse"
            className="underlined-text ml-2 text-gray-300 hover:text-white"
          >
            Browse Programs
          </a>
        </div>
      </div>

      <div className="rec-container-wrap">
        <div className="main">Your Recommended: </div>

        <div className="rec-icon-container">{setupIcons()}</div>
      </div>

      <div>{forms()}</div>
    </div>
  );
}

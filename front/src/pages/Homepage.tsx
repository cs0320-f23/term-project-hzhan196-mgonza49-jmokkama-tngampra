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

      <div className="h-screen">
        <h1 className="shadowed-text main-title text-2xl lg:text-6xl text-white font-semibold mt-2">
          Study Abroad @ Brown
        </h1>

        <h2 className="shadowed-text-small font-panton-semibold text-white mt-2" >
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
        <div className="shadowed-text-small main">Your Recommended: </div>

        <div className="rec-icon-container">{setupIcons()}</div>
      </div>

      <div>{forms()}</div>
    </div>
  );
}

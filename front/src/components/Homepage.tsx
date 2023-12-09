"use client";
import { useState } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import Navbar from "./Navbar.tsx";
import "../style/interface.css";
import { Icons } from "./Icons.tsx";
import { ReactNode } from "react";

interface UserProps {}

const mockData: [string, string][] = [
  [
    "https://cdn.britannica.com/25/4825-004-F1975B92/Flag-United-Kingdom.jpg",
    "UK",
  ],
  [
    "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Flag_of_Ireland.svg/255px-Flag_of_Ireland.svg.png",
    "Ireland",
  ],
  [
    "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Canada_%28Pantone%29.svg/255px-Flag_of_Canada_%28Pantone%29.svg.png",
    "Canada",
  ],
  [
    "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Flag_of_the_People%27s_Republic_of_China.svg/2560px-Flag_of_the_People%27s_Republic_of_China.svg.png",
    "China",
  ],
  [
    "data:image/png;base64,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",
    "Spain",
  ],
];

function setupIcons(data: [string, string][]) {
  const totalIcons: ReactNode[] = [];
  for (const item of data) {
    console.log("item 0" + item[0]);
    console.log("item 1" + item[1]);

    totalIcons.push(Icons(item[0], item[1]));
  }
  return totalIcons;
}

function handleSearch(){

}

export default function Homepage({}: UserProps) {
  return (
    <div>
      <div className="navbar-container">
        {/* {" "} */}
        <Navbar />
      </div>

      <div className="search-container">
          {/* <input type="text" onChange={e => setSearchString(e.target.value)} />
          <button className="button" onClick={handleSearch}>Search</button>
          {notFound && <p> Movie not found </p>} */}


            <input type="text" id="searchInput" className="search-input"/> 
            <button className="button" onClick={handleSearch}> Search </button>

          </div>

      <div>
        <div className="main">You might like:</div>
        {setupIcons(mockData)}
      </div>
    </div>
  );
}

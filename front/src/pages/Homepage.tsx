"use client";
import { useState } from "react";
import Navbar from "../components/Navbar.tsx";
import Search from "../components/Search.tsx";
import Icons from "../components/Icons.tsx";
import { Link , useParams, Outlet} from 'react-router-dom';
import { ReactNode } from "react";
import { forms } from "../components/Form.tsx";
import "../style/interface.css";

interface UserProps {}

export default function Homepage({}: UserProps) {
  return (
    <div>


      <div className="navbar-container">
        <Navbar />
      </div>

      

      <div className="bg-gray-500 flex flex-col justify-center h-screen">
        <h1 className="margin-bottom-5 text-2xl lg:text-6xl text-gray-300 font-semibold">Study Abroad @ Brown</h1>
       
        <h2 className="text-gray-300 mt-2">some placeholder text here, gray bg is also temporary</h2>
        <Search />

        <div className="flex flex-row justify-center mt-2">
          <span className="text-gray-300"> or</span>
          <a href="/browse" className="underlined-text ml-2 text-gray-300 hover:text-white">
            Browse Programs
          </a>
        </div>

      </div>

      <div>
        <div className="main">You might like: </div>
        <div className="main">
          You might like:
          sjdbfjksdbf
          jkdbfsjkdbf
          skjfbdsjkf
          jksdbfjksdf
          jksdbfjksdfkjsdbfkjsd
          sdfjkbsd
          sdfjkbsda
          2dw3BCZ3KfSsFyeLxUv6uUD2jqBEcfYgCivXOi1hBj
          sjdbfjksdbf
          ndjksnncxvn
          siehjsdf
          
           </div>
        {/* {setupIcons()} */}
      </div>

      <div>{forms()}</div>
    </div>
  );
}

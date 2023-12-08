"use client";
import { useState } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import Navbar from "./Navbar.tsx";
import "../style/interface.css";

interface UserProps {}

export default function Homepage({}: UserProps) {
  return (
    <div>
      <div className="navbar-container"> <Navbar /> </div>
      
      <div>
        <div className="search-container">
          <input type="text" id="searchInput" />
          {/* <button onclick="toggleSearch()"/> */}
        </div>

        <div className="main">body</div>
      </div>

      <script>function openSearch() {}</script>
    </div>
  );
}

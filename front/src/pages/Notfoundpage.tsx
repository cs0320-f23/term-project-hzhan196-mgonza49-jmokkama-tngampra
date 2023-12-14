import React from "react";
import Navbar from "../components/Navbar";

function Notfoundpage() {
  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>

      <div style={{ marginTop: "20px" }}>Page wasn't found!</div>
    </div>
  );
}

export default Notfoundpage;

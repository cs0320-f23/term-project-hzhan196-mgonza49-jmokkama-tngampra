import React from 'react'
import { ReactNode } from "react";
import Navbar from '../components/Navbar.tsx'
import Search from '../components/Search.tsx'
import Icons from "../components/Icons.tsx";
import ProgramData from '../components/mockProgramData.tsx'
import { Link , useParams, Outlet, useNavigate} from 'react-router-dom';
import "../style/interface.css"


function ViewProfile() {
  return (
    <div>
      <Navbar />
      
      View Profile</div>
  )
}

export default ViewProfile
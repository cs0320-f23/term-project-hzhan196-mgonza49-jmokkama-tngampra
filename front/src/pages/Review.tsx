import React from 'react'
import { ReactNode } from "react";
import Navbar from '../components/Navbar'
import Search from '../components/Search'
import Icons from "../components/Icons.tsx";
import ProgramData from '../components/mockProgramData'
import { Link , useParams, Outlet, useNavigate} from 'react-router-dom';
import "../style/interface.css"

function Review() {
  return (
    <div>
        <Navbar />
        Review
        </div>
  )
}

export default Review
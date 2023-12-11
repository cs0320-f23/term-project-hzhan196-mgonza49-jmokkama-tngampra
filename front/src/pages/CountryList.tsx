import React from 'react'
import Navbar from '../components/Navbar'
import Search from '../components/Search'
import { Link } from 'react-router-dom';
import "../style/interface.css"

interface Program {
    id: number;
    name: string;
    // country: string;
  }

 const programs: Program[] = [
    {id : 1, name : "UK"},
    {id: 2, name: "Ireland"},
    {id: 3, name: "Canada"},
    {id: 4, name: "China"},
    {id: 5, name: "Spain"},
 ];


function CountryPage() {
  return (
    <div>
        <h1> Program List </h1>
        <ul>
            {programs.map((prog) => (
                <li key= {prog.id}>
                    <Link to={`/programs/${prog.id}`}> 
                        {prog.name}
                    </Link>
                </li>
            ))}
        </ul>
    
    </div>
  )
}

export default CountryPage
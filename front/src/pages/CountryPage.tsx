import React from 'react'
import Navbar from '../components/Navbar'
import Search from '../components/Search'
import { Link , useParams} from 'react-router-dom';
import "../style/interface.css"

interface RouteParams {
    id: string;
}

function CountryPage() {
    // const {id} = useParams<RouteParams>();



  return (
    <div>CountryPage</div>
  )
}

export default CountryPage
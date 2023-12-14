import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter as Router } from 'react-router-dom';
import './style/index.css'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Homepage from './pages/Homepage.tsx';
import BrowseList from './pages/BrowseList.tsx';
import Notfoundpage from './pages/Notfoundpage.tsx';
import ProgramDisplay from './pages/ProgramDisplay.tsx';
import Profile from './pages/ViewProfile.tsx'
import Review from './pages/Review.tsx';
import 'chart.js/auto';
import { Bar } from "react-chartjs-2";
import ChartDataLabels from 'chartjs-plugin-datalabels';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter> 
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="*" element={<Notfoundpage />} />
        <Route path="profile" element={<Profile />} />

        <Route path="browse" element={<BrowseList />} />
        <Route path="browse/:id" element={<ProgramDisplay />} />
        <Route path="review" element={<Review />} />
    
      </Routes>
    </BrowserRouter>
  </React.StrictMode>,
)

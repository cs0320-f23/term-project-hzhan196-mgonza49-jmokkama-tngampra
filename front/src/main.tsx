import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter as Router } from 'react-router-dom';
import './style/index.css'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Homepage from './pages/Homepage.tsx';
import Browse from './pages/Browse.tsx';
import Notfoundpage from './pages/Notfoundpage.tsx';
import CountryPage from './pages/CountryPage.tsx';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="Browse" element={<Browse />} />
        <Route path="programs/:id" element={<CountryPage />} />
        <Route path="*" element={<Notfoundpage />} />
    
      </Routes>
    </BrowserRouter>
  </React.StrictMode>,
)

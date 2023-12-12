import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter as Router } from 'react-router-dom';
import './style/index.css'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Homepage from './pages/Homepage.tsx';
import BrowseList from './pages/BrowseList.tsx';
import Notfoundpage from './pages/Notfoundpage.tsx';
import ProgramList from './pages/ProgramList.tsx';
import ProgramDisplay from './pages/ProgramDisplay.tsx';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter> 
      <Routes>
        <Route path="/" element={<Homepage />} />
        <Route path="*" element={<Notfoundpage />} />
        {/* <Route path="browse" element={<BrowseList />} /> */}

        <Route path="browse" element={<BrowseList />} >
          {/* <Route path="browse" element={<BrowseList />} /> */}
          
        </Route>
        <Route path="browse/:id" element={<ProgramDisplay />} />
    
      </Routes>
    </BrowserRouter>
  </React.StrictMode>,
)

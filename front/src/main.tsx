import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter as Router } from 'react-router-dom';
import App from './App.tsx'
import './style/index.css'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Homepage from './components/Homepage.tsx';
import Browse from './components/Browse.tsx';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" >
          <Route index element={<Homepage />} />
          <Route path="Browse" element={<Browse />} />
        </Route>
      </Routes>
    </BrowserRouter>
  </React.StrictMode>,
)

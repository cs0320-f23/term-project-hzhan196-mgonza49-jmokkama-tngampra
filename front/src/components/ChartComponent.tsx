import React from 'react'
import { ReactNode, useRef, useEffect } from "react";
import { BrowserRouter, Link} from 'react-router-dom';
import "../pages/ProgramDisplay"
import "../style/interface.css";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS } from "chart.js/auto";


function ChartComponent() {
  const labels = [1, 2, 3, 4, 5];
  const data = {
    labels: labels,
    datasets: [{
      axis: 'y',
      label: 'Rating',
      data: [1,2,3,2,4],
      fill: false,
      backgroundColor: [
        'rgba(255, 99, 132, 0.2)',
        'rgba(255, 159, 64, 0.2)',
        'rgba(255, 205, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(153, 102, 255, 0.2)',
        'rgba(201, 203, 207, 0.2)'
      ],
      borderColor: [
        'rgb(255, 99, 132)',
        'rgb(255, 159, 64)',
        'rgb(255, 205, 86)',
        'rgb(75, 192, 192)',
        'rgb(54, 162, 235)',
        'rgb(153, 102, 255)',
        'rgb(201, 203, 207)'
      ],
      borderWidth: 1
    }]
  };

  const options = {
    indexAxis: 'y', // Specify 'y' for horizontal bars
    scales: {
      // x: { // Use 'x' for the horizontal axis
      //   beginAtZero: true,
      // },
      // y: {
      //   beginAtZero: true,
      // },
    },
    aspectRatio: 7/8,
  };

  return (
    <div style={{ }}> {/* Adjust the width as needed */}
      <Bar data={data} options={options} />
    </div>
  );
}

export default ChartComponent;
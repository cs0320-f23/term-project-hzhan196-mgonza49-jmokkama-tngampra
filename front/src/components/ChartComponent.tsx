import React from "react";
import { ReactNode, useRef, useEffect } from "react";
import { BrowserRouter, Link } from "react-router-dom";
import "../pages/ProgramDisplay";
import "../style/interface.css";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS, ChartOptions } from "chart.js/auto";
import ChartDataLabels from "chartjs-plugin-datalabels";

ChartJS.register(ChartDataLabels);

function ChartComponent({ programRating }: { programRating: number[] }) {
  const labels = [1, 2, 3, 4, 5];
  const data = {
    labels: labels,
    datasets: [
      {
        axis: "y",
        label: "Rating",
        data: programRating,
        fill: false,
        backgroundColor: [
          // 'rgba(255, 99, 132, 0.2)',
          // 'rgba(255, 159, 64, 0.2)',
          // 'rgba(255, 205, 86, 0.2)',
          // 'rgba(75, 192, 192, 0.2)',
          // 'rgba(54, 162, 235, 0.2)',
          // 'rgba(153, 102, 255, 0.2)',
          // 'rgba(201, 203, 207, 0.2)'
          // 'rgba(255, 205, 86, 0.2)',
          // 'rgba(255, 205, 86, 0.2)',
          // 'rgba(255, 205, 86, 0.2)',
          // 'rgba(255, 205, 86, 0.2)',
          // 'rgba(255, 205, 86, 0.2)',
          // 'rgba(255, 205, 86, 0.2)',
          "rgba(111, 209, 23, 0.2)",
          "rgba(111, 209, 23, 0.2)",
          "rgba(111, 209, 23, 0.2)",
          "rgba(111, 209, 23, 0.2)",
          "rgba(111, 209, 23, 0.2)",
          "rgba(111, 209, 23, 0.2)",
        ],
        borderColor: [
          // 'rgb(255, 99, 132)',
          // 'rgb(255, 159, 64)',
          // 'rgb(255, 205, 86)',
          // 'rgb(75, 192, 192)',
          // 'rgb(54, 162, 235)',
          // 'rgb(153, 102, 255)',
          // 'rgb(201, 203, 207)'
          "rgb(13, 174, 76)",
          "rgb(13, 174, 76)",
          "rgb(13, 174, 76)",
          "rgb(13, 174, 76)",
          "rgb(13, 174, 76)",
        ],
        borderWidth: 1,
      },
    ],
  };

  const options: ChartOptions<"bar"> = {
    indexAxis: "y",
    scales: {
      x: {
        display: false,
        beginAtZero: true,
      },
      y: {
        display: true,
        beginAtZero: true,
      },
    },
    aspectRatio: 0.99,

    plugins: {
      datalabels: {
        anchor: "end",
        align: "end",
        formatter: (value: number) => value, // Display the data value at the end of each bar
        color: "black", // Adjust the color of the displayed value
        font: {
          weight: "bold",
        },
      },
    },

    layout: {
      padding: {
        top: 20,
        right: 25,
        bottom: 30,
        // left: 20,
      },
    },
  };

  return (
    <div style={{ display: "flex", justifyContent: "center", height: "40vh" }}>
      <Bar
        style={{ flex: 1, maxHeight: "100%" }}
        data={data}
        options={options}
      />
    </div>
  );
}

export default ChartComponent;

import React, { useEffect } from "react";
import { ReactNode, Fragment, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import Icons from "../components/Icons.tsx";
import ProgramData from "../components/mockProgramData";
import { Link, useParams, Outlet, useNavigate } from "react-router-dom";
import "../style/interface.css";
import defaultPhoto from "../assets/blank-profile.jpeg";
import { Combobox, Transition } from "@headlessui/react";
import { CheckIcon, ChevronUpDownIcon } from "@heroicons/react/20/solid";

// const mockData: [string, string][] = [
//   [
//     "https://cdn.britannica.com/25/4825-004-F1975B92/Flag-United-Kingdom.jpg",
//     "UK",
//   ],
//   [
//     "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Flag_of_Ireland.svg/255px-Flag_of_Ireland.svg.png",
//     "Ireland",
//   ],
//   [
//     "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d9/Flag_of_Canada_%28Pantone%29.svg/255px-Flag_of_Canada_%28Pantone%29.svg.png",
//     "Canada",
//   ],
//   [
//     "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Flag_of_the_People%27s_Republic_of_China.svg/2560px-Flag_of_the_People%27s_Republic_of_China.svg.png",
//     "China",
//   ],
//   [
//     "data:image/png;base64,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",
//     "Spain",
//   ],
//   ["https://cdn.britannica.com/73/2573-050-C825CE68/Flag-Mexico.jpg", "Mexico"],
//   [
//     "data:image/png;base64,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",
//     "Portugal",
//   ],
//   [
//     "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAARMAAAC3CAMAAAAGjUrGAAAAFVBMVEX///8AkkbOKzcAjz56uI/ehIjNHy1xf0V9AAAA/klEQVR4nO3QSQ0AIAADsHH6l4yKPUhaCc2oWTs9586aOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHixIkTJ06cOHHy4ckD5KrN4eD2boIAAAAASUVORK5CYII=",
//     "Italy",
//   ],
// ];

function getPrograms() {
  const url = "http://localhost:3232/viewdata";
  return fetch(url)
    .then((res) => {
      if (!res.ok) {
        return Promise.reject("Error");
      }
      return res.json();
    })
    .then((res) => setupIcons(res))
    .catch((error) => {
      console.error(error);
      return Promise.reject("Error: " + error);
    });
}

function setupIcons(res: any) {
  const totalIcons: ReactNode[] = [];
  console.log(res);
  if (res.result === "success") {
    const programs: any = res.data;
    programs.forEach((program: any, index: number) => {
      const id = index;
      totalIcons.push(
        <Icons
          name={program.name}
          image={defaultPhoto}
          link={`/browse/${id}`}
          id={program.name}
          country={program.location}
          // term={program.term}
        />
      );
    });
  }
  return totalIcons;
}

function BrowseList() {
  const [icons, setIcons] = useState<React.ReactNode[]>([]);

  useEffect(() => {
    async function fetchPrograms() {
      try {
        const data = await getPrograms();
        setIcons(data);
      } catch (error) {
        console.error("Error fetching programs:", error);
      }
    }

    fetchPrograms();
  }, []);
  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>

      <div>
        <Search />
      </div>

      <div className="icon-container">{icons}</div>
    </div>
  );
}

export default BrowseList;

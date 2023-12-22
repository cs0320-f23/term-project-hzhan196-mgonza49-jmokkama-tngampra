import React, { useEffect } from "react";
import { ReactNode, Fragment, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import Icons from "../components/Icons.tsx";
import ProgramData from "../mockedData/mockProgramData.tsx";
import { Link, useParams, Outlet, useNavigate } from "react-router-dom";
import "../style/interface.css";
import defaultPhoto from "../assets/blank-profile.jpeg";
import { Combobox, Transition } from "@headlessui/react";
import { CheckIcon, ChevronUpDownIcon } from "@heroicons/react/20/solid";
import { countries } from "../components/Countries.tsx";

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



function getCountryFlag(location:string):string {
  function capitalizeWords(str: string): string {
    return str
      .split(" ")
      .map((word) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(" ");
  }
  // just to change ex ISRAEL to Israel so i can search it in Countries.tsx
  const uncapitalizedLocation = capitalizeWords(location);
  const flagMapping = countries;
  const flagURL = flagMapping[uncapitalizedLocation] || defaultPhoto;
  return flagURL;
}


export function setupIcons(res: any) {
  const totalIcons: ReactNode[] = [];
  console.log(res);
  if (res.result === "success") {
    const programs: any = res.data;
    programs.forEach((program: any, index: number) => {
      const id = index + 1;
      totalIcons.push(
        <Icons
          key={id}
          name={program.name}
          image={getCountryFlag(program.location)}
          link={`/browse/${id}`}
          id={id}
          country={program.location}
        />
      );
    });
  }
  return totalIcons;
}

export const handleSearch = async (
  searchString: string
): Promise<React.ReactNode[]> => {
  try {
    const url = "http://localhost:3232/searchprograms?keyword=" + searchString;

    const response = await fetch(url);
    if (!response.ok) {
      console.error("Error");
      return [];
    }
    const data = await response.json();
    return setupIcons(data);
  } catch (error) {
    console.error("Error: ", error);
    return [];
  }
};

function BrowseList() {
  const [icons, setIcons] = useState<React.ReactNode[]>([]);

  const updateIcons = async (searchString: string) => {
    try {
      const data = await handleSearch(searchString);
      setIcons(data);
    } catch (error) {
      console.error("Error fetching programs:", error);
    }
  };

  useEffect(() => {
    const fetchPrograms = async () => {
      try {
        const data = await getPrograms();
        setIcons(data);
      } catch (error) {
        console.error("Error fetching programs:", error);
      }
    };
    fetchPrograms();
  }, []);
  return (
    <div>
      <div id="navbar" className="navbar-container">
        <Navbar />
      </div>

      <div>
        <Search updateIcons={updateIcons} />
      </div>

      <div className="icon-container">{icons}</div>
    </div>
  );
}

export default BrowseList;

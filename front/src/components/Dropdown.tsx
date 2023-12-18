import React from "react";
import { ReactNode, Fragment, useState, useEffect } from "react";
import Navbar from "../components/Navbar";
import ProgramData from "../components/mockProgramData";
import { Link, useNavigate } from "react-router-dom";
import { Combobox, Transition } from "@headlessui/react";
import { CheckIcon, ChevronUpDownIcon } from "@heroicons/react/24/solid";
import {} from "@heroicons/react/24/outline";
import { ErrorMessage, Field, FieldArray, Form, Formik } from "formik";
import RatingButton from "../components/Radio";
import Popup from "../components/Popup";
import Divider from "@mui/material/Divider";
import { getValue } from "firebase/remote-config";

export interface DropdownProps {
  id: number;
  name: string;
}

export default function dropdown({
  data,
  onSelect,
}: {
  data: DropdownProps[];
  onSelect: (selected: DropdownProps) => void;
}) {
  useEffect(() => {
    // scroll to top
    window.scrollTo(0, 0);
  }, []);
  const [selected, setSelected] = useState<DropdownProps>(data[0]);
  const [query, setQuery] = useState("");

  const filteredData =
    query === ""
      ? data
      : data.filter((item) =>
          item.name
            .toLowerCase()
            .replace(/\s+/g, "")
            .includes(query.toLowerCase().replace(/\s+/g, ""))
        );

  const handleItemSelected = (item: DropdownProps) => {
    setSelected(item);
    onSelect(item); // Trigger onSelect event
  };

  return (
    <div className="dropdown-wrap">
      <Combobox value={selected} onChange={setSelected}>
        <div className="dropdown-container">
          <Combobox.Input
            className="w-full py-2 pl-3 pr-10 black-text bg-white"
            displayValue={(item: DropdownProps) => item.name}
            onChange={(event) => setQuery(event.target.value)}
          />
          <Combobox.Button className="icon-holder">
            <ChevronUpDownIcon className="h-6 w-5 block" />
          </Combobox.Button>
        </div>
        <Transition
          as={Fragment}
          enter="transition ease-out duration-100"
          enterFrom="transform opacity-0 scale-95"
          enterTo="transform opacity-100 scale-100"
          leave="transition ease-in duration-75"
          leaveFrom="transform opacity-100 scale-100"
          leaveTo="transform opacity-0 scale-95"
          afterLeave={() => setQuery("")}
        >
          <Combobox.Options className="dropdown-window">
            {filteredData.length === 0 && query !== "" ? (
              <div className="relative cursor-default select-none px-4 py-2 text-gray-500">
                Nothing found.
              </div>
            ) : (
              filteredData.map((item) => (
                <Combobox.Option
                  key={item.id}
                  className={({ active }) =>
                    `relative cursor-default select-none py-2 pl-10 pr-4 ${
                      active ? "bg-gray-500 text-white" : "black-text"
                    }`
                  }
                  value={item}
                  onSelect={() => {
                    handleItemSelected(item);
                  }}
                >
                  {({ selected, active }) => (
                    <>
                      <span
                        className={`block truncate ${
                          selected ? "font-medium" : "font-normal"
                        }`}
                      >
                        {item.name}
                      </span>
                      {selected ? (
                        <span
                          className={`absolute inset-y-0 left-0 flex items-center pl-3 ${
                            active ? "text-white" : "text-teal-600"
                          }`}
                        >
                          <CheckIcon className="h-5 w-5" aria-hidden="true" />
                        </span>
                      ) : null}
                    </>
                  )}
                </Combobox.Option>
              ))
            )}
          </Combobox.Options>
        </Transition>
      </Combobox>
    </div>
  );
}

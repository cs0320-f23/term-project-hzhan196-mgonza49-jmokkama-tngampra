import React from "react";
import { ReactNode, Fragment, useState } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import ProgramData from "../components/mockProgramData";
import { Link, useNavigate } from "react-router-dom";
import { Combobox, Transition } from '@headlessui/react'
import { CheckIcon, ChevronUpDownIcon } from '@heroicons/react/20/solid'
import {} from '@heroicons/react/24/outline'
import { ErrorMessage, Field, FieldArray, Form, Formik } from "formik";

const people = [
  { id: 1, name: 'Durward Reynolds' },
  { id: 2, name: 'Kenton Towne' },
  { id: 3, name: 'Therese Wunsch' },
  { id: 4, name: 'Benedict Kessler' },
  { id: 5, name: 'Katelyn Rohan' },
]

function example() {
  console.log("called");
  const [selected, setSelected] = useState(people[0])
  const [query, setQuery] = useState('')

  const filteredPeople =
    query === ''
      ? people
      : people.filter((person) =>
          person.name
            .toLowerCase()
            .replace(/\s+/g, '')
            .includes(query.toLowerCase().replace(/\s+/g, ''))
        )

  return (
    <div className="temp-container">
        <Combobox value={selected} onChange={setSelected}>
            <div className="dropdown-container">
              <Combobox.Input
                className="w-full border-none py-2 pl-3 pr-10 text-sm leading-5 text-gray-500 focus:ring-0"
                displayValue={(person) => person.name}
                onChange={(event) => setQuery(event.target.value)}
              />
              <Combobox.Button className="button">
                <ChevronUpDownIcon className="h-5 w-5"/>
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
              afterLeave={() => setQuery('')}
            >
              <Combobox.Options className="absolute mt-1 max-h-60 w-full overflow-auto rounded-md bg-white py-1 text-base shadow-lg ring-1 ring-black/5 focus:outline-none sm:text-sm">
                {filteredPeople.length === 0 && query !== '' ? (
                  <div className="relative cursor-default select-none px-4 py-2 text-gray-500">
                    Nothing found.
                  </div>
                ) : (
                  filteredPeople.map((person) => (
                    <Combobox.Option
                      key={person.id}
                      className={({ active }) =>
                        `relative cursor-default select-none py-2 pl-10 pr-4 ${
                          active ? 'bg-gray-500 text-white' : 'text-black'
                        }`
                      }
                      value={person}
                    >
                      {({ selected, active }) => (
                        <>
                          <span
                            className={`block truncate ${
                              selected ? 'font-medium' : 'font-normal'
                            }`}
                          >
                            {person.name}
                          </span>
                          {selected ? (
                            <span
                              className={`absolute inset-y-0 left-0 flex items-center pl-3 ${
                                active ? 'text-white' : 'text-teal-600'
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
  )
}


function Review() {
  const navigate = useNavigate();
  
function programs() {

  const totalPrograms: ReactNode[] = [];
    
  ProgramData.forEach((program) => {
    totalPrograms.push(  
      <option value={program.id}>
      {program.name}
      </option>
    );
  });

  return totalPrograms;
}

  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>
      <p className="text-black"> Leave a Review </p>
      <button onClick={() => navigate(-1)}> Back </button>
      <div className="review">
        <Formik
          className="footer-content"
          initialValues={{
            program: "",
            friendliness: "",
            safety: "",
            queerSafety: "",
            climate: "",
            comment: "",
            overall: "",
          }}
          onSubmit={async (values) => {
            alert(JSON.stringify(values, null, 2));
          }}
        >
          {({ values, handleChange }) => (
            <Form className="">
              <Field name="program]">
              {() => (
                <div>
                  <div>
                    <h2 className="border border-white p-4">
                      What program is this review for?
                    </h2>
                        <div
                          className="flex flex-row items-center justify-center"
                        >
                          <div className="col">
                            <label>
                              Program
                            </label>
                            <Field as="select" name={`program`}>
                              {programs()}
                            </Field>
                          </div>
                        </div>
                  </div>
                </div>
              )}
            </Field>
              <h3>
                How accepting would you say the participants of this program and
                its surrounding communities are towards foreigners?
              </h3>
              <div role="group">
                <label>
                  1 (hostile)
                  <Field type="radio" name="friendliness" value="1" />
                </label>
                <Field type="radio" name="friendliness" value="2" />
                <Field type="radio" name="friendliness" value="3" />
                <Field type="radio" name="friendliness" value="4" />
                <label>
                  <Field type="radio" name="friendliness" value="5" />5
                  (accepting)
                </label>
              </div>

              <h3>
                How would you rate the safety of campus and its surrounding
                areas?
              </h3>
              <div role="group">
                <label>
                  1 (unsafe)
                  <Field type="radio" name="safety" value="1" />
                </label>
                <Field type="radio" name="safety" value="2" />
                <Field type="radio" name="safety" value="3" />
                <Field type="radio" name="safety" value="4" />
                <label>
                  <Field type="radio" name="safety" value="5" />5 (safe)
                </label>
              </div>

              <h3>
                How accepting was this program's community, as well as
                surrounding comminities, towards members of the LGBTQ+
                community?
              </h3>
              <div role="group">
                <label>
                  1 (hostile)
                  <Field type="radio" name="queerSafety" value="1" />
                </label>
                <Field type="radio" name="queerSafety" value="2" />
                <Field type="radio" name="queerSafety" value="3" />
                <Field type="radio" name="queerSafety" value="4" />
                <label>
                  <Field type="radio" name="queerSafety" value="5" />5
                  (accepting)
                </label>
              </div>

              <h3>
                How much did you learn at your program?
              </h3>
              <div role="group">
                <label>
                  1 (very unsatisfied)
                  <Field type="radio" name="climate" value="1" />
                </label>
                <Field type="radio" name="climate" value="2" />
                <Field type="radio" name="climate" value="3" />
                <Field type="radio" name="climate" value="4" />
                <label>
                  <Field type="radio" name="climate" value="5" />5 (very satisfied)
                </label>
              </div>
              
              {example()}

              <div className="flex items-center justify-center flex flex-col">
                Describe your experience:
                <Field
                  as="textarea"
                  name="comment"
                  onChange={handleChange}
                  className="comment-input"
                />
              </div>

              <h3>
                Overall, how likely are you to recommend this program to others?
              </h3>
              <div role="group">
                <label>
                  1 (unlikely)
                  <Field type="radio" name="overall" value="1" />
                </label>
                <Field type="radio" name="overall" value="2" />
                <Field type="radio" name="overall" value="3" />
                <Field type="radio" name="overall" value="4" />
                <label>
                  <Field type="radio" name="overall" value="5" />5 (very likely)
                </label>
              </div>

              <button type="submit" className="review-button">
                Submit
              </button>

            </Form>
          )}
          {/* </form> */}
        </Formik>
      </div>
    </div>
  );
}

export default Review;

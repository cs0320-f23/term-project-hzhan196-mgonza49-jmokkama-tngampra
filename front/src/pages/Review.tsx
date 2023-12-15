import React from "react";
import { ReactNode, Fragment, useState, useEffect } from "react";
import Navbar from "../components/Navbar"; 
import ProgramData from "../components/mockProgramData";
import { Link, useNavigate } from "react-router-dom";
import { Combobox, Transition } from '@headlessui/react'
import { CheckIcon, ChevronUpDownIcon } from '@heroicons/react/24/solid'
import {} from '@heroicons/react/24/outline'
import { ErrorMessage, Field, FieldArray, Form, Formik } from "formik";
import RatingButton from '../components/Radio'
import Popup from '../components/Popup'
import Divider from '@mui/material/Divider';


function dropdown({ data }) {
  useEffect(() => {
    // scroll to top
    window.scrollTo(0, 0);
  }, []);
  const [selected, setSelected] = useState(data[0])
  const [query, setQuery] = useState('')

  const filteredData =
    query === ''
      ? data
      : data.filter((item) =>
          item.name
            .toLowerCase()
            .replace(/\s+/g, '')
            .includes(query.toLowerCase().replace(/\s+/g, ''))
        )

  return (
    <div className="dropdown-wrap">
        <Combobox value={selected} onChange={setSelected}>
            <div className="dropdown-container">
              <Combobox.Input
                className="w-full py-2 pl-3 pr-10 black-text"
                displayValue={(item) => item.name}
                onChange={(event) => setQuery(event.target.value)}
              />
              <Combobox.Button className="icon-holder">
                <ChevronUpDownIcon className="h-6 w-5 block"/>
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
              <Combobox.Options className="dropdown-window">
                {filteredData.length === 0 && query !== '' ? (
                  <div className="relative cursor-default select-none px-4 py-2 text-gray-500">
                    Nothing found.
                  </div>
                ) : (
                  filteredData.map((item) => (
                    <Combobox.Option
                      key={item.id}
                      className={({ active }) =>
                        `relative cursor-default select-none py-2 pl-10 pr-4 ${
                          active ? 'bg-gray-500 text-white' : 'black-text'
                        }`
                      }
                      value={item}
                    >
                      {({ selected, active }) => (
                        <>
                          <span
                            className={`block truncate ${
                              selected ? 'font-medium' : 'font-normal'
                            }`}
                          >
                            {item.name}
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

      {/* <div className="review-top-panel"> </div> */}
      <div className="review">

      <button style={{
                height: '5vh',
                width: '8vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginTop: '4vh',
                marginBottom: '1vh',
                }} onClick={() => navigate(-1)}> Back </button>
        <h1 className="review-title"> Leave a Review </h1>
          

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
              <Field name="program">
              {() => (
                <div>
                   <Divider sx={{ height: 2, backgroundColor: 'gray'}} />
                  <div>
                    <h2 className="review-question"> - What program is this review for? </h2> 
                            <div>
                            {dropdown({data: ProgramData })}
                            </div>

                  </div>
                </div>
              )}
            </Field>
              <Divider sx={{ height: 20}} />
              <h3 className="review-question">
                - How accepting would you say the participants of this program and
                its surrounding communities are towards foreigners?
              </h3>
              <div role="group">
              <RatingButton first="Not Accepting" last="Very Accepting" />
                {/* <label>
                  1 (hostile)
                  <Field type="radio" name="friendliness" value="1" />
                </label>
                <Field type="radio" name="friendliness" value="2" />
                <Field type="radio" name="friendliness" value="3" />
                <Field type="radio" name="friendliness" value="4" />
                <label>
                  <Field type="radio" name="friendliness" value="5" />5
                  (accepting)
                </label> */}
              </div>

              <Divider sx={{ height: 20}} />
              <h3 className="review-question">
                - How would you rate the safety of campus and its surrounding
                areas?
              </h3>
              <div role="group">
              <RatingButton first="Unsafe" last="Safe" />
                {/* <label>
                  1 (unsafe)
                  <Field type="radio" name="safety" value="1" />
                </label>
                <Field type="radio" name="safety" value="2" />
                <Field type="radio" name="safety" value="3" />
                <Field type="radio" name="safety" value="4" />
                <label>
                  <Field type="radio" name="safety" value="5" />5 (safe)
                </label> */}
              </div>
              
              <Divider sx={{ height: 20}} />
              <h3 className="review-question">
                - How accepting was this program's community, as well as
                surrounding comminities, towards members of the LGBTQ+
                community?
              </h3>
              
              <div role="group">
              <RatingButton first="Not Accepting" last="Very Accepting" />
                {/* <label>
                  1 (hostile)
                  <Field type="radio" name="queerSafety" value="1" />
                </label>
                <Field type="radio" name="queerSafety" value="2" />
                <Field type="radio" name="queerSafety" value="3" />
                <Field type="radio" name="queerSafety" value="4" />
                <label>
                  <Field type="radio" name="queerSafety" value="5" />5
                  (accepting)
                </label> */}
              </div>

              <Divider sx={{ height: 20}} />
              <h3 className="review-question">
                - How did you feel about the quality of education and learning at this program?
              </h3>
              <div className="review-question" role="group">
              <RatingButton first="Very Unsatisfied" last="Very Satisfied" />
                {/* <label>
                  1 (very unsatisfied)
                  <Field type="radio" name="climate" value="1" />
                </label>
                <Field type="radio" name="climate" value="2" />
                <Field type="radio" name="climate" value="3" />
                <Field type="radio" name="climate" value="4" />
                <label>
                  <Field type="radio" name="climate" value="5" />5 (very satisfied)
                </label> */}
              </div>
              
              <Divider sx={{ height: 20}} />
              <div className="review-question">
                - Describe your experience or add any tips for prospective students:
                <Field
                  as="textarea"
                  name="comment"
                  onChange={handleChange}
                  className="comment-input"
                />
              </div>

              <Divider sx={{ height: 20}} />
              <h3 className="review-question">
                - Overall, how likely are you to recommend this program to others?
              </h3>
              <div role="group">
              <RatingButton first="Unlikely" last="Very Likely" />
                {/* <label>
                  1 (unlikely)
                  <Field type="radio" name="overall" value="1" />
                </label>
                <Field type="radio" name="overall" value="2" />
                <Field type="radio" name="overall" value="3" />
                <Field type="radio" name="overall" value="4" />
                <label>
                  <Field type="radio" name="overall" value="5" />5 (very likely)
                </label> */}
              </div>

              <Popup message={"Thank you for submitting a review!"}/>

            </Form>
          )}
          {/* </form> */}
        </Formik>
      </div>
      <div> 
        
      </div>
    </div>
  );
}

export default Review;

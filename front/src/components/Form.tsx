import React, { useEffect, useState, Fragment } from "react";
import { Combobox, Transition } from '@headlessui/react'
import { CheckIcon, ChevronUpDownIcon } from '@heroicons/react/20/solid'
import {} from '@heroicons/react/24/outline'
import { useFormik, Field, Formik, Form, FieldArray } from "formik";
import "../style/interface.css";
import { loginStatus } from "./Login";
import Checkbox from "../components/CheckboxDropdown";
import ProgramData from "../components/mockProgramData";
import Popup from "./Popup";
import Divider from '@mui/material/Divider';

const tempData = [
    "program1", "languages idk", "countries idk"
];

function formAccess() {
  const [commentStatus, setCommentStatus] = useState<Boolean>();
  useEffect(() => {
    loginStatus()
      .then((name) => {
        if (name === "Sign Out") {
          setCommentStatus(true);
        } else {
          setCommentStatus(false);
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);
  return commentStatus;
}

function expandedForm(isExpanded: boolean) {
  if (isExpanded) {
    return (
      <div className="">
        <div className="bold-text"> Preferences Form </div>

      <Formik
        className="footer-content"
        initialValues={{
          languages: [],
          country: "",
          program: "",
          duration: "",
          countryBlacklist: [],
          programBlacklist: [],
          ranking:[{id: 1, name: "safety"}, {id: 2, name: "friendliness"}, {id: 3, name: "lgbt-acceptance"}, {id: 4, name: "education-quality"}]
        }}
        onSubmit={async (values) => {
          alert(JSON.stringify(values, null, 2));
        }}
      >
        

        {({ values, handleChange }) => (
          <Form>
            <FieldArray name="languages">
              {({ insert, remove, push }) => (
                <div role="group">
                  <div>
                        
                    <h2 className="border border-white p-4">
                      1. What languages do you currently speak?
                    </h2>

                    <div className="flex items-center justify-center">
                      <Checkbox data={tempData} placeholder="Enter Languages" />
                    </div>
 
                    {values.languages.length > 0 &&
                      values.languages.map((language, index) => (
                        <div
                          className="flex flex-row items-center justify-center"
                          key={index}
                        >
                          <div className="col">
                          
                            <label htmlFor={`languages.${index}.name`}>
                              Language
                            </label>
                            <Field as="select" name={`languages.${index}.name`}>
                              <option value="English">English</option>
                              <option value="Mandarin">Mandarin</option>
                              <option value="Tagalog">Tagalog</option>
                            </Field>
                          </div>
                          <div className="col">
                            <button
                              type="button"
                              className="review-button"
                              onClick={() => remove(index)}
                            >
                              X
                            </button>
                          </div>
                        </div>
                      ))}
                  </div>

                  <button
                    type="button"
                    className="review-button"
                    onClick={() => push({ name: ""})}
                  >
                    Add Language
                  </button>
                </div>
              )}
            </FieldArray>

            <FieldArray name="countryBlacklist">
              {({ insert, remove, push }) => (
                <div role="group">
                  <div>
                    <h2 className="border border-white p-4">
                      What countries are you NOT interested in going to?
                    </h2>
                    {values.countryBlacklist.length > 0 &&
                      values.countryBlacklist.map((country, index) => (
                        <div
                          className="flex flex-row items-center justify-center"
                          key={index}
                        >
                          <div className="col">
                            <label htmlFor={`countryBlacklist.${index}.name`}>
                              Country
                            </label>
                            <Field
                              as="select"
                              name={`countryBlacklist.${index}.name`}
                              placeholder="Country"
                            >
                              <option value="USA">USA</option>
                              <option value="Canada">Canada</option>
                              <option value="UK">UK</option>
                            </Field>
                          </div>
                          <div className="col">
                            <button
                              type="button"
                              className="review-button"
                              onClick={() => remove(index)}
                            >
                              X
                            </button>
                          </div>
                        </div>
                      ))}
                  </div>

                  <button
                    type="button"
                    className="review-button"
                    onClick={() => push({ name: "", email: "" })}
                  >
                    Add Country
                  </button>
                </div>
              )}
            </FieldArray>

            <FieldArray name="programBlacklist">
              {({ insert, remove, push }) => (
                <div role="group">
                  <div>
                    <h2 className="border border-white p-4">
                      Are there any programs that you've looked at and decided
                      are not for you?
                    </h2>
                    {values.programBlacklist.length > 0 &&
                      values.programBlacklist.map((program, index) => (
                        <div
                          className="flex flex-row items-center justify-center"
                          key={index}
                        >
                          <div className="col">
                            <label htmlFor={`programBlacklist.${index}.name`}>
                              Program
                            </label>
                            <Field
                              as="select"
                              name={`programBlacklist.${index}.name`}
                              placeholder="Program"
                            >
                              <option value="Ben-Gurion University">
                                Ben-Gurion University
                              </option>
                              <option value="Another program">
                                Something else
                              </option>
                            </Field>
                          </div>
                          <div className="col">
                            <button
                              type="button"
                              className="review-button"
                              onClick={() => remove(index)}
                            >
                              X
                            </button>
                          </div>
                        </div>
                      ))}
                  </div>

                  <button
                    type="button"
                    className="review-button"
                    onClick={() => push({ name: "", email: "" })}
                  >
                    Add Program
                  </button>
                </div>
              )}
            </FieldArray>
<h3>Please rank the following aspects in order of how important they are to you:</h3>
                          <h3>
                How accepting the communities are
              </h3>
              <div role="group">
                <label>
                  (least important) 1
                  <Field type="radio" name="friendliness" value="1" />
                </label>
                <Field type="radio" name="friendliness" value="2" />
                <Field type="radio" name="friendliness" value="3" />
                <Field type="radio" name="friendliness" value="4" />
                <label>
                  <Field type="radio" name="friendliness" value="5" />5
                  (most important)
                </label>
              </div>              <h3>
                Overall safety of surrounding area
              </h3>
              <div role="group">
                <label>
                  1
                  <Field type="radio" name="friendliness" value="1" />
                </label>
                <Field type="radio" name="friendliness" value="2" />
                <Field type="radio" name="friendliness" value="3" />
                <Field type="radio" name="friendliness" value="4" />
                <label>
                  <Field type="radio" name="friendliness" value="5" />5
                </label>
              </div>              <h3>
                How accepting would you say the participants of this program and
                its surrounding communities are towards foreigners?
              </h3>
              <div role="group">
                <label>
                  1
                  <Field type="radio" name="friendliness" value="1" />
                </label>
                <Field type="radio" name="friendliness" value="2" />
                <Field type="radio" name="friendliness" value="3" />
                <Field type="radio" name="friendliness" value="4" />
                <label>
                  <Field type="radio" name="friendliness" value="5" />5
                </label>
              </div>              <h3>
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
              </div>              <h3>
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

            {/* <button className="review-button" type="submit">Submit</button> */}
            <Popup message={"Thank you for submitting your program preferences!"}/>
          </Form>
        )}
        {/* </form> */}
      </Formik>
      </div>
    );
  } else {
    return (
      <div className="footer-content">
        {/* Want to be matched with your ideal program? */}
      </div>
    );
  }
}

export const forms = () => {
  const [expanded, setExpanded] = useState(false);
  function label() {
    if (expanded == false) {
      return <span className="expand-label"> Click to match with your ideal program! </span>;
    } else {
      return <span className="expand-label2">–</span>;
    }
  }
  function expand() {
    setExpanded(!expanded);
  }
  if (formAccess()) {
    return (
      <div className={"footer" + (expanded ? " expanded" : "")}>
        <div className="footer-content">
          <button className="expand" onClick={expand}>
            <span className="expand-label">{label()}</span>
          </button>
          {expandedForm(expanded)}
        </div>
      </div>
    );
  } else {
    return <div></div>;
  }
};

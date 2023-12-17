import React, { useEffect, useState, Fragment } from "react";
import { Combobox, RadioGroup, Transition } from "@headlessui/react";
import { CheckIcon, ChevronUpDownIcon } from "@heroicons/react/20/solid";
import {} from "@heroicons/react/24/outline";
import { useFormik, Field, Formik, Form, FieldArray } from "formik";
import "../style/interface.css";
import { loginStatus } from "./Login";
import Checkbox from "../components/CheckboxDropdown";
import ProgramData from "../components/mockProgramData";
import Popup from "./Popup";
import Divider from "@mui/material/Divider";
import Radio2 from "../components/Radio2";
import FormControlLabel from "@mui/material/FormControlLabel";
import Radio from "./Radio";

const tempData = ["program1", "languages idk", "countries idk"];

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
            countryBlacklist: [],
            programBlacklist: [],
            friendliness: "",
            safety: "",
            lgbtAcceptance: "",
            educationQuality: "",
          }}
          onSubmit={async (values) => {
            alert(JSON.stringify(values, null, 2));
          }}
        >
          {() => (
            <Form>
              {/* <FieldArray name="languages">
                {() => (
                  <div role="group">
                    <div>
                      <h2 style={{marginTop: "3vh", marginBottom: "1vh",}}>
                        1. What languages do you currently speak?
                      </h2>
                      <Divider sx={{ height: 0, backgroundColor: "white", marginBottom: '3vh'}} />

                      <div className="flex items-center justify-center">
                        <Checkbox
                          data={tempData}
                          placeholder="Enter Languages"
                        />
                      </div>
                    </div>
                  </div>
                )}
              </FieldArray> */}
              <FieldArray name="countryBlacklist">
                {({ form, push }) => (
                  <div role="group">
                    <div>
                      <h2 style={{ marginTop: "3vh", marginBottom: "1vh" }}>
                        1. What countries do you NOT want to go to?
                      </h2>
                      <Divider
                        sx={{
                          height: 0,
                          backgroundColor: "white",
                          marginBottom: "3vh",
                        }}
                      />

                      <div className="flex items-center justify-center">
                        <Checkbox
                          data={tempData}
                          placeholder="Enter Countries"
                          name="countryBlacklist"
                          onChange={(selectedValues: string[]) => {
                            const newValues = selectedValues.filter(
                              (newValue) =>
                                !form.values.countryBlacklist.includes(newValue)
                            );
                            if (newValues.length > 0) {
                              newValues.forEach((item: string) => {
                                push(item);
                              });
                            }
                          }}
                        />
                      </div>
                    </div>
                  </div>
                )}
              </FieldArray>
              <FieldArray name="programBlacklist">
                {({ form, push }) => (
                  <div role="group">
                    <div>
                      <h2 style={{ marginTop: "3vh", marginBottom: "1vh" }}>
                        2. What programs do you NOT want to do?
                      </h2>
                      <Divider
                        sx={{
                          height: 0,
                          backgroundColor: "white",
                          marginBottom: "3vh",
                        }}
                      />
                      <div className="flex items-center justify-center">
                        <Checkbox
                          data={tempData}
                          placeholder="Enter Programs"
                          name="programBlacklist"
                          onChange={(selectedValues: string[]) => {
                            const newValues = selectedValues.filter(
                              (newValue) =>
                                !form.values.programBlacklist.includes(newValue)
                            );
                            if (newValues.length > 0) {
                              newValues.forEach((item: string) => {
                                push(item);
                              });
                            }
                          }}
                        />
                      </div>
                    </div>
                  </div>
                )}
              </FieldArray>
              <h3
                className="border border-white p-4"
                style={{ marginTop: "3vh", marginBottom: "3vh" }}
              >
                3. Please rank the following aspects in order of how important
                they are to you:
              </h3>
              <h3>How accepting the communities are</h3>
              <div role="group">
                <Radio2
                  first={"Least important"}
                  last={"Most important"}
                  name={"friendliness"}
                />
                {/* <label>
                  (least important) 1
                  <Field type="radio" name="friendliness" value="1" />
                </label>
                <Field type="radio" name="friendliness" value="2" />
                <Field type="radio" name="friendliness" value="3" />
                <label>
                  <Field type="radio" name="friendliness" value="4" />4 (most
                  important)
                </label> */}
              </div>{" "}
              <Divider
                sx={{
                  height: 0,
                  backgroundColor: "white",
                  marginBottom: "3vh",
                }}
              />
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "center",
                  alignItems: "center",
                }}
                role="group"
              >
                <h3>Overall safety of surrounding area</h3>
                <Radio2
                  first={"Least important"}
                  last={"Most important"}
                  name={"safety"}
                />
                {/* <Field type="radio" name="safety" value="1" />
                  <Field type="radio" name="safety" value="2" />
                  <Field type="radio" name="safety" value="3" />
                  <Field type="radio" name="safety" value="4" /> */}
              </div>{" "}
              <Divider
                sx={{
                  height: 0,
                  backgroundColor: "white",
                  marginBottom: "3vh",
                }}
              />
              <h3>Accepting towards minority groups</h3>
              <div role="group">
                <Radio2
                  first={"Least important"}
                  last={"Most important"}
                  name={"lgbtAcceptance"}
                />
                {/* <Field type="radio" name="lgbt-acceptance" value="1" />
                <Field type="radio" name="lgbt-acceptance" value="2" />
                <Field type="radio" name="lgbt-acceptance" value="3" />
                <Field type="radio" name="lgbt-acceptance" value="4" /> */}
              </div>{" "}
              <Divider
                sx={{
                  height: 0,
                  backgroundColor: "white",
                  marginBottom: "3vh",
                }}
              />
              <h3>Quality of education</h3>
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "center",
                  alignItems: "center",
                }}
                role="group"
              >
                <Radio2
                  first={"Least important"}
                  last={"Most important"}
                  name={"educationQuality"}
                />
                {/* <Field type="radio" name="education-quality" value="1" />
                <Field type="radio" name="education-quality" value="2" />
                <Field type="radio" name="education-quality" value="3" />
                <Field type="radio" name="education-quality" value="4" /> */}
                <Popup
                  message={"Thank you for submitting your program preferences!"}
                />
              </div>{" "}
            </Form>
          )}
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
      return (
        <span className="expand-label underlined-text">
          {" "}
          Click to match with your ideal program!{" "}
        </span>
      );
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

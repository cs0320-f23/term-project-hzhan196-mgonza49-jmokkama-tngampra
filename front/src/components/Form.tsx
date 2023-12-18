import React, { useEffect, useState, Fragment } from "react";
import { Combobox, RadioGroup, Transition } from "@headlessui/react";
import { CheckIcon, ChevronUpDownIcon } from "@heroicons/react/20/solid";
import {} from "@heroicons/react/24/outline";
import { useFormik, Field, Formik, Form, FieldArray, isString } from "formik";
import "../style/interface.css";
import { loginStatus, profileEmail, profileName } from "./Login";
import Checkbox from "../components/CheckboxDropdown";
import ProgramData from "../components/mockProgramData";
import Popup from "./Popup";
import Divider from "@mui/material/Divider";
import Radio2 from "../components/Radio2";
import FormControlLabel from "@mui/material/FormControlLabel";
import Radio from "./Radio";

const tempData = ["program1", "languages idk", "countries idk"];

function userCounted(email: string): Promise<boolean> {
  const url = "http://localhost:3232/checkuser?email=" + email;
  return fetch(url)
    .then((res) => {
      if (!res.ok) {
        return Promise.reject(false);
      }
      return res.json();
    })
    .then((res) => checkUser(res))
    .catch((error) => {
      return Promise.reject(false);
    });
}

function checkUser(res: any): Promise<boolean> {
  return Promise.resolve(res.isMember);
}

function getProfileEmail(): string {
  const [email, setEmail] = useState<string>("");
  useEffect(() => {
    profileEmail().then((name) => {
      setEmail(name);
    });
  });
  return email;
}

function getProfileName(): string {
  const [name, setName] = useState<string>("");
  useEffect(() => {
    profileName().then((name) => {
      setName(name);
    });
  });
  return name;
}

function notTakenForm(): boolean {
  const [hasNotTakenForm, setHasNotTakenForm] = useState<boolean>(false);
  useEffect(() => {
    userCounted(getProfileEmail()).then((hasTaken) => {
      setHasNotTakenForm(hasTaken);
    });
  });
  return hasNotTakenForm;
}

function formAccess() {
  const [commentStatus, setCommentStatus] = useState<Boolean>();
  useEffect(() => {
    loginStatus()
      .then((name) => {
        if (name === "Sign Out") {
          setCommentStatus(true);
        } else if (notTakenForm()) {
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

function handleSubmit(
  languages: string[],
  countryBlacklist: string[],
  programBlacklist: string[],
  friendliness: string,
  safety: string,
  minorityAcceptance: string,
  educationQuality: string
) {
  const countries: string[] = [""];
  const programs: string[] = [""];
  const myLanguages: string[] = [""];

  languages.forEach((language, index) => {
    const isLast = index === languages.length - 1;
    if (isLast) {
      myLanguages.push(language);
    } else {
      myLanguages.push(language + "~");
    }
  });

  countryBlacklist.forEach((country, index) => {
    const isLast = index === countryBlacklist.length - 1;
    if (isLast) {
      countries.push(country);
    } else {
      countries.push(country + "~");
    }
  });
  programBlacklist.forEach((program, index) => {
    const isLast = index === programBlacklist.length - 1;
    if (isLast) {
      programs.push(program);
    } else {
      programs.push(program + "~");
    }
  });
  let ranking: number[] = [
    parseInt(friendliness),
    parseInt(safety),
    parseInt(minorityAcceptance),
    parseInt(educationQuality),
  ];
  ranking.sort((a, b) => a - b).reverse();
  const actualRanking: string[] = [];
  ranking.forEach((item, index) => {
    const isLast = index === 3;
    const itemString = item.toString();
    if (isLast) {
      actualRanking.push(itemString);
    } else {
      actualRanking.push(itemString + "~");
    }
  });
  const url =
    "http://localhost:3232/adduser?username=" +
    getProfileName +
    "&email=" +
    getProfileEmail +
    "&languages=" +
    myLanguages.toString() +
    "&countries=" +
    countries.toString() +
    "&programs=" +
    programs.toString() +
    "&ranking=" +
    actualRanking.toString();
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
            countryBlacklist: [],
            programBlacklist: [],
            friendliness: "",
            safety: "",
            lgbtAcceptance: "",
            educationQuality: "",
          }}
          onSubmit={async (values) => {
            handleSubmit(
              values.languages,
              values.countryBlacklist,
              values.programBlacklist,
              values.friendliness,
              values.safety,
              values.lgbtAcceptance,
              values.educationQuality
            );
          }}
        >
          {() => (
            <Form>
              <FieldArray name="languages">
                {({ form, push }) => (
                  <div role="group">
                    <div>
                      <h2 style={{ marginTop: "3vh", marginBottom: "1vh" }}>
                        1. What languages do you currently speak?
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
                          placeholder="Enter Languages"
                          name="languages"
                          onChange={(selectedValues: string[]) => {
                            const newValues = selectedValues.filter(
                              (newValue) =>
                                !form.values.languages.includes(newValue)
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

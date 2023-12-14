import React, { useEffect, useState } from "react";
import { useFormik, Field, Formik, Form, FieldArray } from "formik";
import "../style/interface.css";
import { loginStatus } from "./Login";

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
      <Formik
        className="footer-content"
        initialValues={{
          country: "",
          program: "",
          duration: "",
          countryBlacklist: [],
          programBlacklist: [],
        }}
        onSubmit={async (values) => {
          alert(JSON.stringify(values, null, 2));
        }}
      >
        {({ values, handleChange }) => (
          <Form>
            <label htmlFor="country">Country</label>
            <input
              id="country"
              name="country"
              type="country"
              onChange={handleChange}
              value={values.country}
            />
            <label htmlFor="program">Program</label>
            <input
              id="program"
              name="program"
              type="program"
              onChange={handleChange}
              value={values.program}
            />
            <div>Duration</div>
            <div role="group">
              <label>
                <Field type="radio" name="duration" value="Semester" />
                Semester
              </label>
              <label>
                <Field type="radio" name="duration" value="Full Year" />
                Full Year
              </label>
            </div>

            <FieldArray name="countryBlacklist">
              {({ insert, remove, push }) => (
                <div>
                  <h3>What countries are you uninterested in going to?</h3>
                  {values.countryBlacklist.length > 0 &&
                    values.countryBlacklist.map((country, index) => (
                      <div className="flex flex-row" key={index}>
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

            <button type="submit">Submit</button>
          </Form>
        )}
        {/* </form> */}
      </Formik>
    );
  } else {
    return (
      <div className="footer-content">
        Want to be matched with your ideal program?
      </div>
    );
  }
}

export const forms = () => {
  const [expanded, setExpanded] = useState(false);
  function label() {
    if (expanded == false) {
      return <span className="expand-label">^</span>;
    } else {
      return <span className="expand-label">–</span>;
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

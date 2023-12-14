import React from "react";
import { ReactNode } from "react";
import Navbar from "../components/Navbar";
import Search from "../components/Search";
import ProgramData from "../components/mockProgramData";
import { Link, useParams, Outlet, useNavigate } from "react-router-dom";
import "../style/interface.css";
import { ErrorMessage, Field, FieldArray, Form, Formik } from "formik";

function Review() {
  return (
    <div>
      <div className="navbar-container">
        <Navbar />
        Leave a Review
      </div>
      <div className="review">
        <Formik
          className="footer-content"
          initialValues={{
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
            <Form>
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
                In relation to Providence, would you say that the average
                temperature was colder or warmer?
              </h3>
              <div role="group">
                <label>
                  1 (much colder)
                  <Field type="radio" name="climate" value="1" />
                </label>
                <Field type="radio" name="climate" value="2" />
                <Field type="radio" name="climate" value="3" />
                <Field type="radio" name="climate" value="4" />
                <label>
                  <Field type="radio" name="climate" value="5" />5 (much warmer)
                </label>
              </div>

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

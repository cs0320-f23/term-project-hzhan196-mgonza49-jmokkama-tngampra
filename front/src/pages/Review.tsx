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
            safety: "",
            comment: "",
          }}
          onSubmit={async (values) => {
            alert(JSON.stringify(values, null, 2));
          }}
        >
          {({ values, handleChange }) => (
            <Form>
              <h3>
                How would you rate the safety of campus and its surrounding
                areas?
              </h3>
              <div role="group">
                <label>
                  1
                  <Field type="radio" name="safety" value="1" />
                </label>
                <Field type="radio" name="safety" value="2" />
                <Field type="radio" name="safety" value="3" />
                <Field type="radio" name="safety" value="4" />
                <label>
                  <Field type="radio" name="safety" value="5" />5
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

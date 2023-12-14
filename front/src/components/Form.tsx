import React from "react";
import { useFormik, Field, Formik, Form } from "formik";
import "../style/interface.css";

export const forms = () => {
  //   const formik = useFormik({
  //     initialValues: {
  //       country: "",
  //       program: "",
  //       duration: "",
  //     },
  //     onSubmit: (values) => {
  //       alert(JSON.stringify(values, null, 2));
  //     },
  //   });
  //   return (
  // <form onSubmit={formik.handleSubmit}>
  return (
    <div className="footer">
      <div className="footer-content">
        <div>Form</div>
        <button className="expand">
          <span className="expand-label">^</span>
        </button>
        <Formik
          initialValues={{
            country: "",
            program: "",
            duration: "",
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

              <button type="submit">Submit</button>
            </Form>
          )}
          {/* </form> */}
        </Formik>
      </div>
    </div>
  );
};

import React from "react";
import { useState, useEffect } from "react";
import Navbar from "../components/Navbar";
// import ProgramData from "../components/mockProgramData";
import { useNavigate } from "react-router-dom";
import {} from "@heroicons/react/24/outline";
import { Field, Form, Formik } from "formik";
import RatingButton from "../components/Radio";
import Divider from "@mui/material/Divider";
import Dropdown, { DropdownProps } from "../components/Dropdown";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import Dialog from "@mui/material/Dialog";
import { profileName } from "../components/Login";
import { ActualProgram } from "./Homepage";

function Review() {
  const [data, setData] = useState<DropdownProps[]>([]);
  const [name, setName] = useState<string>("");
  useEffect(() => {
    profileName().then((name) => {
      setName(name);
    });
  });

  function handleSubmit(
    program: string,
    friendliness: string,
    safety: string,
    queerSafety: string,
    education: string,
    comment: string,
    overall: string
  ) {
    const url =
      "http://localhost:3232/programform?program=" +
      program +
      "&username=" +
      name +
      "&acceptance=" +
      friendliness +
      "&safety=" +
      safety +
      "&min=" +
      queerSafety +
      "&learning=" +
      education +
      "&overall=" +
      overall +
      "&comment=" +
      comment;
    return fetch(url)
      .then((res) => {
        if (!res.ok) {
          return Promise.reject("Error");
        }
        return Promise.resolve("Success");
      })
      .catch((error) => {
        console.error(error);
        return Promise.resolve(error.message);
      });
  }

  function toProgram(res: any) {
    const programArray: DropdownProps[] = [];
    if (res.result === "success") {
      const programs: any = res.data;
      programs.forEach((program: ActualProgram, index: number) => {
        const id = index + 1;
        programArray.push({
          id: id,
          name: program.name,
        });
      });
    }
    return programArray;
  }

  useEffect(() => {
    async function fetchPrograms() {
      const url = "http://localhost:3232/viewdata";
      try {
        const res = await fetch(url);
        if (!res.ok) {
          return Promise.reject("Error");
        }
        const jsonData = await res.json();

        const programs = toProgram(jsonData);
        setData(programs);
      } catch (error) {
        console.error("Error fetching programs:", error);
      }
    }

    fetchPrograms();
  }, []);

  const navigate = useNavigate();

  //---popup stuff
  const [open, setOpen] = React.useState(false);
  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
    window.location.reload();
    navigate(-1);
  };
  //---

  const [selectedProgram, setSelectedProgram] = useState<DropdownProps | null>(
    null
  );

  return (
    <div>
      <div id="navbar" className="navbar-container">
        <Navbar />
      </div>

      {/* <div className="review-top-panel"> </div> */}
      <div className="review">
        <button
          style={{
            height: "5vh",
            width: "8vh",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            marginTop: "4vh",
            marginBottom: "1vh",
          }}
          onClick={() => navigate(-1)}
        >
          {" "}
          Back{" "}
        </button>
        <h1 className="review-title"> Leave a Review </h1>

        <Formik
          className="footer-content"
          initialValues={{
            program: selectedProgram ? selectedProgram.name : "",
            friendliness: "",
            safety: "",
            queerSafety: "",
            education: "",
            comment: "",
            overall: "",
          }}
          onSubmit={async (values) => {
            handleSubmit(
              values.program,
              values.friendliness,
              values.safety,
              values.queerSafety,
              values.education,
              values.comment,
              values.overall
            );
          }}
        >
          {({ handleChange, setFieldValue }) => (
            <Form className="">
              <Field name="program">
                {() => (
                  <div>
                    <Divider sx={{ height: 2, backgroundColor: "gray" }} />
                    <div>
                      <h2 className="review-question">
                        {" "}
                        - What program is this review for?{" "}
                      </h2>
                      <div>
                        <Dropdown
                          data={data}
                          onSelect={(selected) => {
                            setSelectedProgram(selected);
                            setFieldValue("program", selected.name);
                          }}
                        />
                      </div>
                      {/* <div>{dropdown({ data: ProgramData, onSelect: {(selected)} =>  })}</div> */}
                    </div>
                  </div>
                )}
              </Field>
              <Divider sx={{ height: 20 }} />
              <h3 className="review-question">
                - How accepting would you say the participants of this program
                and its surrounding communities are towards foreigners?
              </h3>
              <div role="group">
                <RatingButton
                  first="Not Accepting"
                  last="Very Accepting"
                  name="friendliness"
                />
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

              <Divider sx={{ height: 20 }} />
              <h3 className="review-question">
                - How would you rate the safety of campus and its surrounding
                areas?
              </h3>
              <div role="group">
                <RatingButton first="Unsafe" last="Safe" name="safety" />
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

              <Divider sx={{ height: 20 }} />
              <h3 className="review-question">
                - How accepting was this program's community, as well as
                surrounding comminities, towards members of minority groups?
              </h3>

              <div role="group">
                <RatingButton
                  first="Not Accepting"
                  last="Very Accepting"
                  name="queerSafety"
                />
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

              <Divider sx={{ height: 20 }} />
              <h3 className="review-question">
                - How did you feel about the quality of education and learning
                at this program?
              </h3>
              <div className="review-question" role="group">
                <RatingButton
                  first="Very Unsatisfied"
                  last="Very Satisfied"
                  name="education"
                />
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

              <Divider sx={{ height: 20 }} />
              <div className="review-question">
                - Describe your experience or add any tips for prospective
                students:
                <Field
                  as="textarea"
                  name="comment"
                  onChange={handleChange}
                  className="comment-input"
                />
              </div>

              <Divider sx={{ height: 20 }} />
              <h3 className="review-question">
                - Overall, what would you rate your experience at this program?
              </h3>
              <div role="group">
                <RatingButton first="Bad" last="Great" name="overall" />
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

              {/* <Popup message={"Thank you for submitting a review!"} /> */}

              <React.Fragment>
                <button className="review-button" onClick={handleClickOpen}>
                  Submit
                </button>
                <Dialog
                  open={open}
                  onClose={handleClose}
                  aria-labelledby="alert-dialog-title"
                  aria-describedby="alert-dialog-description"
                  sx={{
                    "& .MuiDialog-paper": {
                      width: "40%",
                      height: "20%",
                      maxWidth: "none",
                    },
                  }}
                >
                  <DialogContent>
                    <DialogContentText
                      id="alert-dialog-description"
                      sx={{
                        display: "flex",
                        textAlign: "center",
                        justifyContent: "center",
                        fontSize: "1.3rem", // Adjust the font size as needed
                        color: "black",
                      }}
                    >
                      "Thank you for submitting a review!"
                    </DialogContentText>
                  </DialogContent>
                </Dialog>
              </React.Fragment>
            </Form>
          )}
          {/* </form> */}
        </Formik>
      </div>
      <div></div>
    </div>
  );
}

export default Review;

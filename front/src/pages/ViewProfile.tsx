import React, { useEffect, useState } from "react";
import { ReactNode } from "react";
import Navbar from "../components/Navbar";
import { profilePhoto, profileName, profileEmail } from "../components/Login";
import Search from "../components/Search";
import Icons from "../components/Icons";
import ProgramData from "../components/mockProgramData";
import { Link, useParams, Outlet, useNavigate } from "react-router-dom";
import "../style/interface.css";

function ViewProfile() {
  const [photoURL, setPhotoURL] = useState<string>("");
  useEffect(() => {
    profilePhoto()
      .then((url) => {
        if (url) {
          setPhotoURL(url);
        } else {
          setPhotoURL("error");
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  const [username, setUsername] = useState<string>("");
  useEffect(() => {
    profileName()
      .then((name) => {
        if (name) {
          setUsername(name);
        } else {
          setUsername("error");
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  const [email, setEmail] = useState<string>("");
  useEffect(() => {
    profileEmail()
      .then((address) => {
        if (address) {
          setEmail(address);
        } else {
          setEmail("error");
        }
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  return (
    <div>
      <div className="navbar-container">
        <Navbar />
      </div>
      <div className="profile-page">
        <img src={photoURL} className="profile-page-photo"></img>
        <h2 className="profile-page-color">Name: {username}</h2>
        <h3 className="profile-page-color">Email: {email}</h3>
      </div>
    </div>
  );
}

export default ViewProfile;

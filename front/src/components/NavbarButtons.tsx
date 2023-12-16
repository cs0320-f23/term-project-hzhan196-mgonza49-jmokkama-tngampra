import { Fragment, useState } from "react";
import { Disclosure, Menu, Transition } from "@headlessui/react";
import { BrowserRouter, Link, useNavigate } from "react-router-dom";
import defaultPhoto from "../assets/blank-profile.jpeg";
import "../style/interface.css";
import "../style/App.css";
import { login, logout, loginStatus, profilePhoto } from "./Login";
import { getAuth } from "firebase/auth";
import app from "./firebaseInit";
import { useEffect } from "react";
import React from "react";
import SignInPopup from "./SignInPopup";

export default function NavbarButtons() {
  const navigate = useNavigate();
  const [loginState, setLoginState] = useState("");

  const handleSignIn = async () => {
    const loggedIn = (await loginStatus()).toString();
    console.log("loggedIn " + loggedIn);
    if (loggedIn === "Sign In") {
      try {
        const loginStatus = (await login()).toString();
        if (loginStatus === "success") {
          setLoginState("Signed in successfully");
        }
      } catch (error) {
        console.error(error);
        setLoginState("Error: Please try again");
      }
    } else {
      try {
        const loginStatus = (await logout()).toString();
        console.log("login status " + loginStatus);
        if (loginStatus === "success") {
          setLoginState("Signed out successfully");
        }
      } catch (error) {
        console.error(error);
        setLoginState("Error: Please try again");
      }
    }
  };
  const [buttonText, setButtonText] = useState<String>("");
  useEffect(() => {
    loginStatus()
      .then((name) => {
        setButtonText(name);
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

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

  function photo(): string {
    if (photoURL === "error") {
      return defaultPhoto;
    } else {
      return photoURL;
    }
  }

  return (
    <Menu as="div">
      <Menu.Button
        className="ml-2 relative flex rounded-full custom-dark-gray focus:outline-none"
        // style={{ background: "transparent", border: "none" }}
      >
        <img className="profile-circle" src={photo()} alt="" />
      </Menu.Button>

      <Transition
        as={Fragment}
        enter="transition ease-out duration-100"
        enterFrom="transform opacity-0 scale-95"
        enterTo="transform opacity-100 scale-100"
        leave="transition ease-in duration-75"
        leaveFrom="transform opacity-100 scale-100"
        leaveTo="transform opacity-0 scale-95"
      >
        <Menu.Items className="absolute right-0 z-10 mt-2 w-40 origin-top-right rounded-md bg-white py-1 shadow-lg ring-1 ring-opacity-5 focus:outline-none">
          {/* <div className=""> */}
          <Menu.Item>
            {({ active }) => (
              <button
                className={`${
                  active ? "bg-gray-200" : "bg-white"
                } w-full text-left block px-4 py-2 text-gray-700`}
                // href="/settings"
                onClick={() => navigate("/profile")}
              >
                View Profile
              </button>
            )}
          </Menu.Item>
          <Menu.Item>
            {({ active }) => (
              <button
                onClick={handleSignIn}
                className={`${
                  active ? "bg-gray-200" : "bg-white"
                } w-full text-left block px-4 py-2 text-gray-700`}
              >
                {buttonText}
              </button>
            )}
          </Menu.Item>
        </Menu.Items>
      </Transition>
      <SignInPopup message={loginState} />
    </Menu>
  );
}

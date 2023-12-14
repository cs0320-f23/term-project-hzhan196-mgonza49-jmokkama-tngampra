import { Fragment, useState } from "react";
import { Disclosure, Menu, Transition } from "@headlessui/react";
import { BrowserRouter, Link } from "react-router-dom";
import { Bars3Icon, BellIcon, XMarkIcon } from "@heroicons/react/24/outline";
import "../style/interface.css";
import "../style/App.css";
import { login, logout, loginStatus } from "./Login";
import { getAuth } from "firebase/auth";
import app from "./firebaseInit";
import { useEffect } from "react";

export default function NavbarButtons() {
  const handleSignIn = async () => {
    const loggedIn = (await loginStatus()).toString();
    console.log("loggedIn " + loggedIn);
    if (loggedIn === "Sign In") {
      try {
        const loginStatus = (await login()).toString();
        console.log("login status " + loginStatus);
        if (loginStatus === "success") {
          console.log("Signed in successfully");
        }
      } catch (error) {
        console.error(error);
      }
    } else {
      try {
        const loginStatus = (await logout()).toString();
        console.log("login status " + loginStatus);
        if (loginStatus === "success") {
          console.log("Signed out successfully");
        }
      } catch (error) {
        console.error(error);
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

  return (
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
  );
}

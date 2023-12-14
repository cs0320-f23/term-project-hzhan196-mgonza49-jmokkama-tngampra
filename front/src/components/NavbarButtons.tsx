import { Fragment, useState } from "react";
import { Disclosure, Menu, Transition } from "@headlessui/react";
import { BrowserRouter, Link } from "react-router-dom";
import { Bars3Icon, BellIcon, XMarkIcon } from "@heroicons/react/24/outline";
import "../style/interface.css";
import "../style/App.css";
import { login, logout, loginStatus, profilePhoto } from "./Login";
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
      return "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80";
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
              >
                Settings
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
    </Menu>
  );
}

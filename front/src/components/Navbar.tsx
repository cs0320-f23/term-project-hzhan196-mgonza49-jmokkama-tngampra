"use client";
import { Fragment, useState } from "react";
import { Disclosure, Menu, Transition } from "@headlessui/react";
import { BrowserRouter, Link } from "react-router-dom";
import { Bars3Icon, BellIcon, XMarkIcon } from "@heroicons/react/24/outline";
import "../style/interface.css";
import "../style/App.css";
import { Login } from "./Login";

const navigation = [
  { name: "Home", href: "/", current: true },
  { name: "Browse", href: "/browse/", current: false },
];

export default function Navbar() {
  const handleSignIn = async () => {
    try {
      const loginStatus = (await Login()).toString();
      console.log("login status " + loginStatus);
      if (loginStatus === "success") {
        console.log("it worked!");
      }
    } catch (error) {
      console.log("error in onClick");
      console.error(error);
    }
  };

  return (
    //    <h1>NavBar</h1>
    <Disclosure as="nav" className="menu">
      {({ open }) => (
        <>
          <div className="flex flex-1 items-center justify-center sm:items-stretch sm:justify-start">
            <div className="nav2">
              {/* onClick={() => router.push("./")} */}
              Study Abroad @ Brown
            </div>
          </div>

          <div>
            {/* flex space-x-4 */}
            {navigation.map((item) => (
              <Link
                key={item.name}
                to={item.href}
                className="nav-item"
                aria-current={item.current ? "page" : undefined}
              >
                {item.name}
              </Link>
            ))}
          </div>

          <Menu as="div">
            {/* <div className="profile-outer"> */}
            <Menu.Button
              className="ml-2 relative flex rounded-full custom-dark-gray focus:outline-none"
              // style={{ background: "transparent", border: "none" }}
            >
              <img
                className="profile-circle"
                src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                alt=""
              />
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
                        active ? "bg-gray-200" : ""
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
                        active ? "bg-gray-200" : ""
                      } w-full text-left block px-4 py-2 text-gray-700`}
                    >
                      Sign Out
                    </button>
                  )}
                </Menu.Item>
                {/* </div> */}
              </Menu.Items>
            </Transition>
          </Menu>
        </>
      )}
    </Disclosure>
  );
}

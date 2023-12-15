"use client";
// import { Fragment, useState } from "react";
import { Bars3Icon, BellIcon, XMarkIcon } from "@heroicons/react/24/outline";
import { Disclosure, Menu, Transition } from "@headlessui/react";
import { BrowserRouter, Link } from "react-router-dom";
import photo from "../assets/blank-profile.jpeg"
import "../style/interface.css";
import "../style/App.css";
import { login, logout, loginStatus } from "./Login";
import { getAuth } from "firebase/auth";
import app from "./firebaseInit";
import NavbarButtons from "./NavbarButtons";
import React from "react";

const navigation = [
  { name: "Home", href: "/", current: true },
  { name: "Browse", href: "/browse/", current: false },
];

export default function Navbar() {
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
          <NavbarButtons />
        </>
      )}
    </Disclosure>
  );
}

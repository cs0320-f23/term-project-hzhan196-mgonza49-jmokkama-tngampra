"use client";
// import { Fragment, useState } from "react";
import { Disclosure } from "@headlessui/react";
import { Link } from "react-router-dom";
import "../style/interface.css";
import "../style/App.css";
import NavbarButtons from "./NavbarButtons";

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
            <div className="nav2 ">
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

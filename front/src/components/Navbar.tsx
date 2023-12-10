"use client";
import { Fragment, useState } from "react";
import { Disclosure, Menu, Transition } from "@headlessui/react";
import { BrowserRouter, Link} from 'react-router-dom';
import { Bars3Icon, BellIcon, XMarkIcon } from "@heroicons/react/24/outline";
import "../style/interface.css";
import "../style/App.css"


const navigation = [
  { name: "Home", href: "/", current: true },
  { name: "Browse", href: "/browse/", current: false },
];

export default function Navbar() {
  const [isShowing, setIsShowing] = useState(false);

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
     

        <div className="fixed top-16 w-56 text-right">
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
            onClick={() => setIsShowing((isShowing) => !isShowing)}
            style={{ background: "transparent", border: "none" }}
          >
            <img
              className="profile-circle"
              src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
              alt=""
            />
          </Menu.Button>

          <Transition
            as= {Fragment}
            show={isShowing}
            enter="ease-out opacity-100"
            enterFrom="scale-95 opacity-0"
            enterTo="scale-100 opacity-100"
            leave="ease-in opacity-75"
            leaveFrom="scale-100 opacity-100"
            leaveTo="scale-95 opacity-0"
          >
            <Menu.Items as="section" className="profile-dropdown">
              <div className="px-1 py-1 ">
                  <Menu.Item as={Fragment}>
                  {({ active }) => (
                      <button
                      className={`profile-dropdown-buttons ${active ? 'active' : ''}`}
                      // href="/settings"
                      >
                      Settings
                      </button>
                  )}
                  </Menu.Item>

                  <Menu.Item as={Fragment}>
                  {({ active }) => (
                      <button
                      // onClick={handleSignOut}
                      className={`profile-dropdown-buttons ${active ? 'active' : ''}`}
                      >
                      Sign Out
                      </button>
                  )}
                  </Menu.Item>
              </div>
            </Menu.Items>
          </Transition>
        </Menu>
      </>
    )}
  </Disclosure>
  );
}

'use client'
import { Fragment, useState } from 'react'
import { Disclosure, Menu, Transition } from '@headlessui/react'
import { Bars3Icon, BellIcon, XMarkIcon } from '@heroicons/react/24/outline'
import '../style/interface.css'


const navigation = [
  { name: 'Home', href: '#', current: true },
  { name: 'Browse', href: '/browse/', current: false },
]

export default function Navbar() {
const [isShowing, setIsShowing] = useState(false)
 
  return (
//    <h1>NavBar</h1>
    <Disclosure as="nav" className="menu">
    {({ open }) => (
        <>
         <div className="flex flex-1 items-center justify-center sm:items-stretch sm:justify-start">
            <div className="nav2"
                                   >
                                     {/* onClick={() => router.push("./")} */}
                    Study Abroad @ Brown
            </div>
        </div>
     
        {/* "hidden sm:ml-6 sm:block" */}
            <div className="">
                {/* flex space-x-4 */}
                {navigation.map((item) => (
                    <a
                    key={item.name}
                    href={item.href}
                    className="nav-item"
                    aria-current={item.current ? 'page' : undefined}
                    >
                    {item.name}
                    </a>
                ))}
            </div>
        
            <Menu as="div" >
                  {/* <div className="profile-outer"> */}
                    <Menu.Button 
                    onClick={() => setIsShowing((isShowing) => !isShowing)}
                    style={{ background: 'transparent', border: 'none' }}
                    >
   
                      <img
                        className= "profile-circle"
                        src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                        alt=""
                      /> 
                    </Menu.Button>  

                  <Transition
                    // as= {Fragment}
                    show={isShowing}
                    enter="transform transition duration-[400ms]"
                    enterFrom="opacity-0 rotate-[-120deg] scale-50"
                    enterTo="opacity-100 rotate-0 scale-100"
                    leave="transform duration-200 transition ease-in-out"
                    leaveFrom="opacity-100 rotate-0 scale-100 "
                    leaveTo="opacity-0 scale-95 " 
                >
                    <Menu.Items as="section">           
                        <Menu.Item as={Fragment}>
                        {({ active }) => (
                            <button
                                className={`${active}`}
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
                                className={`${active}`}
                            >
                                Sign Out
                            </button>
                            )}
                        </Menu.Item>
 
                    </Menu.Items>
                </Transition>
            </Menu>

        </>
    )}
    </Disclosure>
  )
}

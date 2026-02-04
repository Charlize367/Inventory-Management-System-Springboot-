import React from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext';
import LogoutConfirm from './LogoutConfirm';
import { useState } from 'react';
import userProfile from '../assets/user_profile.png'

const Sidebar = () => {
  const { logout } = useAuth();
  const username = localStorage.getItem('username');
  const email = localStorage.getItem('email');
  const role = localStorage.getItem('role');
  const [confirmDeletePopup, setConfirmDeletePopup] = useState(false)


  const handleDeletePopup = (e) => {
    e.preventDefault();
    setConfirmDeletePopup(true);
  }

  const handleLogout = async() => {
    logout();
    if(logout) {
    localStorage.setItem("showLogoutPopup", "true");

    }
  }

  const closeDeletePopup = (e) => {
    e.preventDefault();
    setConfirmDeletePopup(false);
  }
  

  
  return (

    
   <div className="flex h-full flex-col justify-between border-e border-white bg-white">
  <div className="px-4 py-6">
    <span className="grid h-10 w-40 place-content-center p-10 rounded-lg bg-gray-100 text-m text-gray-600">
      Inventory Management
    </span>

    <ul className="mt-6 space-y-1">
      <li>
        <Link to ="/dashboard"
          className="block rounded-lg bg-white px-4 py-2 text-sm font-medium text-gray-700"
        >
          General
        </Link>
      </li>

      {(role === "ADMIN" || role === "MANAGER") && (
      <li>
        <details className="group [&_summary::-webkit-details-marker]:hidden">
          <summary
            className="flex cursor-pointer items-center justify-between rounded-lg px-4 py-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
          >
            
            <span className="text-sm font-medium"> Team </span>

            <span className="shrink-0 transition duration-300 group-open:-rotate-180">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="size-5"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fillRule="evenodd"
                  d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
                  clipRule="evenodd"
                />
              </svg>
            </span>
          </summary>

          
          <ul className="mt-2 space-y-1 px-4">
            <li>
              <Link to ="/staffs"
                className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
              >
                Staff
              </Link>
            </li>

            {role === "ADMIN" && (
            <li>
              <Link to ="/managers"
                className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
              >
                Managers
              </Link>
            </li>
            )}
          </ul>
        
        </details>
      </li>
      )}
          

      <li>
        <Link to ="/products"
          className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
        >
          Products
        </Link>
      </li>

      <li>
        <Link to ="/categories"
          className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
        >
          Categories
        </Link>
      </li>

      <li>
        <Link to ="/brands"
          className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
        >
          Brands
        </Link>
      </li>

       <li>
        <Link to ="/variations"
          className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
        >
          Variation Groups
        </Link>
      </li>

      <li>
        <Link to ="/sku"
          className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
        >
          SKU
        </Link>
      </li>

      <li>
        <details className="group [&_summary::-webkit-details-marker]:hidden">
          <summary
            className="flex cursor-pointer items-center justify-between rounded-lg px-4 py-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
          >
            <span className="text-sm font-medium"> Contacts </span>

            <span className="shrink-0 transition duration-300 group-open:-rotate-180">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="size-5"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fillRule="evenodd"
                  d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
                  clipRule="evenodd"
                />
              </svg>
            </span>
          </summary>

          <ul className="mt-2 space-y-1 px-4">
            <li>
              <Link to ="/customers"
                className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
              >
                Customers
              </Link>
            </li>


            <li>
              <Link to ="/suppliers"
                className="w-full rounded-lg px-4 py-2 [text-align:_inherit] text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
              >
                Suppliers
              </Link>
            </li>
          </ul>
        </details>
      </li>

       <li>
        <details className="group [&_summary::-webkit-details-marker]:hidden">
          <summary
            className="flex cursor-pointer items-center justify-between rounded-lg px-4 py-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700"
          >
            <span className="text-sm font-medium"> Transactions </span>

            <span className="shrink-0 transition duration-300 group-open:-rotate-180">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="size-5"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  fillRule="evenodd"
                  d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z"
                  clipRule="evenodd"
                />
              </svg>
            </span>
          </summary>

          <ul className="mt-2 space-y-1 px-4">
            <li>
              <Link to ="/purchases"
                className="block rounded-lg px-4 py-2 text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
              >
                Purchases
              </Link>
            </li>


            <li>
              <Link to ="/sales"
                className="w-full rounded-lg px-4 py-2 [text-align:_inherit] text-sm font-medium text-gray-500 hover:bg-gray-100 hover:text-gray-700"
              >
                Sales
              </Link>
            </li>
          </ul>
        </details>
      </li>

      <li>
        <Link to ="/stock_movements"
          className="block rounded-lg bg-white px-4 py-2 text-sm font-medium text-gray-700"
        >
          Stock Movements
        </Link>
      </li>

      <li>
              <Link to ="/" onClick={handleDeletePopup}
                className="block rounded-lg bg-white px-4 py-2 text-sm font-medium text-gray-700"
              >
                Logout
              </Link>
            </li>
    </ul>
  </div>

  {confirmDeletePopup && (
    <LogoutConfirm onLogoutClick={handleLogout} onClose={closeDeletePopup} />
  )}

  <div className="sticky inset-x-0 bottom-0 border-t border-gray-100">
    <Link to ="/" className="flex items-center gap-2 bg-white p-4 hover:bg-gray-50">
      <img
        alt=""
        src={userProfile}
        className="size-10 rounded-full object-cover"
      />

      <div>
        <p className="text-xs">
          <strong className="block font-medium">{username}</strong>

          <span>{email} </span>
        </p>
      </div>
    </Link>
  </div>
  </div>
  )
}

export default Sidebar
import React from 'react'
import Sidebar from '../components/Sidebar'
import { useState, useEffect } from "react";
import { Menu, X } from "lucide-react"; 
import DailyMetrics from '../components/DailyMetrics';
import TransactionsWeeklyStats from '../components/TransactionsWeeklyStats';
import LowStock from '../components/LowStock';
import TopProducts from '../components/TopProducts';
import RecentTransactions from '../components/RecentTransactions';
import axios from 'axios';


const Dashboard = () => {
    const [showSidebar, setShowSidebar] = useState(false);
    const [dashboardData, setDashboardData] = useState([]);
    const username = localStorage.getItem('username');
    const API_URL = import.meta.env.VITE_API_URL;
    const token = localStorage.getItem('jwtToken');
    const [showLoginPopup, setShowLoginPopup] = useState(false)



    useEffect(() => {
       
        if (localStorage.getItem("showLoginPopup") === "true") {
          setShowLoginPopup(true);
    
          
          const timer = setTimeout(() => {
          setShowLoginPopup(false);
          localStorage.setItem("showLoginPopup", "false"); 
        }, 3000);

    return () => clearTimeout(timer);
    
          
        }
      }, []);

console.log(localStorage.getItem('role'))
  const getDashboardInfo = async() => {
    try {
             const response = await axios.get(`${API_URL}/dashboard`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });

              setDashboardData(response.data);
            
              
              
            } catch (error) {
              console.log(error);
              
            }
          }
  
      useEffect(() => {
      getDashboardInfo();
      
    }, []);

    console.log(dashboardData);

  return (

  <div className="grid grid-cols-1 lg:grid-cols-[300px_1fr] gap-4 lg:gap-0 w-full m-0 p-0 overflow-x-hidden min-h-screen">



        <div className="flex items-center justify-between bg-white p-4 lg:hidden">
          <h1 className="text-2xl block rounded-lg bg-white px-4 py-2 font-medium text-gray-700">Dashboard</h1>
          <button
            onClick={() => setShowSidebar(!showSidebar)}
            className="text-gray-700 hover:text-gray-900"
          >
            {showSidebar ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>

        
     
      <div
        className={`
          bg-gray-300 m-0 p-0
          ${showSidebar ? "block" : "hidden"}
          lg:block
        `}
      >
        <Sidebar />
      </div>

      
      <div className="bg-white m-0 p-0 flex flex-col">

    

        
        <div className="hidden lg:flex items-center justify-between p-4 border-b border-gray-300">
          <h1 className="text-2xl block rounded-lg px-4 py-2 font-medium text-gray-500">Dashboard</h1>
        </div>

       
        {dashboardData && (
        <div className="grid grid-cols-1 gap-4 m-0  bg-gray-200 p-4 flex-grow">
          
          <div className="bg-white p-4 rounded"><DailyMetrics totalSales={dashboardData.totalSales} totalPurchases={dashboardData.totalPurchases} totalProducts={dashboardData.activeProducts} totalCustomers={dashboardData.totalCustomers} totalSuppliers={dashboardData.totalSuppliers} totalProfit={dashboardData.totalProfit}/></div>
          <div className="bg-white p-4 rounded">
                <div class="grid grid-cols-1 gap-4 lg:grid-cols-2 lg:gap-30 flex justify-center">
                    <div class="bg-white p-4 rounded"><TransactionsWeeklyStats last7DaysSale={dashboardData.last7DaysSale} last7DaysPurchase={dashboardData.last7DaysPurchase}/></div>
                    <div class="bg-white p-4 rounded"><TopProducts topProducts={dashboardData.topSellingProducts}/></div>
                </div>
            </div>
          <div className="bg-white p-4 rounded">
            <div class="grid grid-cols-1 gap-4 lg:grid-cols-2 lg:gap-1 m-0">
                    <div class="bg-white p-4 rounded"><RecentTransactions recentTransactions={dashboardData.recentTransactions} /></div>
                    <div class="bg-white p-4 rounded"><LowStock lowStockProducts={dashboardData.lowStockProducts} /></div>
                </div>
          </div>
          
        </div>
        )}
      </div>
      {showLoginPopup && (
    <div id="toast-top-right" class="fixed flex items-center w-full max-w-xs p-4 space-x-4 text-white bg-gray-500 divide-x rtl:divide-x-reverse divide-gray-200 rounded-lg shadow-sm top-5 right-5" role="alert">
    <div class="text-m font-normal">Welcome, {username}!</div>
</div>
  )}
    </div>
    


  )
}

export default Dashboard
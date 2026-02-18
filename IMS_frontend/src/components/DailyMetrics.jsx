import React from 'react'
import { useState } from 'react';



const DailyMetrics = ({ totalSales, totalPurchases, totalProducts, totalCustomers, totalSuppliers, totalProfit }) => {
    const [loading, setLoading] = useState(true);
    const getLoader = () => {
    return(
     <div class="flex items-center justify-center">
  <div class="w-10 h-10 mt-20 border-4 border-gray-300 border-t-gray-600 rounded-full animate-spin"></div>
</div>
    )
}
  return (
    <div>
<section className="bg-gray-30 light:bg-gray-50 m-4">
  <div className="max-w-screen-xl px-4 py-8 mx-auto text-center lg:py-16 lg:px-6">
      <dl className="grid max-w-screen-md gap-8 mx-auto text-gray-500 sm:grid-cols-3 light:text-gray-500">
          <div className="flex flex-col items-center justify-center">
              <dt className="mb-2 text-3xl md:text-4xl font-extrabold">{totalSales ?? getLoader()}</dt>
              <dd className="font-light text-gray-500 dark:text-gray-400">Total Sales</dd>
          </div>
          <div className="flex flex-col items-center justify-center">
              <dt className="mb-2 text-3xl md:text-4xl font-extrabold">{totalPurchases ?? getLoader()}</dt>
              <dd className="font-light text-gray-500 dark:text-gray-400">Total Purchases</dd>
          </div>
          <div className="flex flex-col items-center justify-center">
              <dt className="mb-2 text-3xl md:text-4xl font-extrabold">{totalProducts ?? getLoader()}</dt>
              <dd className="font-light text-gray-500 dark:text-gray-400">Total Products</dd>
          </div>
          <div className="flex flex-col items-center justify-center">
              <dt className="mb-2 text-3xl md:text-4xl font-extrabold">{totalCustomers ?? getLoader()}</dt>
              <dd className="font-light text-gray-500 dark:text-gray-400">Total Customers</dd>
          </div>
          <div className="flex flex-col items-center justify-center">
              <dt className="mb-2 text-3xl md:text-4xl font-extrabold">{totalSuppliers ?? getLoader()}</dt>
              <dd className="font-light text-gray-500 dark:text-gray-400">Total Suppliers</dd>
          </div>
          <div className="flex flex-col items-center justify-center">
              <dt className="mb-2 text-3xl md:text-4xl font-extrabold "><span className={totalProfit < 0 ? "text-red-500" : ""}>
  {(totalProfit ?? 0).toLocaleString('en-PH', { style: 'currency', currency: 'PHP' })}
</span></dt>
              <dd className="font-light text-gray-500 dark:text-gray-400">Total Profit</dd>
          </div>
      </dl>
  </div>
</section>
</div>
  )
}

export default DailyMetrics
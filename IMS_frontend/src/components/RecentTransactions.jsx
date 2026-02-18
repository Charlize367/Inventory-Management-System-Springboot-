import React from 'react'
import { useState, useEffect } from 'react'

const RecentTransactions = ({ recentTransactions }) => {

    const [loading, setLoading] = useState(true);

    useEffect(() => {
      if (recentTransactions) {
        setLoading(false);
      }
    }, [recentTransactions]);

  return (
    <div>
{loading ? (
  
     <div class="flex items-center justify-center">
  <div class="w-10 h-10 mt-20 border-4 border-gray-300 border-t-gray-600 rounded-full animate-spin"></div>
</div>
    
   
    ) : recentTransactions.length === 0 ? (

        <div>
        <div className="relative overflow-x-auto shadow-md sm:rounded-lg">
    <table className="w-full text-sm text-left rtl:text-right text-gray-500 ">
        <caption className="p-5 text-2xl  font-semibold text-left rtl:text-right text-gray-500 bg-gray-50">
            Recent Transactions
            <h5 className="mt-1 text-2xl font-bold font-normal text-gray-500 "></h5>
        </caption>
        <thead className="text-xs text-gray-500 uppercase bg-gray-130">
            <tr>
                <th scope="col" className="px-6 py-3">
                    Amount
                </th>
                 <th scope="col" className="px-6 py-3">
                    Party
                </th>
                <th scope="col" className="px-6 py-3">
                    Type
                </th>
            
                <th scope="col" className="px-6 py-3">
                    Date
                </th>
            </tr>
        </thead>
        </table>
        </div>
        <p className="flex justify-center mt-4">No records found.</p>
        </div>
        
     ) : (
        <>
        <div className="relative overflow-x-auto shadow-md sm:rounded-lg">
    <table className="w-full text-sm text-left rtl:text-right text-gray-500 ">
        <caption className="p-5 text-2xl  font-semibold text-left rtl:text-right text-gray-500 bg-gray-50">
            Recent Transactions
            <h5 className="mt-1 text-2xl font-bold font-normal text-gray-500 "></h5>
        </caption>
        <thead className="text-xs text-gray-500 uppercase bg-gray-130">
            <tr>
                <th scope="col" className="px-6 py-3">
                    Amount
                </th>
                 <th scope="col" className="px-6 py-3">
                    Party
                </th>
                <th scope="col" className="px-6 py-3">
                    Type
                </th>
            
                <th scope="col" className="px-6 py-3">
                    Date
                </th>
            </tr>
        </thead>
        <tbody>
            {recentTransactions?.map(t => (
            <tr key={t.id} className="bg-gray-50 border-b border-gray-200">
                <td className="px-6 py-4">
                    {t.amount.toLocaleString("en-PH", { style: "currency", currency: "PHP" })}
                </td>
                <td className="px-6 py-4">
                    {t.party}
                </td>
                <td className="px-6 py-4">
                    {t.type}
                </td>
                <td className="px-6 py-4">
                    {t.date}
                </td>
                
            </tr>
            ))}
        </tbody>
    </table>
     
</div>
</>
    )}
</div>
  )
}

export default RecentTransactions
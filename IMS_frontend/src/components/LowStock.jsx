
import { useState, useEffect } from 'react'

const LowStock = ({ lowStockProducts}) => {
    const [loading, setLoading] = useState(true);

    useEffect(() => {
      if (lowStockProducts) {
        setLoading(false);
      }
    }, [lowStockProducts]);
  return (
    <div>
{loading ? (
  
    <div class="flex items-center justify-center">
  <div class="w-10 h-10 mt-20 border-4 border-gray-300 border-t-gray-600 rounded-full animate-spin"></div>
</div>
    
   
    ) : lowStockProducts.length === 0 ? (
    <div>
    <div className="relative overflow-x-auto shadow-md sm:rounded-lg">
    <table className="w-full text-sm text-left rtl:text-right text-gray-500 ">
        <caption className="p-5 text-2xl  font-semibold text-left rtl:text-right text-gray-500 bg-gray-50">
            📉 Low Stock Alert
            <h5 className="mt-1 text-2xl font-bold font-normal text-gray-500 "></h5>
        </caption>
        <thead className="text-xs text-gray-500 uppercase bg-gray-130">
            <tr>
                <th scope="col" className="px-6 py-3">
                    Number
                </th>
                <th scope="col" className="px-6 py-3">
                    Product Name
                </th>
                <th scope="col" className="px-6 py-3">
                    Stocks
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
            📉 Low Stock Alert
            <h5 className="mt-1 text-2xl font-bold font-normal text-gray-500 "></h5>
        </caption>
        <thead className="text-xs text-gray-500 uppercase bg-gray-130">
            <tr>
                <th scope="col" className="px-6 py-3">
                    Number
                </th>
                <th scope="col" className="px-6 py-3">
                    Product Name
                </th>
                <th scope="col" className="px-6 py-3">
                    Stocks
                </th>
            </tr>
        </thead>
        <tbody>
            {lowStockProducts?.map(p => (
            <tr className="bg-gray-50 border-b border-gray-200">
                <th scope="row" className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap ">
                    {p.productId}
                </th>
                <td className="px-6 py-4">
                    {p.productName}
                </td>
                <td className="px-6 py-4">
                    {p.stock}
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

export default LowStock
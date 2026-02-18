import React from 'react'
import axios from 'axios';
import { useState, useEffect } from 'react';


const Table = ({ headers, columns, data, onAddClick, onEditClick, onDeleteClick, disableAdd, disableEdit, currentPage, totalPages, fetchData, disablePagination }) => {
    const API_URL = import.meta.env.VITE_API_URL;
    const token = localStorage.getItem('jwtToken');
    const [tableData, setTableData] = useState([]);
    const role = localStorage.getItem('role');
    const [loading, setLoading] = useState(true);
    const getEntityId = (row) => {
        return row.id || row.categoryId || row.variationId || row.brandId || row.userId || row.customerId || row.supplierId || row.purchaseId || row.salesId || row.stockMovementId || row.variationOptionId || null;
    };

useEffect(() => {
  if (data.length > 0) {
    setTableData(data);
    setLoading(false);
  }
}, [data]);



const getAccessorValue = (row, accessor) => {
  const keys = accessor.split('.');

  const getValue = (obj, keys) => {

    if(!obj) return null;
    if(keys.length == 0) return obj;

    const [firstKey, ...restKeys] = keys;

    if(Array.isArray(obj)) {
      return obj.flatMap(item => getValue(item?.[firstKey], restKeys));
    } else {
      return getValue(obj[firstKey], restKeys);
    }

  };

  return getValue(row, keys);
};

const handlePurchaseStatusChange = async(purchaseId, newStatus) => {
    try {
        await axios.put(`${API_URL}/purchases/${purchaseId}`, {purchaseStatus: newStatus}, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },  
            });
    setTableData((prev) =>
      prev.map((item) =>
        item.purchaseId === purchaseId
          ? { ...item, purchaseStatus: newStatus }
          : item
      )
    );
  } catch (err) {
    console.error("Failed to update status:", err);
  }
    
}

const handleSalesPaymentStatusChange = async(salesId, newStatus) => {
    try {
        await axios.put(`${API_URL}/sales/${salesId}`, {salesPaymentStatus: newStatus}, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },  
            });
    setTableData((prev) =>
      prev.map((item) =>
        item.salesId === salesId
          ? { ...item, salesPaymentStatus: newStatus }
          : item
      )
    );
  } catch (err) {
    console.error("Failed to update status:", err);
  }
    
}

const toggleActive = async(skuId) => {
  try {

    const currentItem = tableData.find(item => item.skuId === skuId);
    if (!currentItem) return;

    const newStatus = !currentItem.active;

    await axios.patch(`${API_URL}/sku/toggleActive/${skuId}`, {}, {
      headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                }, 
    });
    setTableData((prev) =>
      prev.map((item) =>
        item.skuId === skuId
          ? { ...item, active: newStatus }
          : item
      )
    );
  } catch (error) {
    console.error(error);
  }
}

console.log(tableData);

  return (
    <div>
    {loading  ? (
  
    <div class="flex items-center justify-center">
  <div class="w-15 h-15 mt-30 border-4 border-gray-300 border-t-gray-600 rounded-full animate-spin"></div>
</div>
    
   
    ) : data.length === 0 && !loading ? (
    <div>
      <p className="flex justify-center mt-4">No records found.</p>
      {(
  (role === "ADMIN" || role === "MANAGER" ) || 
  (headers.some(header => header.key === 'purchaseId') || headers.some(header => header.key === 'salesId'))
) && disableAdd === "false" && (
  <div className="flex items-center justify-center">
    <button 
      onClick={onAddClick} 
      className="flex cursor-pointer items-center justify-center px-3 mt-5 w-9 h-9 text-xl leading-tight text-gray-500 bg-white border border-gray-300 rounded-e hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
    >
      +
    </button>
  </div>
)}
    </div>
    ) : (
        <>
    <div className="relative overflow-x-auto shadow-md sm:rounded-lg">
    <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
        <thead className="text-xs text-gray-700 uppercase bg-gray-50">
            <tr> 
                
                {headers.map((header) => (
    <th scope="col" className="px-6 py-3 text-left" key={header.key}>
        {header.header}
    </th>
))}

          {(role === "ADMIN" || role === "MANAGER") && disableEdit === "false" && (
              <th scope="col" className="px-2 py-3 text-center w-24">
                  Edit
              </th>
          )}

          {(role === "ADMIN" || role === "MANAGER") && (!headers.some(header => header.key === 'skuId')) && (
              <th scope="col" className="px-2 py-3 text-center w-24">
                  Delete
              </th>
          )}

            </tr>
        </thead>
        <tbody>
            {tableData.map((row, rowIndex) => (
          <tr className="bg-white border-b border-gray-200 text-gray-500 hover:bg-gray-700 hover:text-white" key={rowIndex}>
            {columns.map((column) => {
                const value = getAccessorValue(row, column.accessor);
                const isPurchaseAmount = column.accessor.includes("purchaseAmount");
                const isSalesAmount = column.accessor.includes("salesAmount"); 
                const isDateTime = column.accessor.includes("createdAt");
                const isSaleMovement = column.accessor.includes("quantity") && row.stockMovement === "SALE" ;
                const isActive = column.accessor.includes("active");
             
                return(
                    <td className="px-6 py-4 max-w-xs break-words" key={column.accessor}>
                    {Array.isArray(value) ? (
                      value.map((item, i) => (
                        <span key={i}>
                          {item}
                          <br />
                        </span>
                      ))
                    ) : isPurchaseAmount || isSalesAmount ? (
                      value?.toLocaleString("en-PH", { style: "currency", currency: "PHP" })
                    ) : isDateTime ? (
                new Date(value).toLocaleString("en-PH", {
                      year: "numeric",
                      month: "short",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                      second: "2-digit",
                    })
                    ) : isSaleMovement ? (
                      -value
                    ) : isActive ? (
                      row.skuId && (
                  <label className="inline-flex items-center cursor-pointer">
                    <input type="checkbox" value="" checked={row.active} onChange={() => toggleActive(row.skuId)} className="sr-only peer"/>
                    <div className={`relative w-9 h-5 ${row.active ? 'bg-black' : 'bg-gray-400'} peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-gray-700 rounded-full peer peer-checked:bg-black after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-4 after:w-4 after:transition-all peer-checked:after:translate-x-full`}></div>
              
                  </label>
                )
                    
                    ) : (
  value != null ? value : "—"
)}
                  </td>
                );
                })}
                {disableEdit === "true" && row.purchaseId && (
                <td className="px-6 py-4">
                <select
                    value={row.purchaseStatus}
                    onChange={(e) =>
                    handlePurchaseStatusChange(row.purchaseId, e.target.value)
                    }
                    className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
                >
                    <option value="PENDING">Pending</option>
                    <option value="COMPLETE">Completed</option>
                    <option value="CANCELLED">Cancelled</option>
                </select>
                </td>
                )}

                {disableEdit === "true" && row.salesId && (
                <td className="px-6 py-4">
                <select
                    value={row.salesPaymentStatus}
                    onChange={(e) =>
                    handleSalesPaymentStatusChange(row.salesId, e.target.value)
                    }
                    className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
                >
                    <option value="PENDING">Pending</option>
                    <option value="PROCESSING">Processing</option>
                    <option value="PAID">Paid</option>
                    <option value="FAILED">Failed</option>
                    <option value="CANCELLED">Cancelled</option>
                    <option value="REFUNDED">Refunded</option>
                    <option value="PARTIALLY_REFUNDED">Partially Refunded</option>
                    <option value="DISPUTED">Disputed</option>
                    <option value="EXPIRED">Expired</option>
                </select>
                </td>
                )}

                

                 {(role === "ADMIN" || role === "MANAGER") && disableEdit === "false" && (
  <td className="px-6 py-4">
    <button
      onClick={() => onEditClick(row)}
      className="px-4 py-2 bg-green-600 text-white font-medium rounded hover:bg-green-500 hover:shadow-md transition-all duration-200 cursor-pointer"
    >
      Edit
    </button>
  </td>
)}

{(role === "ADMIN" || role === "MANAGER") &&  (!headers.some(header => header.key === 'skuId')) && (
  <td className="px-6 py-4">
    <button
      onClick={() => onDeleteClick(getEntityId(row))}
      className="px-4 py-2 bg-red-600 text-white font-medium rounded hover:bg-red-500 hover:shadow-md transition-all duration-200 cursor-pointer"
    >
      Delete
    </button>
  </td>
)}
            </tr>
            ))}
        </tbody>
    </table>
</div>

            
   {(
  ((role === "ADMIN" || role === "MANAGER") || 
  (headers.some(header => header.key === 'purchaseId') || headers.some(header => header.key === 'salesId')))
  && disableAdd === "false"
) && (
  <div className="flex items-center justify-center">
    <button type="button"
      onClick={onAddClick} 
      className="flex cursor-pointer items-center justify-center px-3 mt-5 w-9 h-9 text-xl leading-tight text-gray-500 bg-white border border-gray-300 rounded-e hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
    >
      +
    </button>
  </div>
)}

{disablePagination != "true" &&
<nav className="flex items-center flex-column flex-wrap md:flex-row justify-between pt-4" aria-label="Table navigation">
        <span className="text-sm font-normal text-gray-900  mb-4 md:mb-0 block w-full md:inline md:w-auto">Showing page <span className="font-semibold text-gray-900 ">{currentPage + 1}</span> of <span className="font-semibold text-gray-900" >{totalPages}</span></span>
        
        <ul className="inline-flex -space-x-px rtl:space-x-reverse text-sm h-8">
            
            <li>
                <button className="flex cursor-pointer items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white" onClick={() => fetchData(currentPage - 1)}>Previous</button>
            </li>

            
                {Array.from({ length: totalPages}, (_, i) => (
                    <li>
                    <button key={i}  className="flex cursor-pointer items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white" onClick={() => fetchData(i)}>{i + 1}</button>
                     </li>
                ))}
                
        
            
            <li>
        <button className="flex items-center cursor-pointer justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white" disabled={currentPage === totalPages - 1} onClick={() => fetchData(currentPage + 1)}>Next</button>
            </li>
        </ul>
    </nav> 
}
              
   </>
              
    )}
            
    </div>
            
  )
}

export default Table
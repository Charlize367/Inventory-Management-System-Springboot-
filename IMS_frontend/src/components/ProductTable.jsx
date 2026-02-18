import React from 'react'
import { useState, useEffect } from 'react'

const ProductTable = ({ columns, headers, data,  onAddClick, onEditClick, onDeleteClick, currentPage, totalPages, fetchData }) => {
    const bucket = import.meta.env.VITE_S3_BUCKET;
    const region = import.meta.env.VITE_AWS_REGION;
    const role = localStorage.getItem('role');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
      if (data) {
        
        setLoading(false);
      }
    }, [data]);
    
const getAccessorValue = (row, accessor) => {
 
  
    return accessor.split('.').reduce((object, key) => {
      if (Array.isArray(object)) {
        return object.map(item => item?.[key]);
      }
      return object?.[key];
    }, row);
};
console.log(data);
  return (
    <div>
    {loading ? (
    <p className="flex justify-center mt-4">Loading...</p>
    ) : data.length === 0 ? (
      <div>
      <p className="flex justify-center mt-4">No products found.</p>
      
        <div className="flex items-center justify-center">
                <button onClick={onAddClick} className="flex cursor-pointer items-center justify-center px-3 mt-5  w-9 h-9 text-xl leading-tight text-gray-500 bg-white border border-gray-300 rounded-e hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">+</button>
        </div>
    
    </div>
    ) : (
        <>
    <div className="relative overflow-x-auto shadow-md sm:rounded-lg">
    <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
        <thead className="text-xs text-gray-700 uppercase bg-gray-50">
            <tr>
                {headers.map((header) => (
                    <th scope="col" className="px-6 py-3" key={header.key}>{header.header}</th>
                ))}

                {(role === "ADMIN" || role === "MANAGER") && (
                <th scope="col" className="px-6 text-center py-3">
                    Edit
                </th>
                )}

                {(role === "ADMIN" || role === "MANAGER") && (
                <th scope="col" className="px-6 py-3 text-center">
                    Delete
                </th>
                )}
            </tr>
        </thead>
        <tbody>
            
        {data.map((row, rowIndex) => (
          <tr className="bg-white border-b border-gray-200 text-gray-500 hover:bg-gray-700 hover:text-white" key={rowIndex}>
            {columns.map((column) => {
                
                
                const value = getAccessorValue(row, column.accessor);
                const isImage = column.accessor.includes("productImage");
                const isCost = column.accessor.includes("productCost");
                const isPrice = column.accessor.includes("productPrice");
                const isVariation = column.accessor.includes("variations");

                return (
        <td className="px-6 py-4 max-w-2xl break-words" key={column.accessor}>
  {isImage ? (
    <img
      src={`https://${bucket}.s3.${region}.amazonaws.com/${value}`}
      alt={"Image"}
      className="w-16 md:w-32 max-w-full max-h-full"
    />
  ) : Array.isArray(value) ? (
    value.length === 0 ? (
      <span>No items</span>
    ) : (
    value.map((item, index) => (
      <div classname="mb-3" key={index}>
      <span key={index}>
        
        {item.categoryName ? item.categoryName : item.variationName ? `${item.variationName} (${item.variationOptions
              ?.map(vo => vo.variationOptionName)
              .join(", ")})` : item.toString()}
        {index < value.length - 1 ? ", " : ""}
      </span> 
     
      </div>
    ))
  )
  ) : typeof value === "object" ? (
    value?.categoryName || value?.name || JSON.stringify(value)
  ) : isCost || isPrice ? (
    value?.toLocaleString("en-PH", { style: "currency", currency: "PHP" })
  ) : (
    value
  )}
</td>
            );
            })}
        
        

                {(role === "ADMIN" || role === "MANAGER") && (
  <td className="px-6 py-4">
    <button
      onClick={() => onEditClick(row)}
      className="px-4 py-2 bg-green-600 text-white font-medium rounded hover:bg-green-500 hover:shadow-md transition-all duration-200 cursor-pointer"
    >
      Edit
    </button>
  </td>
)}

{(role === "ADMIN" || role === "MANAGER") && (
  <td className="px-6 py-4">
    <button
      onClick={() => onDeleteClick(row.productId)}
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
{(role === "ADMIN" || role === "MANAGER") && (
<div className="flex items-center justify-center">
    
           <button onClick={onAddClick} className="flex cursor-pointer items-center justify-center px-3 mt-5  w-9 h-9 text-xl leading-tight text-gray-500 bg-white border border-gray-300 rounded-e hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">+</button>
</div>
)}
<nav className="flex items-center flex-column flex-wrap md:flex-row justify-between pt-4" aria-label="Table navigation">
        <span className="text-sm font-normal text-gray-900  mb-4 md:mb-0 block w-full md:inline md:w-auto">Showing page <span className="font-semibold text-gray-900 ">{currentPage + 1}</span> of <span className="font-semibold text-gray-900" onClick={() => fetchData(currentPage - 1)}>{totalPages}</span></span>
        
        <ul className="inline-flex -space-x-px rtl:space-x-reverse text-sm h-8">
            
            <li>
                <button className="flex cursor-pointer items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">Previous</button>
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
    </>
    )}
    </div>
  )
}

export default ProductTable
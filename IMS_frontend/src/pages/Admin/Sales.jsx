import React from 'react'
import Sidebar from '../../components/Sidebar'
import axios from 'axios';
import { useState, useEffect } from "react";
import { Menu, X } from "lucide-react"; 
import Table from '../../components/Table';
import DeleteConfirm from '../../components/DeleteConfirm';
import { Listbox, ListboxButton, ListboxOption, ListboxOptions } from '@headlessui/react'
import DeleteSuccessPopup from '../../components/DeleteSuccessPopup';

const Sales = () => {
  const [showSidebar, setShowSidebar] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [showModal2, setShowModal2] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const API_URL = import.meta.env.VITE_API_URL;
  const [sales, setSales] = useState([]);
  const token = localStorage.getItem('jwtToken');
  const [deleteId, setDeleteId] = useState(0);
  const [selectedItems, setSelectedItems] = useState([]);
  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const userId = Number(localStorage.getItem("userId"));
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize] = useState(5);
  const role = localStorage.getItem('role');
  const [deleteSuccess, setDeleteSuccess] = useState(false);
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [variations, setVariations] = useState([]);
  const [ skus, setSkus] = useState([]);


  const openModal = () => setShowModal(true);
  const closeModal = () => setShowModal(false);

  const openModal2 = () => setShowModal2(true);
  const closeModal2 = () => setShowModal2(false);

  const headers = [
      { key: 'salesId', header: 'ID' },
      { key: 'saleItems', header: 'Items' },
      { key: 'variationOptions', header: 'Variation'},
      { key: 'customer', header: 'Customer' },
      { key: 'staff', header: 'Staff' },
      { key: 'salesAmount', header: 'Amount' },
      { key: 'saleDate', header: 'Date' },
      { key: 'salesPaymentStatus', header: 'Status' },
      
      ];
  
      console.log(sales);
      const columns = [
      {
          Header: 'ID',
          accessor: 'salesId', 
      },
      {
          Header: 'Items',
          accessor: 'salesItem.product.productName',
      },
      {
      Header: 'Variation',
      accessor: 'salesItem.variationOption.variationOptionName'
    },
      {
          Header: 'Customer',
          accessor: 'customer.customerName',
      },
      {
          Header: 'Staff',
          accessor: 'staff.username',
      },
      {
          Header: 'Amount',
          accessor: 'salesAmount',
      },
      {
          Header: 'Date',
          accessor: 'saleDate',
      },
      
      ];
  
      const [formData, setFormData] = useState({
          salesId: null,
          salesItem: selectedItems,
          customerId: 0,
          staffId: userId,
          salesPaymentStatus: "PENDING",
          saleDate: ""
      });
  
      useEffect(() => {
      setFormData(prev => ({
          ...prev,
          salesItem: selectedItems
      }));
      }, [selectedItems]);
  
      const handleChange = (e) => {
              const { name, value } = e.target;
          setFormData({ ...formData, [name]: name === "customerId" ? Number(value) : value,
              });
      }

      const getSku = async () => {
       try {
             const response = await axios.get(`${API_URL}/sku/all`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });

              setSkus(response.data);
            
            
              
              
            } catch (error) {
              console.log(error);
              if (error.response?.data === "Too many requests" || error.response?.status === 429) {
                const retryAfter = parseInt(error.response.headers["retry-after"], 10) || 5;
                setRetryTime(retryAfter);
                setError("Too Many Requests");
                setShowPopup(true);
                }
            }
          }
  
      useEffect(() => {
      getSku();
      
    }, []);
    


      const disableOption = (itemId, variationOption, variationId, product) => {

    // takes a variation option with variation id
        const newOption = {
  ...variationOption,  
  variationId: variationId,  
};

        // takes selected id based on index 
        const item = selectedItems[itemId];
        // reads var options array of the selected item
        const currentSelections = item?.variationOptions || [];

        console.log(product);
        //if variation options of the selected item is empty, automatically disable the option. like if the stock for this option is completely zero, DISABLE.
        
            const hasStockForOption = skus.some(sku => 
                sku.product.productId === product.productId &&
                sku.stockQuantity > 0 &&
                sku.variationOptions.some((skuOpt) =>
                    skuOpt.variationOptionId === variationOption.variationOptionId
            ));

            if (!hasStockForOption) return true;
        


        // this forms a combo with the variation option (that is being determined) with the currently selected options to create a hypothetical combo
        const hypotheticalCombo = [...currentSelections.filter(opt => !opt.variationId || opt.variationId !== variationId), newOption];
        
       
        // this will look for the corresponding sku combo of the hypothetical combo (doesn't matter the order)
        const matchingSkuCombo = skus.find(sku => 
    sku.product.productId === product.productId &&
    sku.variationOptions.length === hypotheticalCombo.length &&
    sku.variationOptions.every(skuOpt => 
        hypotheticalCombo.some(h => 
            h.variationId === skuOpt.variationId &&
            h.variationOptionId === skuOpt.variationOptionId
        )
    )
);
        console.log(matchingSkuCombo)
        
        //checks if the sku combo has no stock, DISABLE
        if (matchingSkuCombo?.stockQuantity === 0) return true;

        //ENABLE
        return false;
    }

     const handleOptionChange = (itemId, variation, option) => {
       if (!option) return;
  setSelectedItems(prevItems => {
    
  

     const updatedItems = [...prevItems];
     const item = { ...updatedItems[itemId] };
    const updatedOptions = [...item.variationOptions];

      const index = updatedOptions.findIndex(vo => vo.variationId === variation.variationId);

      if (index > -1) {
        
        updatedOptions[index] = {
          variationId: variation.variationId,
          variationOptionId: option.variationOptionId,
          variationOptionName: option.variationOptionName,
          variationOptionCode: option.variationOptionCode,
          variationPriceAdjustment: option.variationPriceAdjustment
        };
      } else {
        updatedOptions.push({
          variationId: variation.variationId,
          variationOptionId: option.variationOptionId,
          variationOptionName: option.variationOptionName,
          variationOptionCode: option.variationOptionCode,
          variationPriceAdjustment: option.variationPriceAdjustment
        });
      }
      

    const updatedItem = { ...item, variationOptions: updatedOptions };
    updatedItems[itemId] = updatedItem;

    const product = products.find(
  p => p.productId === updatedItem.productId
);

const totalVariations = product?.variations?.length || 0;

console.log("Optioins", updatedOptions);
console.log("options to be passed: ", option);


      const existingIndex = updatedItems.findIndex(
        (i, idx) =>  {
        if(idx == itemId) return false;
        if (i.productId !== updatedItem.productId) return false;

        if (
    i.variationOptions.length !== totalVariations ||
    updatedItem.variationOptions.length !== totalVariations
  ) {
    return false;
  }

        return JSON.stringify(i.variationOptions) === JSON.stringify(updatedItem.variationOptions)
        });

      

      if (existingIndex >= 0) {
        
        const mergedQuantity = updatedItem.purchaseItemQuantity + updatedItems[existingIndex].purchaseItemQuantity;

   
    updatedItems[existingIndex] = {
      ...updatedItems[existingIndex],
      purchaseItemQuantity: mergedQuantity
    };
        updatedItems.splice(itemId, 1);
        
        
      }

      return updatedItems;
    })
};
      
           const getVariations = async() => {
             try {
                   const response = await axios.get(`${API_URL}/variations/all`, {
                        headers: {
                             'Content-Type': 'application/json',
                             'Authorization': `Bearer ${token}`
                        }
                    });
                    console.log(response);
      
                    setVariations(response.data);
                    setShowPopup(false);
                  
                  
                    
                    
                  } catch (error) {
                    console.log(error);
                  
                  }
                }
        
            useEffect(() => {
            getVariations();
            
          }, []);
  
  
      const getCustomers = async () => {
         try {
               const response = await axios.get(`${API_URL}/customers/all`, {
                    headers: {
                         'Content-Type': 'application/json',
                         'Authorization': `Bearer ${token}`
                    }
                });
  
                setCustomers(response.data);
                
              
                
                
              } catch (error) {
                console.log(error);
                
                  }
          }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
    
        useEffect(() => {
        getCustomers();
        
      }, []);
  
      const getProducts = async () => {
         try {
               const response = await axios.get(`${API_URL}/products/all`, {
                    headers: {
                         'Content-Type': 'application/json',
                         'Authorization': `Bearer ${token}`
                    }
                });
  
                setProducts(response.data);
              
                
                
              } catch (error) {
                console.log(error);
                
                  }
          }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
    
        useEffect(() => {
        getProducts();
        
      }, []);
  
  
  
      const getSales = async (page = 0) => {
        let endpoint = ``;
        if(role === "ADMIN" || role === "MANAGER") {
          endpoint = `sales?page=${page}&size=${pageSize}`;
        } else if (role === "STAFF") {
          endpoint =`sales?staffId=${userId}&page=${page}&size=${pageSize}`
        }
         try {
               const response = await axios.get(`${API_URL}/${endpoint}`, {
                    headers: {
                         'Content-Type': 'application/json',
                         'Authorization': `Bearer ${token}`
                    }
                });
  
                setSales(response.data.content);
                setCurrentPage(response.data.number);
                setTotalPages(response.data.totalPages);
                
                
              } catch (error) {
                console.log(error);
                
                  }
          }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
    
        useEffect(() => {
        getSales(0);
        
      }, []);
  
  
      const handleSubmit = async (e) => {
  
      e.preventDefault();
      console.log(formData);

      if (selectedItems.length === 0) {
        alert("Please select at least one product");
        return;
        }
  
         try {
  
          if (isEditing) {
          
              const response = await axios.put(`${API_URL}/sales/${formData.salesId}`, formData, {
                  headers: {
                      'Content-Type': 'application/json',
                      'Authorization': `Bearer ${token}`,
                  },  
              });
  
              console.log(response.data);
                
          
              getSales();
  
              closeAndClearForm();
  
          } else {

          const payload = {
          salesId: null,
          salesItem: selectedItems,
          customerId: formData.customerId,
          staffId: userId,
        };
               const response = await axios.post(`${API_URL}/sales`, payload,   {
                    headers: {
                         'Content-Type': 'application/json',
                         'Authorization': `Bearer ${token}`
                    }
                });
                console.log(response.data);
                
          
                getSales();

               
                closeAndClearForm();
                
              }
              } catch (error) {
                console.log(error);
                window.alert("Error adding sales record. Make sure products are still in stock.");
                
              }
      }
  
      const closeAndClearForm = () => {
          closeModal();
          setIsEditing(false);
          setFormData({
              salesId: null,
              salesItems: [],
              customerId: 0,
              staffId: userId,
              salesPaymentStatus: "",
              saleDate: ""
          });
        setSelectedItems([]);
        setSelectedOptions([]);
      }
  
      const handleDelete = (id) => {
          openModal2();
  
          setDeleteId(id);
  
          
      }

      const addSelectedItems = () => {
  setSelectedItems(prev => [
    ...prev,
    { productId: null , purchaseItemQuantity: 0, variationOptions: [] }
  ]);
};

const removeSelectedItems = (index) => {
  setSelectedItems(prev => prev.filter((_, i) => i !== index));
};

      console.log(selectedItems);
  
      const deleteSale = async(id) => {
          try {
            const response = await axios.delete(`${API_URL}/sales/${id}`, {
                headers: {
                          'Authorization' : `Bearer ${token}`,
                          'Content-Type': 'application/json'
                      }});
  
                getSales();     
                setDeleteSuccess(true);  
                closeModal2();
  
          } catch (error) {
            console.log(error);
          }
      }
  console.log(selectedItems);
  
  

  return (

  <div className="grid grid-cols-1 lg:grid-cols-[300px_1fr] gap-4 lg:gap-0 w-full m-0 p-0 overflow-x-hidden min-h-screen">



        <div className="flex items-center justify-between bg-white p-4 lg:hidden">
          <h1 className="text-2xl block rounded-lg bg-white px-4 py-2 font-medium text-gray-700">Sales</h1>
          <button
            onClick={() => setShowSidebar(!showSidebar)}
            className="text-gray-700 cursor-pointer hover:text-gray-900"
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
          <h1 className="text-2xl block rounded-lg px-4 py-2 font-medium text-gray-500">Sales</h1>
        </div>

       
                <div className="grid grid-cols-1 gap-4 m-0  bg-gray-200 p-4 flex-grow">
                  <Table headers={headers} columns={columns} onAddClick={openModal} data={sales} onDeleteClick={handleDelete} disableAdd="false" disableEdit="true" currentPage={currentPage} totalPages={totalPages} fetchData={getSales} />
        
                {showModal && (
                <div id="crud-modal" tabIndex="-1" className=" overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 flex justify-center  items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
                    <div className="relative p-4 w-full max-w-6xl max-h-full">
                    
                        <div className="relative bg-gray-100 rounded-lg shadow-sm ">
                        
                            <div className="flex items-center justify-between p-4 md:p-5 rounded-t dark:border-gray-600 border-gray-100">
                                <h3 className="text-lg font-semibold text-gray-900">
                                    Add New Sale
                                </h3>
                                <button type="button" onClick={closeAndClearForm} className="text-gray-400 cursor-pointer bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-toggle="crud-modal">
                                    <svg className="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                                        <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                                    </svg>
                                    <span className="sr-only">Close modal</span>
                                </button>
                            </div>
                            
                            <form className="p-4 md:p-5" onSubmit={handleSubmit}>
                                <div className="grid gap-4 mb-4 grid-cols-2">
                                    <div className="col-span-2 ">
                                        <label htmlFor="customer" className="block mb-2 text-sm font-medium text-gray-900">Customer</label>
                                        <select name="customerId" id="customerId" value={formData.customerId} onChange={handleChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 ">
                                            <option value="" className="text-gray-50">Select customer</option>
                                            {customers.map(c => (
                                                <option key={c.customerId} value={c.customerId}>{c.customerName}</option>
                                            ))}
                                        </select>
                                    </div>
                                     
                                    <div className="col-span-2">
                                        <label className="block mb-2 text-sm font-medium text-gray-900">Select Items</label>
        
                                        <button
                            type="button"
                            onClick={addSelectedItems}
                            className="text-white inline-flex items-center bg-gray-700 cursor-pointer hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center"
                        >
                            Add Product +
                        </button>
                            </div>
                                  
                                
                                
                        </div>

                        <div className="relative overflow-x-auto shadow-md sm:rounded-lg">
                {selectedItems.length > 0 &&
                    <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
        <thead className="text-xs text-gray-700 uppercase bg-gray-50">
         
            <tr> 
                
               
                <th scope="col" className="px-6 py-3">Product</th>
                <th scope="col" className="px-6 py-3">Base Price</th>
                <th scope="col" className="px-6 py-3">Quantity</th>
                <th scope="col" className="px-6 py-3">Variations</th>
                <th scope="col" className="px-6 py-3">Subtotal</th>
                 <th scope="col" className="px-6 py-3">Remove</th>
              
            </tr>
           
        </thead>
          
        <tbody>
           {selectedItems.map((row, rowIndex) =>  {
            console.log(selectedItems);
            const product = products.find((p) => p.productId === row.productId);
           
            return(
          <tr className="bg-white border-b border-gray-200 text-gray-500 hover:bg-gray-700 hover:text-white">
            
            
                    <td className="px-6 py-4 max-w-xs break-words"> <select name="productId" id="productId" value={row.productId} onChange={(e) => {
    const selectedProductId = Number(e.target.value);

    setSelectedItems(prev =>
      prev.map((item, i) =>
        i === rowIndex
          ? {
              ...item,
              productId: selectedProductId,
              saleItemQuantity: 1,
              variationOptions: []
            }
          : item
      )
    );
  }} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 " required>
                                    <option value="" className="text-gray-50">Select product</option>
                                    {products.map(p => (
                                        <option key={p.productId} disabled={p.productStock == 0} value={p.productId}>{p.productName}</option>
                                    ))}
                                </select> </td>
                    <td className="px-6 py-4">{product?.productPrice || 0}</td>
                    <td className="px-6 py-4"><form>
                
                <label htmlFor="Line1Qty" className="sr-only"> Quantity </label>
                <button type="button" disabled={!product} onClick={(e) => {
    e.stopPropagation();
    setSelectedItems(prev => {
      const updated = [...prev];
      const sku = skus.find(s => 
  s.product.productId === product.productId &&
  s.variationOptions.length === product.variationOptions?.length &&
  s.variationOptions.every((value, idx) => value.variationOptionId === product.variationOptions[idx].variationOptionId)
  );
  const maxStock = sku?.stockQuantity ?? product.productStock;
      updated[rowIndex] = {
        ...updated[rowIndex],
        saleItemQuantity: Math.max(
  0,
  Math.min(updated[rowIndex].saleItemQuantity + 1, maxStock)
)
      };
      return updated;
    });
  }}  className="text-white m-1 bg-black box-border border cursor-pointer border-transparent hover:bg-dark-strong focus:ring-4 focus:warning-subtle shadow-xs font-medium leading-5 rounded-full text-sm px-3 py-2.5 focus:outline-none">+</button>
                <input type="number" min="1" value={row.saleItemQuantity} id="Line1Qty" className="h-8 w-10 rounded-sm border-gray-200 bg-gray-100 p-0 text-center text-sm text-gray-600 [-moz-appearance:_textfield] focus:outline-hidden [&amp;::-webkit-inner-spin-button]:m-0 [&amp;::-webkit-inner-spin-button]:appearance-none [&amp;::-webkit-outer-spin-button]:m-0 [&amp;::-webkit-outer-spin-button]:appearance-none" />
                <button type="button" disabled={!product} onClick={(e) => {
    e.stopPropagation();
    setSelectedItems(prev => {
      const updated = [...prev];
      updated[rowIndex] = {
        ...updated[rowIndex],
        saleItemQuantity: Math.max(0, updated[rowIndex].saleItemQuantity - 1)
      };
      return updated;
    });
  }} className="text-white m-1 bg-black box-border cursor-pointer border border-transparent hover:bg-dark-strong focus:ring-4 focus:warning-subtle shadow-xs font-medium leading-5 rounded-full text-sm px-3 py-2.5 focus:outline-none">-</button>
                           
                        </form></td>
                    <td className="px-6 py-4">{product?.variations && product.variations.length > 0 ? (
    product?.variations.sort((a, b) => a.variationId - b.variationId).map(v => (
                                  <div className="col-span-2 ">
                                  <label htmlFor="variation" className="block mb-2 text-sm font-medium text-gray-900">{v.variationName}</label>
                                <select name={v.variationName} id={v.variationName} onChange={e => {
                              const optionId = Number(e.target.value);
                              const selectedOption = v.variationOptions.find(o => o.variationOptionId === optionId);
                              handleOptionChange(rowIndex,  v, selectedOption);
  }} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 " required>
                                    <option value="" className="text-gray-50">Select variation</option>
                                    {v.variationOptions.map(o => (
                                        <option key={o.variationOptionId}  disabled={disableOption(rowIndex, o, v.variationId, product)} value={o.variationOptionId}>{o.variationOptionName}</option>
                                    ))}
                                </select>
                                </div>
                                 ))
  ) : (
    <span className="text-gray-500 italic">No variations available</span>
  )}
</td>
                    <td className="px-6 py-4">{(product?.productCost + row.variationOptions.reduce(
            (sum, vo) => sum + (vo.variationPriceAdjustment || 0),
            0
          )) * row.saleItemQuantity || 0}</td>
           <td className="px-6 py-4"> <button
                            type="button"
                            onClick={() =>removeSelectedItems(rowIndex)}
                            className="text-white inline-flex items-center cursor-pointer ml-5 bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center"
                        >
                            Remove
                        </button></td>
               
              
                

                
            </tr>
            );
    })}
        </tbody>
           
    </table>
}
    </div>
                        <button type="submit" disabled={selectedItems.some(item => item.saleItemQuantity === 0)} className="text-white  cursor-pointer inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center">
                         <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                             Add new sale
                        </button>
                    </form>
                        </div>
                    </div>
                </div> 
        
                )}
        
        
                {showModal2 && (<DeleteConfirm onClose={closeModal2} onDeleteClick={() => deleteSale(deleteId)} element="sale" />)}
        

                
        {deleteSuccess && (
            <DeleteSuccessPopup element="Sales record" setDeleteSuccess={setDeleteSuccess} />
        )}
                </div>
        
        
        
        
              </div>
        
              
            </div>
            
    
        )
}

export default Sales
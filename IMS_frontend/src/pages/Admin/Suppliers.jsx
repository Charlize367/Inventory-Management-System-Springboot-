import React from 'react'
import Sidebar from '../../components/Sidebar'
import { useState, useEffect } from "react";
import { Menu, X } from "lucide-react"; 
import Table from '../../components/Table';
import DeleteConfirm from '../../components/DeleteConfirm';
import axios from 'axios';
import AddSuccessPopup from '../../components/AddSuccessPopup';

const Suppliers = () => {
  const [showSidebar, setShowSidebar] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [showModal2, setShowModal2] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const API_URL = import.meta.env.VITE_API_URL;
  const [suppliers, setSuppliers] = useState([]);
  const token = localStorage.getItem('jwtToken');
  const [deleteId, setDeleteId] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize] = useState(10);
  const [showActionPopup, setActionPopup] = useState(false);
  const [popupMessage, setPopupMessage] = useState("");
  const [showPopup, setShowPopup] = useState(false);
  const [retryTime, setRetryTime] = useState(0);
  const [error, setError] = useState(null);
  const [deleteSuccess, setDeleteSuccess] = useState(false);
    

  const openModal = () => setShowModal(true);
  const closeModal = () => setShowModal(false);

  const openModal2 = () => setShowModal2(true);
  const closeModal2 = () => setShowModal2(false);

  const handlePopup = (message) => {
        setPopupMessage(message);
        setActionPopup(true);

        setTimeout(() => {
            setActionPopup(false);
            setPopupMessage("");
        }, 3000); 
        };



    const headers = [
    { key: 'supplierId', header: 'ID' },
    { key: 'supplierName', header: 'Name' },
    { key: 'supplierContactName', header: 'Contact Name' },
    { key: 'supplierEmail', header: 'Email' },
    { key: 'supplierPhone', header: 'Phone Number' },
    { key: 'supplierAddress', header: 'Address' },
    ];

    const columns = [
    {
        Header: 'ID',
        accessor: 'supplierId', 
    },
    {
        Header: 'Name',
        accessor: 'supplierName',
    },
    {
        Header: 'Contact Name',
        accessor: 'supplierContactName',
    },
    {
        Header: 'Email',
        accessor: 'supplierEmail',
    },
    {
        Header: 'Phone Number',
        accessor: 'supplierPhone',
    },
    {
        Header: 'Address',
        accessor: 'supplierAddress',
    },
    
    
    ];

    const [formData, setFormData] = useState({
        supplierId: null,
        supplierName: "",
        supplierContactName: "",
        supplierEmail: "",
        supplierPhone: "",
        supplierAddress: "",
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
        ...prev,
        [name]: value,
        }));
    };

    const handleEdit = async (supplier) => {
        setFormData({
            supplierId: supplier.supplierId,
            supplierName: supplier.supplierName,
            supplierContactName: supplier.supplierContactName,
            supplierEmail: supplier.supplierEmail,
            supplierPhone: supplier.supplierPhone,
            supplierAddress: supplier.supplierAddress,
        });
        setIsEditing(true);
        openModal();
        console.log(supplier);
    }



    const getSuppliers = async (page = 0) => {
       try {
             const response = await axios.get(`${API_URL}/suppliers?page=${page}&size=${pageSize}`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });

              setSuppliers(response.data.content);
              setCurrentPage(response.data.number);
              setTotalPages(response.data.totalPages);
            
              
              
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
      getSuppliers(0);
      
    }, []);

    useEffect(() => {
            if (retryTime > 0) {
              const timer = setInterval(() => setRetryTime((prev) => prev - 1), 1000);
              return () => clearInterval(timer);
            } else if (retryTime === 0 && showPopup) {
              setShowPopup(false); 
            }
          }, [retryTime, showPopup]);


    const handleSubmit = async (e) => {

    e.preventDefault();
    console.log(formData);

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneRegex = /^0\d{10}$/;

    if (!emailRegex.test(formData.supplierEmail)) {
    alert("Invalid email");
    return;
    }

    if (!phoneRegex.test(formData.supplierPhone)) {
    alert("Invalid phone number");
    return;
    }

       try {

        if (isEditing) {
        
            const response = await axios.put(`${API_URL}/suppliers/${formData.supplierId}`, formData, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },  
            });

            console.log(response.data);
              
        
            getSuppliers();
            handlePopup("Supplier edited successfully.");
            closeAndClearForm();

        } else {
             const response = await axios.post(`${API_URL}/suppliers`, formData,   {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });
              console.log(response.data);
              
        
              getSuppliers();
              handlePopup("Supplier added successfully.");
              closeAndClearForm();
              
            }
            } catch (error) {
              console.log(error);
              
            }
    }

    const closeAndClearForm = () => {
        closeModal();
        setIsEditing(false);
        setFormData({
            supplierId: "",
            supplierName: "",
            supplierContactName: "",
            supplierEmail: "",
            supplierPhone: "",
            supplierAddress: "",
        });
    }

    const handleDelete = (id) => {
        openModal2();

        setDeleteId(id);

        
    }

    const deleteSupplier = async(id) => {
        try {
          const response = await axios.delete(`${API_URL}/suppliers/${id}`, {
              headers: {
                        'Authorization' : `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }});

              getSuppliers(); 
              setDeleteSuccess(true);        
              closeModal2();

        } catch (error) {
          console.log(error);
          let msg = "Delete failed";
        if (error.response?.data?.message?.includes("TransientObjectException")) {
        msg = "Cannot delete this supplier because they are linked to other records.";

        window.alert(msg);
         }
        }
    }


  return (

  <div className="grid grid-cols-1 lg:grid-cols-[300px_1fr] gap-4 lg:gap-0 w-full m-0 p-0 overflow-x-hidden min-h-screen">



        <div className="flex items-center justify-between bg-white p-4 lg:hidden">
          <h1 className="text-2xl block rounded-lg bg-white px-4 py-2 font-medium text-gray-700">Suppliers</h1>
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
          <h1 className="text-2xl block rounded-lg px-4 py-2 font-medium text-gray-500">Suppliers</h1>
        </div>

        
        <div className="grid grid-cols-1 gap-4 m-0  bg-gray-200 p-4 flex-grow">
          <Table headers={headers} columns={columns} onAddClick={openModal} onEditClick={handleEdit} disableAdd="false" disableEdit="false" data={suppliers} onDeleteClick={handleDelete} currentPage={currentPage} totalPages={totalPages} fetchData={getSuppliers}  />

        {showPopup && (
                <div className="overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 flex justify-center  items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full bg-black">
                <div className="bg-white p-6 rounded shadow-lg w-80 text-center">
                    <h3 className="text-lg font-bold mb-2">{error}</h3>
                    <p>Please wait {retryTime} second{retryTime !== 1 ? "s" : ""} before trying again.</p>
                    <button
                    onClick={() => { setShowPopup(false); getSuppliers(currentPage); }}
                    disabled={retryTime > 0}
                    className={`text-white inline-flex items-center cursor-pointer bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center ${
                        retryTime > 0 ? "opacity-50 cursor-not-allowed" : ""
                    }`}
                    >
                    Retry Now
                    </button>
                </div>
                </div>
            )}

        {showModal && (
        <div id="crud-modal" tabIndex="-1" className=" overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 flex justify-center  items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
            <div className="relative p-4 w-full max-w-md max-h-full">
            
                <div className="relative bg-gray-100 rounded-lg shadow-sm ">
                
                    <div className="flex items-center justify-between p-4 md:p-5 rounded-t dark:border-gray-600 border-gray-100">
                        <h3 className="text-lg font-semibold text-gray-900">
                            {isEditing ? "Edit supplier" : "Add New supplier"}
                        </h3>
                        <button type="button" onClick={closeAndClearForm} className="text-gray-400 cursor-pointer bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-toggle="crud-modal">
                            <svg className="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                            </svg>
                            <span className="sr-only">Close modal</span>
                        </button>
                    </div>
                    
                    <form onSubmit={handleSubmit} className="p-4 md:p-5">
                        <div className="grid gap-4 mb-4 grid-cols-2">
                            <div className="col-span-2">
                                <label htmlFor="supplierName" className="block mb-2 text-sm font-medium text-gray-900">Name</label>
                                <input type="text" name="supplierName" id="supplierName" value={formData.supplierName} onChange={handleChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder="Name" required/>
                            </div>
                            <div className="col-span-2">
                                <label htmlFor="supplierContactName" className="block mb-2 text-sm font-medium text-gray-900">Contact Name</label>
                                <input type="text" name="supplierContactName" id="supplierContactName" value={formData.supplierContactName} onChange={handleChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder="Name" required/>
                            </div>
                            <div className="col-span-2">
                                <label htmlFor="supplierEmail" className="block mb-2 text-sm font-medium text-gray-900">Email</label>
                                <input type="text" name="supplierEmail" id="supplierEmail" value={formData.supplierEmail} onChange={handleChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder=" Email" required/>
                            </div>
                             <div className="col-span-2">
                                <label htmlFor="supplierAddress" className="block mb-2 text-sm font-medium text-gray-900">Address</label>
                                <textarea name="supplierAddress" id="supplierAddress" value={formData.supplierAddress} onChange={handleChange} rows="4" className="block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 " placeholder="Address" required></textarea>                    
                            </div>
                            <div className="col-span-2">
                                <label htmlFor="price" className="block mb-2 text-sm font-medium text-gray-900">Phone Number</label>
                                <input type="text" name="supplierPhone" id="supplierPhone" value={formData.supplierPhone} onChange={handleChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder="Phone Number" required/>
                            </div>

                        
                        
                        </div>
                        <button type="submit" className="text-white cursor-pointer inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center">
                            <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                            {isEditing ? "Update" : "Add"}
                        </button>
                    </form>
                </div>
            </div>
        </div> 

        )}


        {showModal2 && (<DeleteConfirm onClose={closeModal2} onDeleteClick={() => deleteSupplier(deleteId)} element="supplier" />)}

        {showActionPopup && (
            <AddSuccessPopup message={popupMessage} />
        )}

        {deleteSuccess && (
            <DeleteSuccessPopup element="Supplier" />
        )}
        </div>




      </div>

      
    </div>
    
        )

}

export default Suppliers
import React from 'react'
import Sidebar from '../../components/Sidebar'
import { useState, useEffect } from "react";
import { Menu, X } from "lucide-react"; 
import Table from '../../components/Table';
import DeleteConfirm from '../../components/DeleteConfirm';
import axios from 'axios';
import AddSuccessPopup from '../../components/AddSuccessPopup';
import DeleteSuccessPopup from '../../components/DeleteSuccessPopup';

const Variations = () => {
    const [showSidebar, setShowSidebar] = useState(false);
    const API_URL = import.meta.env.VITE_API_URL;
    const token = localStorage.getItem('jwtToken');
    const [isEditing, setIsEditing] = useState(false);
    const [isEditing2, setIsEditing2] = useState(false);
    const [variations, setVariations] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [showModal2, setShowModal2] = useState(false);
    const [showModal3, setShowModal3] = useState(false);
    const [showModal4, setShowModal4] = useState(false);
    const [error, setError] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [pageSize] = useState(5);
    const [retryTime, setRetryTime] = useState(0);
    const [showPopup, setShowPopup] = useState(false);
    const [showActionPopup, setActionPopup] = useState(false);
    const [popupMessage, setPopupMessage] = useState("");
    const [deleteSuccess, setDeleteSuccess] = useState(false);
    const [deleteId, setDeleteId] = useState(0);
    const [optionList, setOptionList] = useState([]);
    
    const [selectedVariationOptions, setSelectedVariationOptions] = useState([]);
    

console.log(variations);
console.log(optionList);


    const openModal = () => setShowModal(true);
    const closeModal = () => {
         setFormData({
            variationId: null,
            variationName: "",
        });
        setShowModal(false);
    }

    const openModal2 = () => setShowModal2(true);
    const closeModal2 = () => setShowModal2(false);

    const openModal3 = () => setShowModal3(true);
    const closeModal3 = () => {
         setFormData2({
            variationOptionsId: null,
            variationOptionsName: "",
            variationPriceAdjustment: 0.0,
            variationOptionCode: ""
        });
        setShowModal3(false);
    }

     const openModal4 = () => setShowModal4(true);
    const closeModal4 = () => setShowModal4(false);

    console.log(variations);


    const handlePopup = (message) => {
        setPopupMessage(message);
        setActionPopup(true);

        setTimeout(() => {
            setActionPopup(false);
            setPopupMessage("");
        }, 3000); 
        };

    const headers = [
    { key: 'variationId', header: 'ID' },
    { key: 'variationName', header: 'Variation' },
    { key: 'variationOptions', header: 'Options' },
    
    ];

    const columns = [
    {
        Header: 'ID',
        accessor: 'variationId', 
    },
    {
        Header: 'Variation',
        accessor: 'variationName',
    },
    {
        Header: 'Options',
        accessor: 'variationOptions.variationOptionName',
    },
    ];

    const headers2 = [
    { key: 'variationOptionId', header: 'ID' },
    { key: 'variationOptionName', header: 'Option' },
    
    ];

    const columns2 = [
    {
        Header: 'ID',
        accessor: 'variationOptionId', 
    },
    {
        Header: 'Option',
        accessor: 'variationOptionName',
    },
    
    ];
    

    const [formData, setFormData] = useState({
        variationId: null,
        variationName: "",
    });

    console.log(formData);

     const [formData2, setFormData2] = useState({
        variationOptionId: null,
        variationOptionName: "",
        variationPriceAdjustment: Number(0.0),
        variationOptionCode: ""
    });

    const handleOptionEditChange = (e) => {
        const { name, value } = e.target;
        setFormData2((prev) => ({
        ...prev,
        [name]: value,
        }));

       
   
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
        ...prev,
        [name]: value,
        }));
    };

    const handleEdit = async (variation) => {
        setFormData({
            variationId: variation.variationId,
            variationName: variation.variationName
        });

        setOptionList(
        variation.variationOptions ?? []
    );
    
        setIsEditing(true);
        openModal();
        console.log(variation);
    }

    const handleEdit2 = async (variationOption) => {
        setFormData2({
            variationOptionId: variationOption.variationOptionId,
            variationOptionName: variationOption.variationOptionName,
            variationPriceAdjustment: variationOption.variationPriceAdjustment
        });

        setIsEditing2(true);
        openModal3();
        console.log(variationOption);
    }
 
    const getVariations = async(page = 0) => {
       try {
             const response = await axios.get(`${API_URL}/variations?page=${page}&size=${pageSize}`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });
              console.log(response);

              setVariations(response.data.content);
              setCurrentPage(response.data.number);
              setTotalPages(response.data.totalPages);
              setShowPopup(false);
              setRetryTime(0);
              setError(null);
            
              
              
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
      getVariations(0);
      
    }, []);

    

     useEffect(() => {
    if (retryTime > 0) {
      const timer = setInterval(() => setRetryTime((prev) => prev - 1), 1000);
      return () => clearInterval(timer);
    } else if (retryTime === 0 && showPopup) {
      setShowPopup(false); 
    }
  }, [retryTime, showPopup]);



    

    const handleSubmit = async(e) => {

    e.preventDefault();
    console.log(formData);

   
    

       try {
        if (isEditing) {
        
            const updatedVarName = {
                "variationName" : formData.variationName
            };

            const response = await axios.put(`${API_URL}/variations/${formData.variationId}`, updatedVarName, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },  
            });

            console.log(response.data);
              
        
            getVariations();
            handlePopup("Variation edited successfully.");
            closeAndClearForm(formData.variationId);
            

            
        } else {

            const payload = {
    ...formData,
    variationOptions: optionList
};
             const response = await axios.post(`${API_URL}/variations`, payload,   {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });
              console.log(response.data);
              console.log(formData);
              
        
            getVariations();
            
              handlePopup("Variation added successfully.");
              closeAndClearForm();
              closeModal();
              
            }
            } catch (error) {
              console.log(error);
              
            }
    }

    

    const handleSubmit2 = async(e) => {
        e.preventDefault();
    console.log(formData);

   

       try {
        if (isEditing2) {
        
            const updatedVarOptions = {
                "variationOptionName" : formData2.variationOptionName,
                "variationPriceAdjustment" : formData2.variationPriceAdjustment,
            };

            const response = await axios.put(`${API_URL}/variationOptions/${formData2.variationOptionId}`, updatedVarOptions, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },  
            });

            getVariations();
            console.log(response.data);
            setOptionList(prev =>
                prev.map(opt =>
                    opt.variationOptionId === response.data.variationOptionId ? response.data : opt
                )
            );

            
     setVariations(prev =>
                prev.map(v =>
                    v.variationId === formData.variationId
                        ? { ...v, variationOptions: optionList }
                        : v
                )
            );
              
        
            
            handlePopup("Variation option edited successfully.");
            closeAndClearForm2(formData2.variationOptionId);
            

        } else {
             const response = await axios.post(`${API_URL}/variationOptions/variation/${formData.variationId}`, formData2,   {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });
              console.log(response);
              console.log(response.data);

              getVariations();

               setOptionList(prev =>
    prev.map(opt =>
        opt.variationOptionId === response.data.variationOptionId ? response.data : opt
    )
);


               setVariations(prev =>
                prev.map(v =>
                    v.variationId === formData.variationId
                        ? { ...v, variationOptions: optionList }
                        : v
                )
            );
              
        
              handlePopup("Variation added successfully.");
              closeAndClearForm2();
              
            }
            } catch (error) {
              console.log(error);
              
              
            }
    }


console.log(formData2);


const handleOptionAddChange = (index, field, value) => {
//   const updatedOptions = [...variationOptions];
//   updatedOptions[index][field] = value;
//   setVariationOptions(updatedOptions);
  setOptionList(prev =>
    prev.map((opt, idx) =>
      idx === index ? { ...opt, [field]: value } : opt
)
  );
};


    const closeAndClearForm = (variationId) => {
       
        setIsEditing(false);
        
        setFormData({
            variationId: variationId,
            variationName: "",
            variationOptions: []
        });
        setOptionList([]);
        closeModal();
    }

    const closeAndClearForm2 = (variationOptionId) => {
       
        setFormData2({
            variationOptionsId: variationOptionId,
            variationOptionName: "",
            variationPriceAdjustment: 0.0,
            variationOptionCode: ""
        });
        
        closeModal3();
        if(isEditing2) {
            setIsEditing2(false);
        }
        
    }

    const handleDelete = (id) => {
        setShowModal4(false);
        openModal2();

        setDeleteId(id);

        
    }


    const deleteVariation = async(id) => {
        try {
          const response = await axios.delete(`${API_URL}/variations/${id}`, {
              headers: {
                        'Authorization' : `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }});

              getVariations();     
              setDeleteSuccess(true);
              closeModal2();

        } catch (error) {
          console.log(error);
          let msg = "Delete failed";
        if (error.response?.data?.message?.includes("TransientObjectException")) {
        msg = "Cannot delete this variation because they are linked to other records.";

        window.alert(msg);
         }
        }
    }

    const addOption = () => {
  setOptionList(prev => [
    ...prev,
    { variationOptionName: "", variationOptionCode: "", variationPriceAdjustment: 0 }
  ]);
};

const removeOption = (index) => {
  setOptionList(prev => prev.filter((_, i) => i !== index));
};



    const handleDelete2 = (id) => {
        setShowModal2(false)
        openModal4();

        setDeleteId(id);

        
    }

    console.log(optionList);

    const deleteVariationOption = async(id) => {
        try {
          const response = await axios.delete(`${API_URL}/variationOptions/${id}`, {
              headers: {
                        'Authorization' : `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }});

              getVariations();     
              setDeleteSuccess(true);
              closeModal4();

        } catch (error) {
          console.log(error);
          let msg = "Delete failed";
        if (error.response?.data?.message?.includes("TransientObjectException")) {
        msg = "Cannot delete this variation because they are linked to other records.";

        window.alert(msg);
         } else {
             msg = "Cannot delete this variation because they are linked to other records.";
            window.alert(msg);
         }
        }
    }

    console.log(formData2);

const currentVariation = variations.find(v => v.variationId === formData.variationId);
  return (
        
        <div className="grid grid-cols-1 lg:grid-cols-[300px_1fr] gap-4 lg:gap-0 w-full m-0 p-0 overflow-x-hidden min-h-screen">



        <div className="flex items-center justify-between bg-white p-4 lg:hidden">
          <h1 className="text-2xl block rounded-lg bg-white px-4 py-2 font-medium text-gray-700">Variations</h1>
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
          <h1 className="text-2xl block rounded-lg px-4 py-2 font-medium text-gray-500">Variations</h1>
        </div>

        
        <div className="grid grid-cols-1 gap-4 m-0  bg-gray-200 p-4 flex-grow">
          <Table headers={headers} columns={columns} onAddClick={openModal} onEditClick={handleEdit} disableAdd="false" disableEdit="false" data={variations} onDeleteClick={handleDelete} currentPage={currentPage} totalPages={totalPages} fetchData={getVariations}  />


        {showPopup && (
                <div className="overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 flex justify-center  items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full bg-black">
                <div className="bg-white p-6 rounded shadow-lg w-80 text-center">
                    <h3 className="text-lg font-bold mb-2">{error}</h3>
                    <p>Please wait {retryTime} second{retryTime !== 1 ? "s" : ""} before trying again.</p>
                    <button
                    onClick={() => { setShowPopup(false); getVariations(currentPage); }}
                    disabled={retryTime > 0}
                    className={`text-white inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center ${
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
                            {isEditing ? "Edit Variation" : "Add New Variation"}
                        </h3>
                        <button type="button" onClick={closeAndClearForm} className="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-toggle="crud-modal">
                            <svg className="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                            </svg>
                            <span className="sr-only">Close modal</span>
                        </button>
                    </div>
                    
                    <form onSubmit={handleSubmit} className="p-4 md:p-5">
                       
                            <div className="col-span-2">
                                <label htmlFor="variationName" className="block mb-2 text-sm font-medium text-gray-900">Name</label>
                                <input type="text" name="variationName" id="variationName" value={formData.variationName} onChange={handleChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 mb-5 " placeholder="Name" required/>
                            </div>

                            {isEditing &&
                        <button type="submit" className="text-white inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-1 mb-3 text-center">
                            <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                           Update
                        </button>
                        }
                        
                        
                        
                        <label htmlFor="variationName" className="block mb-2 text-sm font-medium text-gray-900">Options</label>
                        {isEditing ?
                        
                        <Table headers={headers2} columns={columns2} onAddClick={openModal3}  disableAdd="false" disableEdit="false" data={currentVariation?.variationOptions ?? []} onEditClick={handleEdit2} onDeleteClick={handleDelete2} disablePagination="true" /> :
                        <div>
                            {optionList.map((option, index) => (
                            <div className="mb-4 rounded" key={index}>
                            <div className="mb-2">
                            <label htmlFor="variationOptionName" className="block text-sm font-medium text-gray-900">Option {index + 1}</label>
                            <input
                                key={index}
                                placeholder="Option Name"
                                type="text"
                                value={option.variationOptionName}
                               onChange={(e) => handleOptionAddChange(index, 'variationOptionName', e.target.value)}
                                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 "
                            required/>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                            <div>
                            <label htmlFor="variationOptionCode" className="block text-sm font-medium text-gray-900">Option Code {index + 1}</label>
                            <input
                                key={index}
                                placeholder="Code"
                                type="text"
                                value={option.variationOptionCode}
                                onChange={(e) => handleOptionAddChange(index, 'variationOptionCode', e.target.value)}
                                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 "
                            required/>
                             </div>
                            <div className="flex-2">
                            <label htmlFor="variationPriceAdjustment" className="block text-sm font-medium text-gray-900">Price adjustment {index + 1}</label>
                            <input
                                key={index}
                                placeholder="Price Adjustment"
                                type="number"
                                value={option.variationPriceAdjustment}
                                onChange={(e) => handleOptionAddChange(index, 'variationPriceAdjustment', e.target.value)}
                                className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 "
                            required/>
                            </div>
                            </div>
                            <button
                            type="button"
                            onClick={() =>removeOption(index)}
                            className="text-white inline-flex items-center ml-5 bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center"
                        >
                            Remove Option
                        </button>
                            </div>
                            
                        ))}
                        <button
                            type="button"
                            onClick={addOption}
                            className="text-white inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center"
                        >
                            Add Option
                        </button>
                        

                        </div>}
                         {!isEditing &&
                        <button  type="submit" className="text-white inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center">
                            <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                           Add
                        </button>
                        }
                    </form>
                    
                 
                </div>
                
            </div>


        
        </div> 


        )}



        {showModal2 && (<DeleteConfirm onClose={closeModal2} onDeleteClick={() => deleteVariation(deleteId)} element="variation" />)}

        {showModal4 && (<DeleteConfirm onClose={closeModal4} onDeleteClick={() => deleteVariationOption(deleteId)} element="variationOption" />)}

        {showModal3 && (
        <div id="crud-modal" tabIndex="-1" className=" overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 flex justify-center  items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
            <div className="relative p-4 w-full max-w-md max-h-full">
            
                <div className="relative bg-gray-100 rounded-lg shadow-sm ">
                
                    <div className="flex items-center justify-between p-4 md:p-5 rounded-t dark:border-gray-600 border-gray-100">
                        <h3 className="text-lg font-semibold text-gray-900">
                            {isEditing2 ? "Edit Variation Option" : "Add New Variation Option"}
                        </h3>
                        <button type="button" onClick={closeAndClearForm2} className="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-toggle="crud-modal">
                            <svg className="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                            </svg>
                            <span className="sr-only">Close modal</span>
                        </button>
                    </div>
                    
                    <form onSubmit={handleSubmit2} className="p-4 md:p-5">
                       
                            <div className="col-span-2">
                                <label htmlFor="variationOptionName" className="block mb-2 text-sm font-medium text-gray-900">Name</label>
                                <input type="text" name="variationOptionName" id="variationOptionName" value={formData2.variationOptionName} onChange={handleOptionEditChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 mb-5 " placeholder="Name" required/>
                            </div>
                             <div className="col-span-2">
                                <label htmlFor="variationPriceAdjustment" className="block mb-2 text-sm font-medium text-gray-900">Price Adjustment</label>
                                <input type="number" name="variationPriceAdjustment" id="variationPriceAdjustment" value={formData2.variationPriceAdjustment} onChange={handleOptionEditChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 mb-5 " placeholder="Name" required/>
                            </div>
                            {!isEditing2 &&
                             <div className="col-span-2">
                                <label htmlFor="variationOptionCode" className="block mb-2 text-sm font-medium text-gray-900">Code</label>
                                <input type="text" name="variationOptionCode" id="variationOptionCode" value={formData2.variationOptionCode} onChange={handleOptionEditChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 mb-5 " placeholder="Name" required/>
                            </div>
                            }

                        {isEditing2 &&
                        <button type="submit" className="text-white inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-1 mb-3 text-center">
                            <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                           Update
                        </button>
                        }
                        
                        
                    
                        
                         {!isEditing2 &&
                        <button type="submit" className="text-white inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-4 text-center">
                            <svg className="me-1 -ms-1 w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                           Add
                        </button>
                        }
                    </form>
                    
                 
                </div>
                
            </div>


        
        </div> 


        )}

        {showActionPopup && (
            <AddSuccessPopup message={popupMessage} />
        )}

        
        {deleteSuccess && (
            <DeleteSuccessPopup element="Variation" setDeleteSuccess={setDeleteSuccess} />
        )}
  
</div>
</div>
</div>

  )
}

export default Variations
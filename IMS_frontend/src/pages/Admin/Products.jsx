import React from 'react'
import Sidebar from '../../components/Sidebar'
import { useState, useEffect, useRef} from "react";
import { Menu, Type, X } from "lucide-react"; 
import ProductTable from '../../components/ProductTable';
import DeleteConfirm from '../../components/DeleteConfirm';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import AddSuccessPopup from '../../components/AddSuccessPopup';
import DeleteSuccessPopup from '../../components/DeleteSuccessPopup';
import { Listbox, ListboxButton, ListboxOption, ListboxOptions } from '@headlessui/react'

const Products = () => {
  const [showSidebar, setShowSidebar] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [showModal2, setShowModal2] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const API_URL = import.meta.env.VITE_API_URL;
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
  const [products, setProducts] = useState([]);
  const token = localStorage.getItem('jwtToken');
  const [deleteId, setDeleteId] = useState(0);
  const [category, setCategory] = useState([]);
  const userId = Number(localStorage.getItem("userId"));
  const param = useParams();
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize] = useState(5);
  const [showActionPopup, setActionPopup] = useState(false);
  const [popupMessage, setPopupMessage] = useState("");
  const [showPopup, setShowPopup] = useState(false);
  const [retryTime, setRetryTime] = useState(0);
  const [error, setError] = useState(null);
  const [deleteSuccess, setDeleteSuccess] = useState(false);
  const [suppliers, setSuppliers] = useState([]);
  const [brands, setBrands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [variations, setVariations] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [selectedVariations, setSelectedVariations] = useState([]);
  const [productImage, setProductImage] = useState(null);
  const [categoryOpen, setCategoryOpen] = useState(false);
  const [brandOpen, setBrandOpen] = useState(false);
  const categoryRef = useRef(null);
  const brandRef = useRef(null);
  const [categoryId, setCategoryId] = useState(0);
  const [brandId, setBrandId] = useState(0);


  useEffect(() => {
    const handleClickOutside = (event) => {
      if (categoryRef.current && !categoryRef.current.contains(event.target)) {
        setCategoryOpen(false);
      }
      if (brandRef.current && !brandRef.current.contains(event.target)) {
        setBrandOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

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
    { key: 'productId', header: 'ID' },
    { key: 'productImage', header: 'Image' },
    { key: 'productName', header: 'Name' },
    { key: 'productDescription', header: 'Description' },
    { key: 'productPrice', header: 'Price' },
    { key: 'productCost', header: 'Cost' },
    { key: 'productSupplier', header: 'Supplier' },
    { key: 'productStock', header: 'Stock' },
    { key: 'categories', header: 'Categories' },
    { key: 'brand', header: 'Brand' },
    { key: 'variations', header: 'Variations' },
    
    ];

    const columns = [
    {
        Header: 'ID',
        accessor: 'productId', 
    },
    {
        Header: 'Image',
        accessor: 'productImage',
    },
    {
        Header: 'Name',
        accessor: 'productName',
    },
    {
        Header: 'Description',
        accessor: 'productDescription',
    },
    {
        Header: 'Price',
        accessor: 'productPrice',
    },
    {
        Header: 'Cost',
        accessor: 'productCost',
    },
    {
        Header: 'Supplier',
        accessor: 'supplier.supplierName',
    },
    {
        Header: 'Stock',
        accessor: 'productStock',
    },
    {
        Header: 'Categories',
        accessor: 'categories.categoryName',
    },
    {
        Header: 'Brand',
        accessor: 'brand.brandName',
    },
    {
        Header: 'Variations',
        accessor: 'variations',
    },
    
    
    
    ];

    const handleImageChange = (e) => {
  setProductImage(e.target.files[0]);
};

    

  const [formData, setFormData] = useState({
          productId: null,
          productName: "",
          productCode: "",
          productDescription: "",
          productPrice: 0.0,
          productCost: 0.0,
          productStock: 0,
          staffId: userId,
          brandId: 0,
          supplierId: 0,
          productImage: null

      });
  
    const handleChange = (e) => {
            const { name, value, files, type } = e.target;
        setFormData({ ...formData, [name]: type === "file" ? files[0] : type === "number" || name === "brandId" || name === "supplierId" ? Number(value) : value,
            });
    }

  console.log(formData);
      const handleEdit = async (product) => {
          setFormData({
            productId: product.productId,
            productName: product.productName,
            productCode: product.productCode,
            productDescription: product.productDescription,
            productPrice: product.productPrice,
            productCost: product.productCost,
            productStock: product.productStock,
            staffId: userId,
            brandId:product.brand.brandId,
            supplierId:product.supplier.supplierId
          });

          setProductImage(product.productImage);
          setSelectedCategories(product.categories.map(c => c.categoryId));
          setSelectedVariations(product.variations.map(v => v.variationId));
          setIsEditing(true);
          openModal();
          console.log(product);
      }
    

console.log(selectedCategories);

console.log(products);
  const getSuppliers = async () => {
       try {
             const response = await axios.get(`${API_URL}/suppliers/all`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });

              setSuppliers(response.data);

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
      getSuppliers();
      
    }, []);

    const getBrands = async () => {
       try {
             const response = await axios.get(`${API_URL}/brands/all`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });

              setBrands(response.data);

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
      getBrands();
      
    }, []);

    const getCategories = async () => {
       try {
             const response = await axios.get(`${API_URL}/categories/all`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });

              setCategories(response.data);

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
      getCategories();
      
    }, []);

    const getVariations = async () => {
       try {
             const response = await axios.get(`${API_URL}/variations/all`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });

              setVariations(response.data);

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
      getVariations();
      
    }, []);


    const getProducts = async (page = 0) => {
      
       try {
        let endpoint = ``;
        if (categoryId !== 0 && brandId !== 0) {
        endpoint = `products?page=${page}&size=${pageSize}&categoryId=${categoryId}&brandId=${brandId}`;
        } else if (categoryId !== 0) {
        endpoint = `products?categoryId=${categoryId}`;
        } else if (brandId !== 0) {
        endpoint = `products?brandId=${brandId}`;
        } else {
        endpoint = `products?page=${page}&size=${pageSize}`;
        }
             const response = await axios.get(`${API_URL}/${endpoint}`, {
                  headers: {
                       'Content-Type': 'application/json',
                       'Authorization': `Bearer ${token}`
                  }
              });

              setProducts(response.data.content);
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
      getProducts(0);
      
    }, [categoryId, brandId]);

    useEffect(() => {
                if (retryTime > 0) {
                  const timer = setInterval(() => setRetryTime((prev) => prev - 1), 1000);
                  return () => clearInterval(timer);
                } else if (retryTime === 0 && showPopup) {
                  setShowPopup(false); 
                }
              }, [retryTime, showPopup]);

         

    const uploadImage = async() => {

      let productPayload = { ...formData };
      try{

        const imageData = {
                filename: productImage.name,
                contentType: productImage.type,
                fileSize: productImage.size

        }

        const { data } = await axios.post(`${API_URL}/images/initiate`, imageData,  {
                            headers: {
                                'Authorization' : `Bearer ${token}`,
                                'Content-Type': 'application/json'
                            }
                });


                const { uploadUrl, key } = data;

              await axios.put(uploadUrl, productImage, {
                headers: {
                  'Content-Type': productImage.type,
                },
              });

              productPayload = {
              ...formData,
              productImage: key, 
            };
      } catch(error) {
        console.log(error);
      }
    }


    const handleSubmit = async (e) => {

    e.preventDefault();
    
    if (formData.productDescription > 100) {
    alert("Description must not exceed 100 characters.");
    return;
    }

    if (!productImage) {
        return alert("Please select an image");
    }

    
       try {

        if (isEditing) {

          if(productImage) {
            uploadImage();
              
          }

            const response = await axios.put(`${API_URL}/products/${formData.productId}`, formData, {
              headers: {
                 "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
            });

            console.log(response);
            getProducts();
            handlePopup("Product details edited successfully.");
            closeAndClearForm();

            

        } else {

        uploadImage();

          const response = await axios.post(`${API_URL}/products`, formData, {
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
            });

            

        
            console.log(response);
             getProducts();
            handlePopup("Product added successfully.");
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
          productId: null,
          productName: "",
          productCode: "",
          productDescription: "",
          productPrice: 0.0,
          productCost: 0.0,
          productStock: 0,
          productImage: null,
          staffId: 0
        });
        setSelectedCategories([]);
        setSelectedVariations([]);
    }

    const handleDelete = (id) => {
        openModal2();

        setDeleteId(id);

        
    }

    console.log(deleteId);
    const deleteProduct = async(id) => {
        try {
          const response = await axios.delete(`${API_URL}/products/${id}`, {
              headers: {
                        'Authorization' : `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }});

              getProducts();     
              setDeleteSuccess(true);  
              closeModal2();
              

        } catch (error) {
          console.log(error);
        }
    }

    useEffect(() => {
        setFormData(prev => ({
            ...prev,
            categoryIds: selectedCategories
        }));
        }, [selectedCategories]);
    

        useEffect(() => {
            setFormData(prev => ({
                ...prev,
                variationIds: selectedVariations
            }));
            }, [selectedVariations]);
        


  return (

  <div className="grid grid-cols-1 lg:grid-cols-[300px_1fr] gap-4 lg:gap-0 w-full m-0 p-0 overflow-x-hidden min-h-screen">
    
    


        <div className="flex items-center justify-between bg-white p-4 lg:hidden">
          <h1 className="text-2xl block rounded-lg bg-white px-4 py-2 font-medium text-gray-700">{category}</h1>
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
          <h1 className="text-2xl block rounded-lg px-4 py-2 font-medium text-gray-500">{category}</h1>
        </div>

      
        <div className="grid grid-cols-1 gap-4 m-0  bg-gray-200 p-4 flex-grow">
          <div className="flex gap-2">

      <div className="relative" ref={categoryRef}>
        <button
          onClick={() => setCategoryOpen(!categoryOpen)}
          className="inline-flex items-center justify-center text-sm px-3 py-2 rounded bg-gray-200 hover:bg-gray-300"
        >
          Sort By Category
          <svg
            className="w-4 h-4 ml-1"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            viewBox="0 0 24 24"
          >
            <path strokeLinecap="round" strokeLinejoin="round" d="m19 9-7 7-7-7" />
          </svg>
        </button>

        {categoryOpen && (
        <div className="absolute mt-1 w-40 bg-white rounded shadow-lg z-50">
          <ul className="p-2 text-sm text-gray-700">
            {categories.map((c) => {
              const selected = Number(categoryId) === Number(c.categoryId);

              return (
                <li key={c.categoryId}>
                  <button
                    onClick={() => {
                      if (selected) {
                        setCategoryId(0); 
                      } else {
                        setCategoryId(c.categoryId);
                      }
                    }}
                    className={`w-full text-left p-2 hover:bg-gray-100 rounded
                      ${selected ? "bg-gray-200" : ""}`}
                  >
                    {c.categoryName}
                  </button>
                </li>
              );
            })}
          </ul>
        </div>
      )}
      </div>

      
      <div className="relative" ref={brandRef}>
        <button
          onClick={() => setBrandOpen(!brandOpen)}
          className="inline-flex items-center justify-center text-sm px-3 py-2 rounded bg-gray-200 hover:bg-gray-300"
        >
          Sort By Brand
          <svg
            className="w-4 h-4 ml-1"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            viewBox="0 0 24 24"
          >
            <path strokeLinecap="round" strokeLinejoin="round" d="m19 9-7 7-7-7" />
          </svg>
        </button>

       {brandOpen && (
        <div className="absolute mt-1 w-40 bg-white rounded shadow-lg z-50">
          <ul className="p-2 text-sm text-gray-700">
            {brands.map((b) => {
              const selected = Number(brandId) === Number(b.brandId);

              return (
                <li key={b.brandId}>
                  <button
                    onClick={() => {
                      if (selected) {
                        setBrandId(0); 
                      } else {
                        setBrandId(b.brandId);
                      }
                    }}
                    className={`w-full text-left p-2 hover:bg-gray-100 rounded
                      ${selected ? "bg-gray-200" : ""}`}
                  >
                    {b.brandName}
                  </button>
                </li>
              );
            })}
          </ul>
        </div>
      )}
      </div>
    </div>


          <ProductTable columns={columns} headers={headers} onAddClick={openModal} onEditClick={handleEdit} data={products} onDeleteClick={handleDelete} currentPage={currentPage} totalPages={totalPages} fetchData={getProducts} />

        {showPopup && (
                <div className="overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 flex justify-center  items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full bg-black">
                <div className="bg-white p-6 rounded shadow-lg w-80 text-center">
                    <h3 className="text-lg font-bold mb-2">{error}</h3>
                    <p>Please wait {retryTime} second{retryTime !== 1 ? "s" : ""} before trying again.</p>
                    <button
                    onClick={() => { setShowPopup(false); getProducts(currentPage); }}
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
                            {isEditing ? "Edit Product" : "Add New Product"}
                        </h3>
                        <button type="button" onClick={closeAndClearForm} className="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white" data-modal-toggle="crud-modal">
                            <svg className="w-3 h-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
                                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"/>
                            </svg>
                            <span className="sr-only">Close modal</span>
                        </button>
                    </div>
                    
                    <form onSubmit={handleSubmit} className="p-4 md:p-5">
                        <div className="grid gap-4 mb-4 grid-cols-2">
                            <div className="col-span-2">
                                <label htmlFor="name" className="block mb-2 text-sm font-medium text-gray-900">Name</label>
                                <input type="text" value={formData.productName} onChange={handleChange} name="productName" id="productName" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder="Name" required/>
                            </div>
                            <div className="col-span-2">
                                <label htmlFor="name" className="block mb-2 text-sm font-medium text-gray-900">Code</label>
                                <input type="text" value={formData.productCode} onChange={handleChange} name="productCode" id="productCode" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder="Name" required/>
                            </div>
                            <div className="col-span-2">
                                <label htmlFor="description" className="block mb-2 text-sm font-medium text-gray-900">Description</label>
                                <textarea value={formData.productDescription} onChange={handleChange} name="productDescription" id="productDescription" rows="4" className="block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-lg border border-gray-300 focus:ring-blue-500 focus:border-blue-500 " placeholder="Description" required></textarea>                    
                            </div>
                            
                            <div className="col-span-2 sm:col-span-1">
                                <label htmlFor="price" className="block mb-2 text-sm font-medium text-gray-900">Price</label>
                                <input type="number" value={formData.productPrice} onChange={handleChange} name="productPrice" id="productPrice" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder="Price" required/>
                            </div>
                           <div className="col-span-2 sm:col-span-1">
                                <label htmlFor="price" className="block mb-2 text-sm font-medium text-gray-900">Cost </label>
                                <input type="number" value={formData.productCost} onChange={handleChange} name="productCost" id="productCost" className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder="Cost" required/>
                            </div>
                            <div className="col-span-2 sm:col-span-1">
                                <label htmlFor="supplierId" className="block mb-2  text-sm font-medium text-gray-900">Supplier</label>
                                <select name="supplierId" id="supplierId" value={formData.supplierId} onChange={handleChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 " required>
                                    <option value="" className="text-gray-50">Select supplier</option>
                                    {suppliers.map(s => 
                                    <option value={Number(s.supplierId)}>{s.supplierName}</option>
                                   )}
                                </select>
                            </div>
                            <div className="col-span-2 sm:col-span-1">
                                <label htmlFor="brandId" className="block mb-2  text-sm font-medium text-gray-900">Brand</label>
                                <select name="brandId" id="brandId" value={formData.brandId} onChange={handleChange} className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 " required>
                                    <option value="" className="text-gray-50">Select brand</option>
                                    {brands.map(b => 
                                    <option value={Number(b.brandId)}>{b.brandName}</option>
                                   )}
                                </select>
                            </div>
                        <div className="col-span-2">
                                <label className="block mb-2 text-sm font-medium text-gray-900">Select Categories</label>

                                <div className="relative">
                                  
                                    <Listbox
                                      value={selectedCategories}
                                      onChange={(ids) => setSelectedCategories(ids)}
                                      multiple
                                    >
                                    
                                    <ListboxButton className="w-full bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg p-2.5 text-left focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                                        {selectedCategories.length > 0
                                        ? selectedCategories.map(id => categories.find(c => c.categoryId === id)?.categoryName || 'Unknown').join(', ')
                                    : 'Select items'}
                                    </ListboxButton>

                                   
                                    <ListboxOptions className="absolute z-50 mt-1 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-auto focus:outline-none">
                            
            
                                        {categories.map((c) => {
                                        const selectedItem = selectedCategories.find(i => i.categoryId === c.categoryId);
                                            return(
                                        <ListboxOption
                                            key={c.categoryId}
                                            value={Number(c.categoryId)}
                                            className={({ active, selected }) =>
                                            `cursor-pointer select-none px-4 py-2 text-sm ${
                                                active ? 'bg-blue-100 text-blue-900' : 'text-gray-900'
                                            } ${selected ? 'font-semibold bg-blue-50' : ''}`
                                            }
                                        >
                                            {({ selected }) => (
                                            <div className="flex items-center gap-2">
                                                <input
                                                type="checkbox"
                                                checked={selected}
                                                readOnly
                                                className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                                                required/>
                                                <span>{c.categoryName}</span>
                                    </div>
                                            
                                            )}
                                        
                                        

                                        
                                            
                                    </ListboxOption>
                                    )})}
                               
                             
                                        
                                </ListboxOptions>
                                        
                            </Listbox>
                        </div>
                    </div>
                    <div className="col-span-2">
                                <label className="block mb-2 text-sm font-medium text-gray-900">Select Variations</label>

                                <div className="relative">
                                    <Listbox
                                      value={selectedVariations}
                                      onChange={(ids) => setSelectedVariations(ids)}
                                      multiple
                                    >
                                    
                                    <ListboxButton className="w-full bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg p-2.5 text-left focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                                        {selectedVariations.length > 0
                                        ? selectedVariations.map(id => variations.find(v => v.variationId === id)?.variationName || 'Unknown').join(', ')
                                    : 'Select items'}
                                    </ListboxButton>

                                   
                                    <ListboxOptions className="absolute z-50 mt-1 w-full bg-white border border-gray-300 rounded-lg shadow-lg max-h-60 overflow-auto focus:outline-none">
                            
            
                                        {variations.map((v) => {
                                        const selectedItem = selectedVariations.find(i => i.variationId === v.variationId);
                                            return(
                                        <ListboxOption
                                            key={v.variationId}
                                            value={Number(v.variationId)}
                                            className={({ active, selected }) =>
                                            `cursor-pointer select-none px-4 py-2 text-sm ${
                                                active ? 'bg-blue-100 text-blue-900' : 'text-gray-900'
                                            } ${selected ? 'font-semibold bg-blue-50' : ''}`
                                            }
                                        >
                                            {({ selected }) => (
                                            <div className="flex items-center gap-2">
                                                <input
                                                type="checkbox"
                                                checked={selected}
                                                readOnly
                                                className="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                                                required/>
                                                <span>{v.variationName}</span>
                                                <span>
                                              </span>
                                          
                                    </div>
                                            
                                            )}
                                        
                                        

                                        
                                            
                                    </ListboxOption>
                                    )})}
                               
                             
                                        
                                </ListboxOptions>
                                        
                            </Listbox>
                        </div>
                    </div>                          
                            <div className="col-span-2 ">
                            <label className="block text-sm font-medium text-gray-700 mb-1" htmlFor="file_input">{isEditing ? "Upload a new file" : "Upload file"}</label>
                            <input onChange={handleImageChange} className="block w-full text-sm text-gray-700
               file:mr-4 file:py-2 file:px-4
               file:rounded-lg file:border-0
               file:text-sm file:font-semibold
               file:bg-gray-100 file:text-gray-700
               hover:file:bg-gray-200
               border border-gray-300 rounded-lg cursor-pointer bg-gray-50" name="productImage" aria-describedby="file_input_help" id="file_input" type="file"/>
                            <p className="mt-1 text-sm text-gray-500 dark:text-gray-300" id="file_input_help">SVG, PNG, JPG or GIF (MAX. 800x400px).</p>
                        </div>
                        <div className="col-span-2 flex justify-center">
                        {isEditing ? <img
                        src={`${API_BASE_URL}/images/${productImage}`}
                        alt=""
                        className="w-16 md:w-32 max-w-full max-h-full "
                        /> : null}
                        
                        </div>
                        <div className="col-span-2 flex justify-center">
                        <button type="submit" className="text-white inline-flex items-center bg-gray-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mt-2 text-center">
                            <svg className="me-1 -ms-1 w- h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd"></path></svg>
                            {isEditing ? "Update" : "Add"}
                        </button>
                        </div>
                        </div>
                    </form>
                </div>
            </div>
        </div> 

        )}


        {showModal2 && (<DeleteConfirm onClose={closeModal2} onDeleteClick={() => deleteProduct(deleteId)} element="product" />)}

        {showActionPopup && (
            <AddSuccessPopup message={popupMessage} />
        )}

        {deleteSuccess && (
            <DeleteSuccessPopup element="Product" setDeleteSuccess={setDeleteSuccess} />
        )}
        </div>




      </div>

      
    </div>
    
        )
      }


export default Products
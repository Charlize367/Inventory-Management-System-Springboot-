import React from 'react'
import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react'
import { useAuth } from '../auth/AuthContext.jsx'
import axios from 'axios';

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const API_URL = import.meta.env.VITE_API_URL;
  const navigate = useNavigate();
  const { login } = useAuth();
  const [showLogoutPopup, setShowLogoutPopup] = useState(false);

  

  useEffect(() => {
   
    if (localStorage.getItem("showLogoutPopup") === "true") {
      setShowLogoutPopup(true);

      
      const timer = setTimeout(() => {
      setShowLogoutPopup(false);
      localStorage.setItem("showLogoutPopup", "false"); 
    }, 3000);

    return () => clearTimeout(timer);

      
    }
  }, []);

  const handleEmailChange = (event) => {
    setEmail(event.target.value);
  };

  const handlePasswordChange = (event) => {
    setPassword(event.target.value);
  };

   const handleSubmit = async (event) => {
        event.preventDefault();
    
            try {
                const postData = {
                    "email" : email,
                    "password" : password,
                }
                const response = await axios.post(`${API_URL}/auth/login`, postData, {
                    headers: {
                        
                        'Content-Type': 'application/json'
                    }
                });
                
                login({username: response.data.username, email: response.data.email, role: response.data.role, id: response.data.id}, response.data.token)
                console.log(response);
                localStorage.setItem("showLoginPopup", "true");
                navigate('/dashboard');
                
                return true;

            } catch (error) {
                window.alert("Login failed");
                console.error('Login failed:', error);
                return false;
            }

        }


  return (
    <section className="bg-white-50 dark:bg-gray-200">
  <div className="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0">
      <div className="w-full bg-white rounded-lg shadow gray:border md:mt-0 sm:max-w-md xl:p-0 light:bg-white-800 dark:border-gray-700">
          <div className="p-6 space-y-4 md:space-y-6 sm:p-8">
              <h1 className="text-xl font-bold leading-tight tracking-tight text-gray-900 md:text-2xl dark:text-black ">
                  Sign in to your Inventory Management System
              </h1>
              <form onSubmit={handleSubmit} className="space-y-4 md:space-y-6">
                  <div>
                      <label htmlFor="email" className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Your email</label>
                      <input type="email" value={email} onChange={handleEmailChange} name="email" id="email" className="bg-gray-50 border border-gray-900 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" placeholder="name@company.com" required/>
                  </div>
                  <div>
                      <label htmlFor="password" className="block mb-2 text-sm font-medium text-gray-900 dark:text-white">Password</label>
                      <input type="password" value={password} onChange={handlePasswordChange} name="password" id="password" placeholder="••••••••" className="bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5" required/>
                  </div>
                
                  <button type="submit" name="login" className="w-full mt-8 text-white bg-primary-600 hover:bg-primary-700 focus:ring-4 focus:outline-none focus:ring-primary-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-primary-600 dark:hover:bg-primary-700 dark:focus:ring-primary-800 dark:bg-gray-600">Sign in</button>
              </form>
          </div>
      </div>
  </div>

  {showLogoutPopup && (
    <div id="toast-top-right" class="fixed flex items-center w-full max-w-xs p-4 space-x-4 text-gray-500 bg-white divide-x rtl:divide-x-reverse divide-gray-200 rounded-lg shadow-sm top-5 right-5" role="alert">
    <div class="text-m font-normal">User successfully logged out.</div>
</div>
  )}

  
</section>
  )
}

export default Login
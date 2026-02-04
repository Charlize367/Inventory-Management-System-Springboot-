import { createContext, useContext, useState  } from "react";
import React from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(JSON.parse(localStorage.getItem("user")) || null);
    const [token, setToken] = useState(localStorage.getItem("jwtToken") || null);

    const login = (userData, token) => {
        const roleArray = typeof userData.role === "string" ? [userData.role] : userData.role;
        const userWithRole = {...userData, role: roleArray};
        const id = userData.id;
        const email = userData.email;
        const username = userData.username;
        const role = userData.role;
        setUser(userWithRole);
        setToken(token);
        localStorage.setItem("user", JSON.stringify(userWithRole));
        localStorage.setItem("jwtToken", token);
        localStorage.setItem("userId", id);
        localStorage.setItem("email", email);
        localStorage.setItem("username", username)
        localStorage.setItem("role", role);
        
    }



    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem("user");
        localStorage.removeItem("token");
        window.location.href = "/login";
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout}}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);


import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Staff from './pages/Admin/Staff'
import Manager from './pages/Admin/Manager'
import Customers from './pages/Admin/Customers'
import Suppliers from './pages/Admin/Suppliers'
import Products from './pages/Admin/Products'
import Purchases from './pages/Admin/Purchases'
import Sales from './pages/Admin/Sales'
import StockMovements from './pages/Admin/StockMovements'
import { AuthProvider } from './auth/AuthContext'
import ProtectedRoute from './auth/ProtectedRoute'
import Categories from './pages/Admin/Categories'
import Brands from './pages/Admin/Brands'
import Sku from './pages/Admin/Sku'
import Variations from './pages/Admin/Variations'

function App() {
  const [count, setCount] = useState(0)

  return (
     <AuthProvider>
     <BrowserRouter>
    
            <Routes>
                <Route exact path="/login" element={<Login/>} />
                <Route exact path="/dashboard" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Dashboard /></ProtectedRoute>} />
                <Route exact path="/staffs" element={<ProtectedRoute allowedRoles={["ADMIN", "MANAGER"]}><Staff /></ProtectedRoute>} />
                <Route exact path="/managers" element={<ProtectedRoute allowedRoles={["ADMIN", "MANAGER"]}><Manager /></ProtectedRoute>} />
                <Route exact path="/customers" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Customers /></ProtectedRoute>} />
                <Route exact path="/suppliers" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Suppliers /></ProtectedRoute>} />
                <Route exact path="/products" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Products /></ProtectedRoute>} />
                <Route exact path="/purchases" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Purchases /></ProtectedRoute>} />
                <Route exact path="/sales" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Sales /></ProtectedRoute>} />
                <Route exact path="/stock_movements" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><StockMovements /></ProtectedRoute>} />
                <Route exact path="/categories" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Categories /></ProtectedRoute>} />
                <Route exact path="/brands" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Brands /></ProtectedRoute>} />
                <Route exact path="/sku" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Sku /></ProtectedRoute>} />
                <Route exact path="/variations" element={<ProtectedRoute allowedRoles={["ADMIN", "STAFF", "MANAGER"]}><Variations /></ProtectedRoute>} />
            </Routes>
      </BrowserRouter>
      </AuthProvider>
  )
}

export default App

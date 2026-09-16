import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LoginPage from '@/pages/AuthPage/LoginPage'
import RegisterPage from '@/pages/AuthPage/RegisterPage'
import HomePage from '@/pages/HomePage'
import CreateOccurrencePage from '@/pages/OccurrencesPage/CreateOccurrencePage'
import CategoriesListPage from '@/pages/SettingsPage/CategoriesListPage'
import CreateCategoryPage from '@/pages/SettingsPage/CreateCategoryPage'
import CreateSubcategoryPage from '@/pages/SettingsPage/CreateSubcategoryPage'
import NotFoundPage from '@/pages/NotFoundPage'
import ProtectedRoute from '@/components/common/ProtectedRoute'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <HomePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/ocorrencias/nova"
          element={
            <ProtectedRoute>
              <CreateOccurrencePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/configuracoes"
          element={
            <ProtectedRoute requireAdmin>
              <CategoriesListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/configuracoes/categorias/nova"
          element={
            <ProtectedRoute requireAdmin>
              <CreateCategoryPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/configuracoes/subcategorias/nova"
          element={
            <ProtectedRoute requireAdmin>
              <CreateSubcategoryPage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App

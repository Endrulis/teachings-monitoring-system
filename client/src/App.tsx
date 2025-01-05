import './App.css';
import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import UserPage from './pages/UserPage';
import PrivateRoute from './PrivateRoute';
import { useEffect, useState } from 'react';
import axios from 'axios';

const App: React.FC = () => {

  const [roles, setRoles] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  
  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/roles');
        setRoles(response.data.map((role: { name: string }) => role.name));
      } catch (error) {
        console.error('Error fetching roles:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchRoles();
  }, []);

  if (loading) {
    return <div>Loading roles...</div>;
  }

  return (
    <Routes>
      {/* Route for the Home page */}
      <Route path="/" element={<HomePage />} />

      {/* Routes for Login and Register pages */}
      <Route path="/login" element={<LoginForm />} />
      <Route path="/register" element={<RegisterForm />} />

      <Route
        path="/user"
        element={
          <PrivateRoute allowedRoles={roles}>
            <UserPage />
          </PrivateRoute>
        }
      />
    </Routes>
  );
};

export default App;

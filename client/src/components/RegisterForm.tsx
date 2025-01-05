// src/RegisterForm.tsx
import React, { useState } from 'react';
import axios from 'axios';

import { useNavigate } from 'react-router-dom';

const RegisterForm: React.FC = () => {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const validateForm = () => {
    if (!username || !email || !password) {
      setError('All fields are required');
      return false;
    }
    if (password.length < 8) {
      setError('Password must be at least 8 characters');
      return false;
    }
    return true;
  };

  const handleSignUp = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!validateForm()) return; // Ensure form validation before sending request

    setIsSubmitting(true);
    setError(''); // Clear previous errors

    try {
      const response = await axios.post(
        'http://localhost:8080/api/v1/auth/signup',
        { username, password, email },
        { headers: { 'Content-Type': 'application/json' } }
      );

      console.log('Sign-up successful', response.data);

      navigate('/login');
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        if (err.response) {
          setError(`Sign-up failed: ${err.response.statusText}`);
        } else {
          setError('An error occurred while signing up.');
        }
      } else {
        setError('An unknown error occurred.');
      }
      console.error('Sign-up error:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="bg-gray-800 shadow-lg rounded-lg p-8 w-full max-w-md mx-auto">
      <h2 className="text-3xl text-center text-gray-100 mb-6">Sign Up</h2>
      <form onSubmit={handleSignUp} className="space-y-4">
        <div>
          <label htmlFor="username" className="text-gray-300">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="w-full px-4 py-2 bg-gray-700 text-gray-100 rounded-md"
            required
          />
        </div>
        <div>
          <label htmlFor="email" className="text-gray-300">Email:</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full px-4 py-2 bg-gray-700 text-gray-100 rounded-md"
            required
          />
        </div>
        <div>
          <label htmlFor="password" className="text-gray-300">Password:</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full px-4 py-2 bg-gray-700 text-gray-100 rounded-md"
            required
          />
        </div>
        <button
          type="submit"
          className="w-full bg-indigo-600 hover:bg-indigo-700 text-white py-2 rounded-md"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Signing up...' : 'Sign Up'}
        </button>
        {error && <div className="text-red-500 text-center mt-4">{error}</div>}
      </form>
    </div>
  );
};

export default RegisterForm;

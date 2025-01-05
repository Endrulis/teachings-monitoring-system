// src/LoginForm.tsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const LoginForm: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const navigate = useNavigate();

    const handleLogin = async (event: React.FormEvent) => {
        event.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/v1/auth/authenticate', {
                email,
                password,
            });
            
            console.log('Login successful:', response.data);  // Log the entire response
        
            const token = response.data.accessToken;  // Corrected token key
            console.log('Received token:', token);  // Log the token value
        
            if (token) {
                localStorage.setItem('token', token);  // Store JWT in local storage
                setError('');
                navigate('/user');
            } else {
                setError('Token is undefined, please check the server response.');
            }
        
        } catch (err) {
            setError('Login failed, please check your credentials.');
            console.error('Login error:', err);
        }
    };
    

    return (
        <div className="bg-gray-800 shadow-lg rounded-lg p-8 w-full max-w-md mx-auto">
            <h2 className="text-3xl text-center text-gray-100 mb-6">Login</h2>
            <form onSubmit={handleLogin} className="space-y-4">
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
                <button type="submit" className="w-full bg-indigo-600 hover:bg-indigo-700 text-white py-2 rounded-md">
                    Login
                </button>
                {error && <div className="text-red-500 text-center mt-4">{error}</div>}
            </form>
        </div>
    );
};

export default LoginForm;

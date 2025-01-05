// src/hooks/useToken.ts
import { useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

const useToken = () => {
  const [decodedToken, setDecodedToken] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    if (storedToken) {
      try {
        const decoded = jwtDecode(storedToken);
        setDecodedToken(decoded);
        setError(null);
      } catch (err) {
        console.error('Error decoding token:', err);
        setError('Invalid token');
      }
    } else {
      setError('No token found in localStorage');
    }
  }, []);

  return { decodedToken, error };
};

export default useToken;

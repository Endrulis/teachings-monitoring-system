import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

interface PrivateRouteProps {
  allowedRoles: string[];
  children: JSX.Element;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ allowedRoles, children }) => {
  const navigate = useNavigate();
  const token = localStorage.getItem('token')?.trim(); // Remove any extra spaces
  const [isAuthorized, setIsAuthorized] = useState<boolean | null>(null); // Manage authorization state

  useEffect(() => {
    // Check if the token exists and has a valid structure (3 parts)
    if (!token || token.split('.').length !== 3) {
      console.error('Invalid token structure:', token);
      navigate('/login'); // Redirect to login if the token is invalid
      setIsAuthorized(false); // Set unauthorized state
      return;
    }

    let decodedToken: any;
    try {
      // Decode the token, assuming it's a valid JWT
      decodedToken = jwtDecode(token);
    } catch (error) {
      console.error('Error decoding token:', error);
      navigate('/login'); // Redirect to login on invalid token
      setIsAuthorized(false); // Set unauthorized state
      return;
    }

    const userRoles = decodedToken?.roles;

    if (!userRoles) {
      navigate('/login'); // If there are no roles in the token
      setIsAuthorized(false); // Set unauthorized state
      return;
    }

    const hasAccess = allowedRoles.some((role: string) => userRoles.includes(role));

    if (!hasAccess) {
      navigate('/unauthorized'); // Redirect if role is not allowed
      setIsAuthorized(false); // Set unauthorized state
      return;
    }

    setIsAuthorized(true); // Set authorized state if role is valid
  }, [token, navigate, allowedRoles]);

  // Wait until the authorization check is complete before rendering
  if (isAuthorized === null) {
    return <div>Loading...</div>; // Or you could display a spinner or some loading UI
  }

  // If authorized, render children (protected content)
  if (isAuthorized) {
    return children;
  }

  // Render nothing if unauthorized (redirecting already happened)
  return null;
};

export default PrivateRoute;

// src/hooks/useRoles.ts
import { useState } from 'react';
import axios from 'axios';

const useRoles = () => {
  const [roles, setRoles] = useState<any[]>([]);
  const [loadingRoles, setLoadingRoles] = useState(false);
  const [rolesError, setRolesError] = useState<string | null>(null);
  const [isCreatingRole, setIsCreatingRole] = useState(false);
  const [newRoleName, setNewRoleName] = useState('');

  const fetchRoles = async () => {
    setLoadingRoles(true);
    setRolesError(null);
    try {
      const response = await axios.get("http://localhost:8080/api/v1/roles", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      setRoles(response.data);
    } catch (err) {
      console.error("Error fetching roles: ", err);
      setRolesError("Failed to fetch roles.");
    } finally {
      setLoadingRoles(false);
    }
  };

  const createRole = async (roleName: string) => {
    setIsCreatingRole(true);
    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/roles",
        { name: roleName },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      setRoles([...roles, response.data]);
      setNewRoleName('');
    } catch (err) {
      console.error("Error creating role:", err);
      setRolesError("Failed to create role.");
    } finally {
      setIsCreatingRole(false);
    }
  };

  return { roles, loadingRoles, rolesError, fetchRoles, createRole, newRoleName, setNewRoleName, isCreatingRole };
};

export default useRoles;

// src/hooks/useUsers.ts
import { useState } from 'react';
import axios from 'axios';

const useUsers = () => {
  const [users, setUsers] = useState<any[]>([]);
  const [loadingUsers, setLoadingUsers] = useState(false);
  const [usersError, setUsersError] = useState<string | null>(null);

  const fetchUsers = async () => {
    setLoadingUsers(true);
    setUsersError(null);
    try {
      const response = await axios.get("http://localhost:8080/api/v1/users", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      setUsers(response.data);
    } catch (err) {
      console.error("Error fetching users: ", err);
      setUsersError("Failed to fetch users.");
    } finally {
      setLoadingUsers(false);
    }
  };

  return { users, loadingUsers, usersError, fetchUsers };
};

export default useUsers;

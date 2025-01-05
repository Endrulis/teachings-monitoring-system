// src/hooks/useClasses.ts
import { useState } from 'react';
import axios from 'axios';
import { ClassDto } from '../dto/ClassDto';

const useClasses = () => {
  const [selectedUnitClasses, setSelectedUnitClasses] = useState<ClassDto[] | null>(null);
  const [isClassesPopupOpen, setIsClassesPopupOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchClassesByCurricularUnitId = async (unitId: number) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/v1/classes/unit/${unitId}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      setSelectedUnitClasses(response.data);
      setIsClassesPopupOpen(true); // Open the popup after fetching the data
    } catch (error) {
      console.error("Error fetching classes:", error);
      setError('Error fetching classes');
    }
  };

  return {
    selectedUnitClasses,
    isClassesPopupOpen,
    fetchClassesByCurricularUnitId,
    setIsClassesPopupOpen,
    error,
  };
};

export default useClasses;

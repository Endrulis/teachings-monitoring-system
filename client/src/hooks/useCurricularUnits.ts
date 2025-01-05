// src/hooks/useCurricularUnits.ts
import { useState, useEffect } from 'react';
import axios from 'axios';
import { CurricularUnitDto } from '../dto/CurricularUnitDto';

const useCurricularUnits = (userEmail: string | null) => {
  const [curricularUnits, setCurricularUnits] = useState<CurricularUnitDto[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (userEmail) {
      setLoading(true);
      axios
        .get(`http://localhost:8080/api/v1/curricular-units/user/email/${userEmail}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        })
        .then((response) => {
          setCurricularUnits(response.data);
          setLoading(false);
        })
        .catch(() => {
          setError('Error fetching curricular units.');
          setLoading(false);
        });
    }
  }, [userEmail]);

  return { curricularUnits, loading, error };
};

export default useCurricularUnits;

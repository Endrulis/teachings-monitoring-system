// components/CurricularUnitsPopup.tsx
import React, { useEffect, useState } from 'react';
import { CurricularUnitDto } from '../../dto/CurricularUnitDto';
import { UserDto } from '../../dto/UserDto';
import { useToken } from '../../hooks';

interface CurricularUnitsPopupProps {
  curricularUnits: CurricularUnitDto[];
  isOpen: boolean;
  onClose: () => void;
  onUnitClick: (unit: CurricularUnitDto) => void;
}

interface AttendanceDto {
  id: number;
  attended: boolean;
  studentId: number;
  classSessionId: number;
}

const CurricularUnitsPopup: React.FC<CurricularUnitsPopupProps> = ({
  curricularUnits,
  isOpen,
  onClose,
  onUnitClick
}) => {

  const { decodedToken } = useToken();
  const isTeacher = decodedToken?.roles?.includes("TEACHER");

  const [selectedUnit, setSelectedUnit] = useState<CurricularUnitDto | null>(null);
  const [students, setStudents] = useState<UserDto[]>([]);
  const [filteredStudents, setFilteredStudents] = useState<UserDto[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [attendanceData, setAttendanceData] = useState<
    Record<number, { attended: number; total: number }>
  >({});
  const [isLoading, setIsLoading] = useState(false);

  const fetchStudents = async (unitId: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `http://localhost:8080/api/v1/users/curricular-unit/${unitId}`,
        {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        }
      );
      if (!response.ok) {
        throw new Error(`Failed to fetch students: ${response.statusText}`);
      }
      const data: UserDto[] = await response.json();
      setStudents(data);
      setFilteredStudents(data);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchAttendance = async (userId: number, unitId: number) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/attendance/user/${userId}/curricularUnit/${unitId}`,
        {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        }
      );
      if (!response.ok) {
        throw new Error(`Failed to fetch attendance: ${response.statusText}`);
      }
      const attendance: AttendanceDto[] = await response.json();
      const attended = attendance.filter((a) => a.attended).length;
      const total = attendance.length;
      setAttendanceData((prev) => ({
        ...prev,
        [userId]: { attended, total },
      }));
    } catch (error) {
      console.error(error);
    }
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const term = e.target.value.toLowerCase();
    setSearchTerm(term);
    setFilteredStudents(
      students.filter(
        (student) =>
          student.username.toLowerCase().includes(term) ||
          student.email.toLowerCase().includes(term)
      )
    );
  };

  const handleStudentsClick = (unit: CurricularUnitDto) => {
    setAttendanceData({});
    setSelectedUnit(unit);
    fetchStudents(unit.id);
  };

  const handleClassesClick = (unit: CurricularUnitDto) => {
    onUnitClick(unit);
  };

  useEffect(() => {
    if (selectedUnit) {
      students.forEach((student) => {
        if (!attendanceData[student.id]) {
          fetchAttendance(student.id, selectedUnit.id);
        }
      });
    }
  }, [students, selectedUnit]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-70">
      {/* Main Popup */}
      <div className="bg-gray-900 p-8 rounded-lg shadow-xl w-11/12 max-w-2xl relative">
        <h2 className="text-3xl font-semibold text-white mb-6">
          Your Curricular Units
        </h2>
        <ul className="space-y-4">
          {curricularUnits.map((unit) => (
            <li key={unit.id} className="p-4 bg-gray-800 rounded-lg shadow-md">
              <div className="flex justify-between items-center">
                <div>
                  <strong className="text-xl text-white">{unit.name}</strong>
                  <p className="text-sm text-gray-400">
                    Teacher: {unit.teacher.email}
                  </p>
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() => handleClassesClick(unit)}
                    className="bg-green-600 text-white px-4 py-2 rounded-full hover:bg-green-700 transition duration-300"
                  >
                    Classes
                  </button>
                  {isTeacher && (
                    <button
                      onClick={() => handleStudentsClick(unit)}
                      className="bg-blue-600 text-white px-4 py-2 rounded-full hover:bg-blue-700 transition duration-300"
                    >
                      Students
                    </button>
                  )}
                </div>
              </div>
            </li>
          ))}
        </ul>
        <button
          onClick={onClose}
          className="mt-6 bg-red-600 text-white py-2 px-6 rounded-full hover:bg-red-700 transition duration-300"
        >
          Close
        </button>

        {/* Students Overlay */}
        {selectedUnit && (
          <div className="absolute inset-0 bg-gray-900 bg-opacity-90 flex flex-col p-8 rounded-lg shadow-xl overflow-y-auto">
            <h2 className="text-3xl font-semibold text-white mb-6">
              Students in {selectedUnit.name}
            </h2>
            <input
              type="text"
              value={searchTerm}
              onChange={handleSearchChange}
              placeholder="Search by email or username"
              className="mb-6 px-4 py-2 w-full rounded-lg bg-gray-800 text-white border border-gray-700 focus:ring focus:ring-blue-500"
            />
            {isLoading ? (
              <p className="text-white text-center">Loading students...</p>
            ) : (
              <ul className="space-y-4">
                {filteredStudents.map((student) => {
                  const attendance = attendanceData[student.id] || {
                    attended: 0,
                    total: 0,
                  };
                  const attendancePercentage =
                    attendance.total > 0
                      ? ((attendance.attended / attendance.total) * 100).toFixed(2)
                      : "N/A";
                  return (
                    <li
                      key={student.id}
                      className="p-4 bg-gray-800 rounded-lg shadow-md"
                    >
                      <strong className="text-xl text-white">{student.username}</strong>
                      <p className="text-sm text-gray-400">
                        Email: {student.email}
                      </p>
                      <p className="text-sm text-gray-400">
                        Attendance: {attendance.attended}/{attendance.total} (
                        {attendancePercentage}%)
                      </p>
                    </li>
                  );
                })}
              </ul>
            )}
            <button
              onClick={() => setSelectedUnit(null)}
              className="mt-6 bg-yellow-600 text-white py-2 px-6 rounded-full hover:bg-yellow-700 transition duration-300 self-end"
            >
              Close Students
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default CurricularUnitsPopup;

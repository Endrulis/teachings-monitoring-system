// components/ClassesPopup.tsx
import React, { useEffect, useState } from "react";
import { ClassDto } from "../../dto/ClassDto";
import CurriculumStudentListPopup from "./CurriculumStudentListPopup";
import { useToken } from "../../hooks";
import axios from "axios";
import { Category } from "../../dto/Category";

interface ClassesPopupProps {
  classes: ClassDto[];
  isOpen: boolean;
  onClose: () => void;
  unitName: string;
  curricularUnitId: number | null;
}

const ClassesPopup: React.FC<ClassesPopupProps> = ({
  classes,
  isOpen,
  onClose,
  unitName,
  curricularUnitId,
}) => {
  const { decodedToken } = useToken();
  const isTeacher = decodedToken?.roles?.includes("TEACHER");
  const isStudent = decodedToken?.roles?.includes("STUDENT");
  const userEmail = decodedToken?.sub;

  const [attendanceStatuses, setAttendanceStatuses] = useState<
    Map<number, string>
  >(new Map());

  const [selectedClassSession, setSelectedClassSession] =
    useState<ClassDto | null>(null);

  const [showScores, setShowScores] = useState(false);
  const [scores, setScores] = useState<{ [categoryId: number]: number }>({});

  const [categories, setCategories] = useState<Category[]>([]);

  // Sort classes by date (newest to oldest)
  const sortedClasses = [...classes].sort(
    (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
  );

  useEffect(() => {
    if (isStudent && userEmail && classes.length > 0) {
      const fetchCategories = async () => {
        try {
          const response = await fetch("http://localhost:8080/api/v1/categories", {
            method: "GET",
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`, // Add Authorization header
                "Content-Type": "application/json", // Add Content-Type header for safety
            },
        });
          const data = await response.json();
          setCategories(data); // Store fetched categories in state
        } catch (error) {
          console.error("Error fetching categories:", error);
          alert("Failed to fetch categories.");
        }
      };

      const fetchAttendanceStatuses = async () => {
        try {
          const statuses: Map<number, string> = new Map();
          for (const classSession of classes) {
            const response = await axios.post(
              `http://localhost:8080/api/v1/attendance/status`,
              {
                studentEmail: userEmail,
                classSessionId: classSession.id,
              },
              {
                headers: {
                  Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
              }
            );

            if (response.status === 200) {
              statuses.set(
                classSession.id,
                response.data.attended === null
                  ? "Not Recorded Yet"
                  : response.data.attended
                  ? "Attended"
                  : "Not Attended"
              );
            } else {
              statuses.set(classSession.id, "Not Recorded Yet");
            }
          }
          setAttendanceStatuses(statuses);
        } catch (error) {
          console.error("Error fetching attendance status", error);
        }
      };

      fetchAttendanceStatuses();
      fetchCategories();
    }
  }, [isStudent, userEmail, classes]);

  const fetchScores = async (classSessionId: number) => {
    try {
      if (!userEmail) {
        console.error("User email is not available");
        return;
      }

      // Step 1: Fetch the attendanceId for the given student and class session
      const attendanceResponse = await axios.get(
        `http://localhost:8080/api/v1/attendance/user/${userEmail}/class/${classSessionId}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (attendanceResponse.status !== 200) {
        console.error("Failed to fetch attendance");
        return;
      }

      const attendanceId = attendanceResponse.data.id;
      console.log("Attendance ID:", attendanceId);

      // Step 2: Fetch scores using the attendanceId
      const scoresResponse = await fetch(
        `http://localhost:8080/api/v1/participation-scores/attendance/${attendanceId}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (scoresResponse.status === 204) {
        setScores({});
        setShowScores(true);
        return;
      }

      if (scoresResponse.ok) {
        const scoresData: {
          score: number;
          participationCategory: { id: number };
        }[] = await scoresResponse.json();
        console.log("Scores data:", scoresData);

        const scoresMap: { [categoryId: number]: number } = {};

        // Populate the scoresMap manually
        for (const score of scoresData) {
          const categoryId = score.participationCategory.id;
          const scoreValue = score.score;
          scoresMap[categoryId] = scoreValue;
        }

        setScores(scoresMap);
        setShowScores(true);
      } else {
        console.error("Failed to fetch scores");
      }
    } catch (error) {
      console.error("Error fetching scores:", error);
    }
  };

  const handleClassSessionClick = (classSession: ClassDto) => {
    setSelectedClassSession(classSession);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-70">
      <div className="bg-gray-900 p-8 rounded-lg shadow-xl w-11/12 max-w-2xl">
        <h2 className="text-3xl font-semibold text-white mb-6">
          Classes for {unitName}
        </h2>
        <ul className="space-y-4">
          {sortedClasses.map((classSession) => (
            <li
              key={classSession.id}
              className="p-4 bg-gray-800 rounded-lg shadow-md hover:bg-gray-700 transition duration-300"
            >
              <div className="flex justify-between items-center">
                <div>
                  <strong className="text-xl text-white">
                    {classSession.className}
                  </strong>
                  <p className="text-sm text-gray-400">
                    Date: {new Date(classSession.date).toLocaleString()}
                  </p>
                </div>
                {isStudent && (
                  <div className="text-white text-lg">
                    {attendanceStatuses.get(classSession.id) ||
                      "Not Recorded Yet"}
                  </div>
                )}
                {isTeacher && (
                  <button
                    onClick={() => handleClassSessionClick(classSession)}
                    className="bg-blue-600 text-white px-4 py-2 rounded-full hover:bg-blue-700 transition duration-300"
                  >
                    Manage Attendances
                  </button>
                )}
                {isStudent &&
                  attendanceStatuses.get(classSession.id) === "Attended" && (
                    <button
                      onClick={() => fetchScores(classSession.id)}
                      className="bg-green-600 text-white px-4 py-2 rounded-full hover:bg-green-700 transition duration-300"
                    >
                      Show Scores
                    </button>
                  )}
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
      </div>
      {selectedClassSession && (
        <CurriculumStudentListPopup
          curricularUnitId={curricularUnitId}
          selectedClassSessionId={selectedClassSession.id}
          isOpen={!!selectedClassSession}
          onClose={() => setSelectedClassSession(null)}
        />
      )}

      {/* Scores Popup */}
      {showScores && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-70">
          <div className="bg-gray-900 p-8 rounded-lg shadow-xl w-11/12 max-w-2xl">
            <h2 className="text-3xl font-semibold text-white mb-6">
              Scores for {selectedClassSession?.className}
            </h2>
            <ul className="space-y-4">
              {categories.map((category) => {
                const score = scores[category.id] || 0; // Default score to 0 if not available
                return (
                  <li
                    key={category.id}
                    className="p-4 bg-gray-800 rounded-lg shadow-md"
                  >
                    <strong className="text-xl text-white">
                      {category.name}
                    </strong>
                    <div className="flex items-center">
                      {[1, 2, 3, 4, 5].map((value) => (
                        <svg
                          key={value}
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 24 24"
                          fill={score >= value ? "yellow" : "gray"}
                          className="w-8 h-8"
                        >
                          <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
                        </svg>
                      ))}
                    </div>
                  </li>
                );
              })}
            </ul>
            <button
              onClick={() => setShowScores(false)}
              className="mt-6 bg-red-600 text-white py-2 px-6 rounded-full hover:bg-red-700 transition duration-300"
            >
              Close Scores
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ClassesPopup;

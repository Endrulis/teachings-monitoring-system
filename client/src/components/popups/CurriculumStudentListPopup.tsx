import React, { useEffect, useReducer, useState } from 'react';
import { UserDto } from '../../dto/UserDto.tsx'; // Assuming you have a DTO for user

interface StudentListPopupProps {
    curricularUnitId: number | null;
    selectedClassSessionId: number | null;
    isOpen: boolean;
    onClose: () => void;
}

interface Category {
    id: number;
    name: string;
}

type AttendanceState = {
    students: UserDto[];
    attendance: { [key: number]: { isPresent: boolean; attendanceId: number | null } };
    loading: boolean;
    error: string | null;
};

const initialState: AttendanceState = {
    students: [],
    attendance: {},
    loading: false,
    error: null,
};

const attendanceReducer = (state: AttendanceState, action: any): AttendanceState => {
    switch (action.type) {
        case 'FETCH_STUDENTS_START':
            return { ...state, loading: true, error: null };

        case 'FETCH_STUDENTS_SUCCESS':
            const attendance = action.attendance || {}; // Ensure action contains attendance data
            console.log('attendance from action:', attendance); // Debug log to check the data being passed

            // Initialize the attendance state with the data passed in the action
            const initialAttendance = action.payload.reduce((acc: { [key: number]: { isPresent: boolean; attendanceId: number | null } }, student: UserDto) => {
                // Fetch the attendance state for the student, ensuring it's either the provided attendance or a default
                const studentAttendance = attendance[student.id] || { isPresent: false, attendanceId: null };

                console.log('initialAttendance for studentId:', student.id, studentAttendance);  // Debug log to check final attendance

                // Ensure correct assignment of attendanceId
                acc[student.id] = studentAttendance;
                return acc;
            }, {});

            return { ...state, students: action.payload, attendance: initialAttendance, loading: false };

        case 'FETCH_STUDENTS_FAILURE':
            return { ...state, loading: false, error: action.error };

        case 'TOGGLE_ATTENDANCE':
            return {
                ...state,
                attendance: {
                    ...state.attendance,
                    [action.studentId]: {
                        isPresent: !state.attendance[action.studentId]?.isPresent,
                        attendanceId: state.attendance[action.studentId]?.attendanceId || null  // Ensure attendanceId is retained
                    },
                },
            };

        default:
            return state;
    }
};



const CurriculumStudentListPopup: React.FC<StudentListPopupProps> = ({ curricularUnitId, selectedClassSessionId, isOpen, onClose }) => {
    const [state, dispatch] = useReducer(attendanceReducer, initialState);

    const [showReportForm, setShowReportForm] = useState(false); // Manage form visibility
    const [currentStudentId, setCurrentStudentId] = useState<number | null>(null); // Track student id
    const [categories, setCategories] = useState<Category[]>([]);
    const [scores, setScores] = useState<{ [key: number]: number }>({}); // Store the scores for each category


    useEffect(() => {
        const fetchStudents = async () => {
            if (!curricularUnitId || !selectedClassSessionId) return;

            dispatch({ type: 'FETCH_STUDENTS_START' });

            try {
                const studentsResponse = await fetch(`http://localhost:8080/api/v1/users/curricular-unit/${curricularUnitId}`, {
                    headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` },
                });

                if (!studentsResponse.ok) throw new Error('Failed to fetch students.');

                const studentsData = await studentsResponse.json();

                const attendanceResponse = await fetch(`http://localhost:8080/api/v1/attendance/class/${selectedClassSessionId}`, {
                    headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` },
                });

                let attendanceData = [];
                if (attendanceResponse.ok) {
                    attendanceData = await attendanceResponse.json();
                    console.log(attendanceData);
                }

                // Create an attendance state, defaulting to false if no attendance data is available
                const attendance = studentsData.reduce((acc: { [key: number]: { isPresent: boolean; attendanceId: number | null } }, student: UserDto) => {
                    const studentAttendance = attendanceData.find((attendance: any) => attendance.studentId === student.id);
                    console.log('student:', student);  // Log student data
                    console.log('attendanceData:', studentAttendance);  // Log attendanceData for the student

                    acc[student.id] = studentAttendance
                        ? { isPresent: studentAttendance.attended, attendanceId: studentAttendance.id ?? null }
                        : { isPresent: false, attendanceId: null }; // Default to false and null if no data
                    return acc;
                }, {});

                console.log('attendance state before dispatch:', attendance);

                dispatch({ type: 'FETCH_STUDENTS_SUCCESS', payload: studentsData, attendance });
            } catch (error: unknown) {
                dispatch({ type: 'FETCH_STUDENTS_FAILURE', error: error instanceof Error ? error.message : 'An unknown error occurred' });
            }
        };

        const fetchCategories = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/v1/categories', {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`, // Add Authorization header
                        'Content-Type': 'application/json', // Optional: Add Content-Type header
                    },
                });
        
                if (!response.ok) {
                    throw new Error(`Failed to fetch categories. Status: ${response.status}`);
                }
        
                const data = await response.json();
                setCategories(data); // Store fetched categories in state
            } catch (error) {
                console.error('Error fetching categories:', error);
                alert('Failed to fetch categories.');
            }
        };
        
        fetchStudents();
        fetchCategories();
        console.log('categories: ', categories);
    }, [curricularUnitId, selectedClassSessionId]);


    const handleAttendanceChange = async (studentId: number) => {
        const newAttendance = {
            ...state.attendance,
            [studentId]: {
                isPresent: !state.attendance[studentId]?.isPresent,
                attendanceId: state.attendance[studentId]?.attendanceId || null
            }
        };

        dispatch({ type: 'TOGGLE_ATTENDANCE', studentId });

        const attendancePayload = {
            classSessionId: selectedClassSessionId,
            userEmail: state.students.find((s) => s.id === studentId)?.email,
            attended: newAttendance[studentId].isPresent,
        };

        try {
            // Send the update to the server
            const response = await fetch('http://localhost:8080/api/v1/attendance', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
                body: JSON.stringify([attendancePayload]), // Assuming the API accepts an array of attendance objects
            });

            if (!response.ok) {
                throw new Error('Failed to update attendance.');
            }
        } catch (error) {
            // Revert the change in case of an error
            dispatch({ type: 'TOGGLE_ATTENDANCE', studentId });
            alert('Failed to update attendance. Please try again.');
        }
    };

    const saveAttendance = async () => {
        if (!selectedClassSessionId) return;

        const attendedStudents = Object.entries(state.attendance)
            .map(([studentId, { isPresent }]) => ({
                classSessionId: selectedClassSessionId,
                userEmail: state.students.find((s) => s.id === parseInt(studentId, 10))?.email,
                attended: isPresent,  // Ensure that isPresent is passed correctly
            }));

        try {
            const response = await fetch('http://localhost:8080/api/v1/attendance', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
                body: JSON.stringify(attendedStudents),
            });

            if (!response.ok) throw new Error('Failed to save attendance.');

            alert('Attendance saved successfully!');
            onClose();
        } catch (error: unknown) {
            dispatch({ type: 'FETCH_STUDENTS_FAILURE', error: error instanceof Error ? error.message : 'An unknown error occurred.' });
        }
    };


    const handleReportButtonClick = async (studentId: number) => {
        setCurrentStudentId(studentId);

        const attendance = state.attendance[studentId];
        const attendanceId = attendance?.attendanceId;

        if (!attendanceId) {
            alert("Attendance ID not found for the selected student.");
            return;
        }

        try {
            // Fetch existing scores for the attendance ID
            const response = await fetch(`http://localhost:8080/api/v1/participation-scores/attendance/${attendanceId}`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
    
            if (response.status === 204) {
                // No scores exist
                setScores({});
            } else if (response.ok) {
                const scoresData: { score: number, participationCategory: { id: number } }[] = await response.json();
                const fetchedScores = scoresData.reduce((acc, { score, participationCategory }) => {
                    const categoryId = participationCategory?.id; // Extract categoryId from participationCategory
                    if (categoryId !== undefined) {  // Ensure categoryId is defined
                        acc[categoryId] = score;   // Map scores by categoryId
                    }
                    return acc;
                }, {} as { [key: number]: number });
    
                setScores(fetchedScores);
                console.log("fetched scores: ", scoresData);
                console.log('Updated scores:', fetchedScores);
            } else {
                throw new Error('Failed to fetch participation scores.');
            }
        } catch (error) {
            console.error(error);
            alert('Failed to fetch scores. Please try again.');
        }

        setShowReportForm(true);
    };

    const handleScoreChange = (categoryId: number, score: number) => {
        setScores((prevScores) => ({ ...prevScores, [categoryId]: score }));
    };

    const handleSubmitScores = async () => {
        if (!currentStudentId) return;
        console.log(currentStudentId);
        const attendance = state.attendance[currentStudentId];
        console.log(state.attendance);
        const attendanceId = attendance?.attendanceId;
        console.log(attendanceId);
        if (!attendanceId) {
            alert("Attendance ID not found.");
            return;
        }

        // Prepare categoryScores for submission
        const categoryScores = categories.map(category => {
            const score = scores[category.id];
            if (score !== undefined) {
                return {
                    categoryId: category.id,
                    score,
                };
            }
            return null;  // Skip categories with no score
        }).filter(item => item !== null); // Remove null items

        if (categoryScores.length === 0) {
            alert("No scores to submit.");
            return;
        }

        try {
            await fetch('http://localhost:8080/api/v1/participation-scores', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
                body: JSON.stringify({
                    attendanceId,
                    categoryScores,
                }),
            });

            alert('Scores submitted successfully!');
            setShowReportForm(false);
        } catch (error) {
            console.error(error);
            alert('Failed to submit scores.');
        }
    };


    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-70">
            <div className="bg-gray-900 p-8 rounded-lg shadow-xl w-11/12 max-w-2xl">
                <h2 className="text-3xl font-semibold text-white mb-6">Students</h2>

                {/* Show loading message if data is loading */}
                {state.loading && <p className="text-white">Loading...</p>}

                {/* Show error message if there's an error */}
                {state.error && <p className="text-red-500">{state.error}</p>}

                {/* Table of students */}
                <table className="min-w-full table-auto text-white">
                    <thead>
                        <tr>
                            <th className="px-4 py-2">ID</th>
                            <th className="px-4 py-2">Username</th>
                            <th className="px-4 py-2">Email</th>
                            <th className="px-4 py-2">Attended</th>
                            <th className="px-4 py-2">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {state.students.length > 0 ? (
                            state.students.map((student) => (
                                <tr key={student.id} className="hover:bg-gray-700 transition duration-200">
                                    <td className="px-4 py-2">{student.id}</td>
                                    <td className="px-4 py-2">{student.username}</td>
                                    <td className="px-4 py-2">{student.email}</td>
                                    <td className="px-4 py-2 text-center">
                                        <input
                                            type="checkbox"
                                            checked={state.attendance[student.id]?.isPresent || false}
                                            onChange={() => handleAttendanceChange(student.id)}
                                        />
                                    </td>
                                    <td className="px-4 py-2 text-center">
                                        {state.attendance[student.id].isPresent && (
                                            <button
                                                onClick={() => handleReportButtonClick(student.id)}
                                                className="bg-blue-500 text-white px-4 py-2 rounded"
                                            >
                                                Report
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan={3} className="px-4 py-2 text-center">No students found</td>
                            </tr>
                        )}
                    </tbody>
                </table>

                {/* Report Form */}
                {showReportForm && (
                    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
                        <div className="bg-gray-900  p-8 rounded-lg shadow-lg max-w-sm w-full">
                            <h3 className="text-2xl font-semibold text-white mb-4">
                                Enter Scores for Student {currentStudentId}
                            </h3>

                            <div className="space-y-6">
                                {categories.map((category) => (
                                    <div key={category.id}>
                                        <label className="text-white block mb-2">{category.name} Score:</label>
                                        <div className="flex items-center">
                                            {[1, 2, 3, 4, 5].map((value) => (
                                                <svg
                                                    key={value}
                                                    onClick={() => handleScoreChange(category.id, value)}
                                                    xmlns="http://www.w3.org/2000/svg"
                                                    viewBox="0 0 24 24"
                                                    fill={scores[category.id] >= value ? "yellow" : "gray"}
                                                    className="w-8 h-8 cursor-pointer hover:fill-yellow-500 transition duration-200"
                                                >
                                                    <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
                                                </svg>
                                            ))}
                                        </div>
                                    </div>
                                ))}
                            </div>

                            <div className="mt-6 flex justify-end gap-4">
                                <button
                                    onClick={() => setShowReportForm(false)}
                                    className="bg-red-600 text-white py-2 px-6 rounded-full hover:bg-red-700 transition duration-300"
                                >
                                    Cancel
                                </button>
                                <button
                                    onClick={handleSubmitScores}
                                    className="bg-green-600 text-white py-2 px-6 rounded-full hover:bg-green-700 transition duration-300"
                                >
                                    Submit Scores
                                </button>
                            </div>
                        </div>
                    </div>
                )}


                <div className="mt-6 flex justify-end gap-4">
                    <button
                        onClick={onClose}
                        className="bg-red-600 text-white py-2 px-6 rounded-full hover:bg-red-700 transition duration-300"
                    >
                        Close
                    </button>
                    <button
                        onClick={saveAttendance}
                        className="bg-green-600 text-white py-2 px-6 rounded-full hover:bg-green-700 transition duration-300"
                    >
                        Save
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CurriculumStudentListPopup;
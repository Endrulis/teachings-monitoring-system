import React from 'react';
import { Category } from '../../dto/Category';

interface ReportFormProps {
    showReportForm: boolean;
    currentStudentId: number | null;
    categories: Category[];
    scores: { [key: number]: number };
    handleScoreChange: (categoryId: number, value: number) => void;
    handleSubmitScores: () => void;
    setShowReportForm: (show: boolean) => void;
}

const ReportForm: React.FC<ReportFormProps> = ({
    showReportForm,
    currentStudentId,
    categories,
    scores,
    handleScoreChange,
    handleSubmitScores,
    setShowReportForm
}) => {
    if (!showReportForm) return null;

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
            <div className="bg-gray-900 p-8 rounded-lg shadow-lg max-w-sm w-full">
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
    );
};

export default ReportForm;

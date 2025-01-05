// src/components/RoleTablePopup.tsx

import { useState } from "react";

const RoleTablePopup = ({
    roles,
    loadingRoles,
    rolesError,
    createRole,
    newRoleName,
    setNewRoleName,
    isCreatingRole,
    setIsRolesPopupOpen
}: any) => {

    const [isCreateRolePopupOpen, setIsCreateRolePopupOpen] = useState(false)

    const handleCreateRolePopupOpen = () => {
        setIsCreateRolePopupOpen(true);
    };

    const handleClosePopup = () => {
        setIsCreateRolePopupOpen(false);
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg shadow-lg w-11/12 max-w-4xl p-8 relative max-h-96 overflow-y-auto">
                <h2 className="text-2xl font-bold text-gray-800 mb-4">All Roles</h2>
                <button
                    onClick={() => setIsRolesPopupOpen(false)}
                    className="absolute top-4 right-4 text-gray-600 hover:text-gray-800 text-sm"
                >
                    &times;
                </button>

                <button
                    onClick={handleCreateRolePopupOpen}
                    className="bg-green-500 hover:bg-green-600 text-white py-2 px-4 rounded-md mb-4"
                >
                    Create New Role
                </button>

                {isCreateRolePopupOpen && (
                    <div className="bg-white rounded-lg shadow-lg p-8 mb-4">
                        <form
                            onSubmit={(e) => {
                                e.preventDefault();
                                createRole(newRoleName);
                            }}
                            className="mb-4"
                        >
                            <label className="block text-gray-800 font-semibold mb-2">Role Name</label>
                            <input
                                type="text"
                                value={newRoleName}
                                onChange={(e) => setNewRoleName(e.target.value)}
                                className="w-full p-2 border border-gray-300 rounded-md mb-4"
                                placeholder="Enter role name"
                                required
                            />
                            <button
                                type="submit"
                                className={`w-full py-2 px-4 text-white rounded ${isCreatingRole ? 'bg-gray-500' : 'bg-blue-500 hover:bg-blue-600'}`}
                                disabled={isCreatingRole}
                            >
                                {isCreatingRole ? 'Creating...' : 'Create Role'}
                            </button>
                        </form>
                        <button
                            onClick={handleClosePopup}
                            className="bg-red-500 hover:bg-red-600 text-white py-2 px-4 rounded-md"
                        >
                            Cancel
                        </button>
                    </div>
                )}

                {loadingRoles ? (
                    <p className="text-center text-gray-600">Loading roles...</p>
                ) : rolesError ? (
                    <p className="text-center text-red-500">{rolesError}</p>
                ) : (
                    <table className="w-full border-collapse border border-gray-500 text-gray-800">
                        <thead>
                            <tr className="bg-gray-700 text-white">
                                <th className="border border-gray-600 px-4 py-2">ID</th>
                                <th className="border border-gray-600 px-4 py-2">Role Name</th>
                            </tr>
                        </thead>
                        <tbody>
                            {roles.map((role: any) => (
                                <tr key={role.id} className="hover:bg-gray-200">
                                    <td className="border border-gray-600 px-4 py-2">{role.id}</td>
                                    <td className="border border-gray-600 px-4 py-2">{role.name}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}


            </div>
        </div>
    );
};

export default RoleTablePopup;

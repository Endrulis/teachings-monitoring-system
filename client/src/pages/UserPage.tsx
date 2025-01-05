//src/UserPage.tsx

import React, { useEffect, useState } from 'react';

import UserTablePopup from '../components/UserTablePopup';
import ClassesPopup from '../components/popups/ClassesPopup';
import RoleTablePopup from '../components/RoleTablePopup';
import CurricularUnitsPopup from '../components/popups/CurricularUnitsPopup';

import { useToken, useUsers, useRoles, useClasses, useCurricularUnits } from '../hooks';

import { CurricularUnitDto } from '../dto/CurricularUnitDto';

const UserPage: React.FC = () => {

    const { decodedToken, error: tokenError } = useToken();
    const { users, loadingUsers, usersError, fetchUsers } = useUsers();
    const { roles, loadingRoles, rolesError, fetchRoles, createRole, newRoleName, setNewRoleName, isCreatingRole } = useRoles();
    const { selectedUnitClasses, isClassesPopupOpen, fetchClassesByCurricularUnitId, setIsClassesPopupOpen } = useClasses();
    const [selectedCurricularUnit, setSelectedCurricularUnit] = useState<CurricularUnitDto | null>(null);

    const [isUsersPopupOpen, setIsUsersPopupOpen] = useState(false);
    const [isRolesPopupOpen, setIsRolesPopupOpen] = useState(false);
    const [isCurricularUnitsPopupOpen, setIsCurricularUnitsPopupOpen] = useState(false);

    const isAdmin = decodedToken?.roles?.includes("ADMIN");
    const isAuthenticated = decodedToken?.roles?.includes("TEACHER") || decodedToken?.roles?.includes("STUDENT");

    const { curricularUnits } = useCurricularUnits(decodedToken?.sub);
    
    const handleUnitClick = (unit: CurricularUnitDto) => {
        setSelectedCurricularUnit(unit);
        fetchClassesByCurricularUnitId(unit.id); // Fetch classes for the selected unit
    };

    useEffect(() => {
        if (isAuthenticated) {
            setIsCurricularUnitsPopupOpen(true);
        }
    }, [isAuthenticated]);

    return (
        <div className="bg-gray-800 min-h-screen flex items-center justify-center">
            <div className="bg-white text-gray-800 p-8 rounded-lg shadow-lg w-full max-w-md">
                <h1 className="text-3xl font-bold text-center text-gray-800 mb-4">Welcome to the User Page</h1>

                {tokenError ? (
                    <p className="text-xl text-center text-red-500">{tokenError}</p>
                ) : (
                    decodedToken ? (
                        <div>
                            <p className="text-xl text-center text-gray-600">Decoded Token:</p>
                            <pre className="bg-gray-100 p-4 text-sm text-gray-700 rounded-md break-words">
                                {JSON.stringify(decodedToken, null, 2)}
                            </pre>

                            {isAuthenticated && (
                                <div className="space-y-4">
                                    <button
                                        onClick={() => setIsCurricularUnitsPopupOpen(true)}
                                        className="mt-4 w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600"
                                    >
                                        View My Curricular Units
                                    </button>
                                </div>
                            )}

                            {isAdmin && (
                                <div className="space-y-4">
                                    <button
                                        onClick={() => {
                                            setIsUsersPopupOpen(true);
                                            fetchUsers();
                                        }}
                                        className="mt-4 w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600"
                                    >
                                        Manage Users
                                    </button>
                                    <button
                                        onClick={() => {
                                            setIsRolesPopupOpen(true);
                                            fetchRoles();
                                        }}
                                        className="w-full bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600"
                                    >
                                        Manage Roles
                                    </button>
                                </div>

                            )}
                        </div>
                    ) : (
                        <p className="text-xl text-center text-gray-600">Loading token data...</p>
                    )
                )}
            </div>

            <CurricularUnitsPopup
                curricularUnits={curricularUnits}
                isOpen={isCurricularUnitsPopupOpen}
                onClose={() => setIsCurricularUnitsPopupOpen(false)}
                onUnitClick={handleUnitClick}
            />

            <ClassesPopup
                classes={selectedUnitClasses || []}
                isOpen={isClassesPopupOpen}
                onClose={() => setIsClassesPopupOpen(false)}
                unitName={selectedCurricularUnit?.name || ""}
                curricularUnitId={selectedCurricularUnit ? selectedCurricularUnit.id : null}/>

            {isUsersPopupOpen && (
                <UserTablePopup
                    users={users}
                    loadingUsers={loadingUsers}
                    usersError={usersError}
                    setIsUsersPopupOpen={setIsUsersPopupOpen}
                />
            )}
            {isRolesPopupOpen && (
                <RoleTablePopup
                    roles={roles}
                    loadingRoles={loadingRoles}
                    rolesError={rolesError}
                    createRole={createRole}
                    newRoleName={newRoleName}
                    setNewRoleName={setNewRoleName}
                    isCreatingRole={isCreatingRole}
                    setIsRolesPopupOpen={setIsRolesPopupOpen}
                />
            )}


        </div>
    );
};

export default UserPage;

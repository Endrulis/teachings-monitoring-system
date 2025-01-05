// src/components/UserPopup.tsx

const UserTablePopup = ({ users, loadingUsers, usersError, setIsUsersPopupOpen }: any) => {
  return (
    <div className="fixed inset-0 bg-black bg-opacity-75 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-lg w-11/12 max-w-4xl p-8 relative max-h-96 overflow-y-auto">
        <h2 className="text-2xl font-bold text-gray-800 mb-4">All Users</h2>
        <button
          onClick={() => setIsUsersPopupOpen(false)}
          className="absolute top-4 right-4 text-gray-600 hover:text-gray-800 text-sm"
        >
          &times;
        </button>

        {loadingUsers ? (
          <p className="text-center text-gray-600">Loading users...</p>
        ) : usersError ? (
          <p className="text-center text-red-500">{usersError}</p>
        ) : (
          <table className="w-full border-collapse border border-gray-500 text-gray-800">
            <thead>
              <tr className="bg-gray-700 text-white">
                <th className="border border-gray-600 px-4 py-2">ID</th>
                <th className="border border-gray-600 px-4 py-2">Username</th>
                <th className="border border-gray-600 px-4 py-2">Email</th>
                <th className="border border-gray-600 px-4 py-2">Roles</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user: any) => (
                <tr key={user.id} className="hover:bg-gray-200">
                  <td className="border border-gray-600 px-4 py-2">{user.id}</td>
                  <td className="border border-gray-600 px-4 py-2">{user.username}</td>
                  <td className="border border-gray-600 px-4 py-2">{user.email}</td>
                  <td className="border border-gray-600 px-4 py-2">
                    {user.roles && user.roles.length > 0
                      ? user.roles.map((role: any) => role.name).join(', ')
                      : 'No roles assigned'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default UserTablePopup;

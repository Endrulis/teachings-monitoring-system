
import { Link } from 'react-router-dom';
import HelloWorld from '../components/HelloWorld';

const HomePage: React.FC = () => {
  return (
    <div className="bg-gray-100 min-h-screen flex flex-col items-center justify-center p-6">
      <div className="bg-white shadow-lg rounded-lg p-8 w-full max-w-3xl">
        <h1 className="text-4xl font-semibold text-center text-gray-800 mb-4">
          Welcome to the Teaching Activities Management System
        </h1>
        <h2 className="text-xl text-center text-gray-600 mb-8">
          Made by <span className="font-bold text-gray-800">Rokas Endrulis</span> and{' '}
          <span className="font-bold text-gray-800">Aronas Giacius</span>
        </h2>

        <HelloWorld />

        {/* Navbar to switch between login and register */}
        <div className="flex justify-center mb-4">
          <Link
            to="/login"
            className="px-4 py-2 mx-2 text-white rounded bg-indigo-600"
          >
            Login
          </Link>
          <Link
            to="/register"
            className="px-4 py-2 mx-2 text-white rounded bg-indigo-600"
          >
            Sign Up
          </Link>
        </div>
      </div>
    </div>
  );
};

export default HomePage;

import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

const ProtectedRoute = ({ children }) => {
  const { accessToken, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  return accessToken ? children : <Navigate to="/" replace />;
};

export default ProtectedRoute;

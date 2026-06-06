import { createContext, useContext, useEffect, useState } from "react";
import { refreshAccessToken } from "../api/authApi";
import { setToken } from "../api/axiosPrivate";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [accessToken, setAccessToken] = useState(null);
  const [loading, setLoading] = useState(true);

  const login = (token) => {
    setAccessToken(token);
    setToken(token);
  };

  const logout = () => {
    setAccessToken(null);
    setToken(null);
  };

  useEffect(() => {
    const persistLogin = async () => {
      try {
        const newAccessToken = await refreshAccessToken();

        setAccessToken(newAccessToken);

        setToken(newAccessToken);

      } catch (error) {
        console.log("User not logged in");
      } finally {
        setLoading(false);
      }
    };

    persistLogin();
  }, []);

  return (
    <AuthContext.Provider
      value={{
        accessToken,
        setAccessToken,
        login,
        logout,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
import axios from "axios";

export const sendLoginEvent = (LoginUserData) => {
  return axios.post(
    "http://localhost:8000/auth/v1/login",
    LoginUserData,
    {
      withCredentials: true,
    }
  );
};
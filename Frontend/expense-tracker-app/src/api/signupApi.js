import axios from "axios";

export const sendSignupEvent = (signupUserData) => {
  return axios.post(
    "http://localhost:8000/auth/v1/signup",
    signupUserData,
    {
      withCredentials: true,
    }
  );
};
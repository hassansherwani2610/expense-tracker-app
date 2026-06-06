import axiosInstance from "./axiosInstance";

export const refreshAccessToken = async () => {
  const response = await axiosInstance.post("/auth/v1/refreshToken");

  return response.data.access_token;
};
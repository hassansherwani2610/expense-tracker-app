import axiosInstance from "./axiosInstance";
import { refreshAccessToken } from "./authApi";

let accessToken = null;

export const setToken = (token) => {
  accessToken = token;
};

axiosInstance.interceptors.request.use(

  (config) => {

    if (accessToken) {

      config.headers.Authorization = `Bearer ${accessToken}`;

    }

    return config;
  },

  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(

  (response) => response,

  async (error) => {

    const originalRequest = error.config;

    if (
      error.response?.status === 401 &&
      !originalRequest._retry
    ) {

      originalRequest._retry = true;

      try {

        const newAccessToken = await refreshAccessToken();

        setToken(newAccessToken);

        originalRequest.headers.Authorization =
          `Bearer ${newAccessToken}`;

        return axiosInstance(originalRequest);

      } catch (refreshError) {

        return Promise.reject(refreshError);

      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;
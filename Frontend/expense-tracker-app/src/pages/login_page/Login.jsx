import { useState } from "react";
import { FiLock, FiEye, FiEyeOff } from "react-icons/fi";
import { FaRegUserCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { sendLoginEvent } from "../../api/loginApi";
import { useAuth } from "../../hooks/useAuth";
import { setToken } from "../../api/axiosPrivate";
import { useEffect } from "react";

const Login = () => {
  const [focus, setFocus] = useState(null);

  const navigate = useNavigate();

  const { login } = useAuth();

  const { accessToken } = useAuth();

  // Show / Hide Password
  const [showPassword, setShowPassword] = useState(false);

  // Form State
  const [form, setForm] = useState({
    username: "",
    password: "",
  });

  // Handle Input Change
  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  useEffect(() => {
    if (accessToken) {
      navigate("/dashboard");
    }
  }, [accessToken, navigate]);

  // Handle Login Submit
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await sendLoginEvent(form);

      // Get Access Token
      const token = response.data.access_token;

      // Store in MEMORY
      login(token);

      setToken(token);

      console.log("Login Success:", token);

      /*
        Backend Response:
        {
          accessToken: "jwt-token"
        }
      */

      // Store Access Token in localStorage
      // localStorage.setItem("accessToken", response.data.access_token);

      /*
        Refresh Token is automatically stored
        inside HttpOnly Cookie by Spring Boot
      */

      alert("Login Successful 🚀");

      // Redirect after login
      navigate("/dashboard");
    } catch (error) {
      console.error("Login Failed:", error);

      if (error.response?.status === 401) {
        alert("Invalid Username or Password ❌");
      } else {
        alert("Something went wrong ❌");
      }
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      {/* Card */}
      <div className="w-full max-w-md p-8 rounded-2xl bg-white/5 backdrop-blur-xl border border-white/10 shadow-xl transition-all duration-300 hover:shadow-2xl">
        {/* Heading */}
        <h1 className="text-3xl font-semibold mb-6 text-center">
          <span className="text-teal-300/90">Welcome</span>{" "}
          <span className="text-amber-200/90">Back</span>
        </h1>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-5">
          {/* Username */}
          <div
            className={`flex items-center gap-3 px-4 py-3 rounded-lg border transition-all duration-300 
            ${
              focus === "username"
                ? "border-teal-400 bg-white/10"
                : "border-white/10 bg-white/5"
            }`}
          >
            <FaRegUserCircle className="text-teal-300" />

            <input
              type="text"
              name="username"
              placeholder="Username"
              value={form.username}
              onChange={handleChange}
              className="w-full bg-transparent outline-none text-slate-200 placeholder-slate-400"
              onFocus={() => setFocus("username")}
              onBlur={() => setFocus(null)}
            />
          </div>

          {/* Password */}
          <div
            className={`flex items-center gap-3 px-4 py-3 rounded-lg border transition-all duration-300 
            ${
              focus === "password"
                ? "border-teal-400 bg-white/10"
                : "border-white/10 bg-white/5"
            }`}
          >
            <FiLock className="text-teal-300" />

            <input
              type={showPassword ? "text" : "password"}
              name="password"
              placeholder="Password"
              value={form.password}
              onChange={handleChange}
              className="w-full bg-transparent outline-none text-slate-200 placeholder-slate-400"
              onFocus={() => setFocus("password")}
              onBlur={() => setFocus(null)}
            />

            {/* Eye Icon */}
            {form.password.length > 0 && (
              <button
                type="button"
                onMouseDown={(e) => e.preventDefault()}
                onClick={() => setShowPassword(!showPassword)}
                className="cursor-pointer text-slate-400 hover:text-teal-300 transition-colors"
              >
                {showPassword ? <FiEyeOff size={18} /> : <FiEye size={18} />}
              </button>
            )}
          </div>

          {/* Button */}
          <button
            type="submit"
            className="cursor-pointer w-full py-3 rounded-lg bg-amber-400/80 hover:bg-amber-400 transition-all duration-300 text-slate-900 font-medium hover:scale-[1.02] active:scale-[0.98]"
          >
            Login
          </button>
        </form>

        {/* Footer */}
        <p className="text-sm text-slate-400 mt-6 text-center">
          Don’t have an account?{" "}
          <span
            onClick={() => navigate("/signup")}
            className="text-teal-300 cursor-pointer hover:underline"
          >
            Sign Up
          </span>
        </p>
      </div>
    </div>
  );
};

export default Login;

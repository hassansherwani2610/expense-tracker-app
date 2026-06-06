import { useState, useEffect } from "react";
import {
  FiUser,
  FiMail,
  FiLock,
  FiPhone,
  FiEye,
  FiEyeOff,
} from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import { sendSignupEvent } from "../../api/signupApi";

const Signup = () => {
  const [focus, setFocus] = useState(null);
  const navigate = useNavigate();

  // Show / Hide Password States
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    username: "",
    phone: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [passwordError, setPasswordError] = useState("");
  const [confirmError, setConfirmError] = useState("");

  // Password Regex
  const passwordRegex =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&.#])[A-Za-z\d@$!%*?&.#]{8,}$/;

  // Handle Input Change
  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  // Real-time Validation
  useEffect(() => {
    // Password Validation
    if (form.password.length > 0) {
      if (!passwordRegex.test(form.password)) {
        setPasswordError(
          "Must contain Capital, Small, Number, Special Character & 8+ characters"
        );
      } else {
        setPasswordError("");
      }
    } else {
      setPasswordError("");
    }

    // Confirm Password Validation
    if (form.confirmPassword.length > 0) {
      if (form.password !== form.confirmPassword) {
        setConfirmError("Passwords do not match");
      } else {
        setConfirmError("Passwords match ✓");
      }
    } else {
      setConfirmError("");
    }
  }, [form.password, form.confirmPassword]);

  // Handle Form Submit
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Stop if validation fails
    if (passwordError || form.password !== form.confirmPassword) {
      return;
    }

    // Remove confirmPassword before sending to backend
    const { confirmPassword, ...signupData } = form;

    try {
      const response = await sendSignupEvent(signupData);

      console.log("Signup Success:", response.data);

      alert("Account created successfully 🚀");

      // Redirect to Login page
      navigate("/");
    } catch (error) {
      console.error("Signup Failed:", error);

      const errorMessage =
        error.response?.data?.message ||
        error.response?.data ||
        "Signup failed ❌";

      alert(errorMessage);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-md p-8 rounded-2xl bg-white/5 backdrop-blur-xl border border-white/10 shadow-xl transition-all duration-300 hover:shadow-2xl">
        {/* Heading */}
        <h1 className="text-3xl font-semibold mb-6 text-center">
          <span className="text-teal-300/90">Create</span>{" "}
          <span className="text-amber-200/90">Account</span>
        </h1>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          {/* First Name */}
          <InputBox
            icon={<FiUser className="text-teal-300" />}
            placeholder="First Name"
            name="firstName"
            value={form.firstName}
            onChange={handleChange}
            focus={focus}
            setFocus={setFocus}
            id="firstName"
          />

          {/* Last Name */}
          <InputBox
            icon={<FiUser className="text-teal-300" />}
            placeholder="Last Name"
            name="lastName"
            value={form.lastName}
            onChange={handleChange}
            focus={focus}
            setFocus={setFocus}
            id="lastName"
          />

          {/* Username */}
          <InputBox
            icon={<FiUser className="text-teal-300" />}
            placeholder="Username"
            name="username"
            value={form.username}
            onChange={handleChange}
            focus={focus}
            setFocus={setFocus}
            id="username"
          />

          {/* Phone Number */}
          <InputBox
            icon={<FiPhone className="text-teal-300" />}
            placeholder="Phone Number"
            name="phone"
            type="tel"
            value={form.phone}
            onChange={(e) => {
              const value = e.target.value.replace(/\D/g, "");
              setForm({
                ...form,
                phone: value,
              });
            }}
            focus={focus}
            setFocus={setFocus}
            id="phone"
          />

          {/* Email */}
          <InputBox
            icon={<FiMail className="text-teal-300" />}
            placeholder="Email"
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            focus={focus}
            setFocus={setFocus}
            id="email"
          />

          {/* Password */}
          <InputBox
            icon={<FiLock className="text-teal-300" />}
            placeholder="Password"
            name="password"
            type={showPassword ? "text" : "password"}
            value={form.password}
            onChange={handleChange}
            focus={focus}
            setFocus={setFocus}
            id="password"
            isPassword={true}
            showPassword={showPassword}
            togglePassword={() => setShowPassword(!showPassword)}
          />

          {/* Password Validation Message */}
          {passwordError && (
            <p className="text-red-400 text-sm">{passwordError}</p>
          )}

          {/* Confirm Password */}
          <InputBox
            icon={<FiLock className="text-teal-300" />}
            placeholder="Confirm Password"
            name="confirmPassword"
            type={showConfirmPassword ? "text" : "password"}
            value={form.confirmPassword}
            onChange={handleChange}
            focus={focus}
            setFocus={setFocus}
            id="confirmPassword"
            isPassword={true}
            showPassword={showConfirmPassword}
            togglePassword={() =>
              setShowConfirmPassword(!showConfirmPassword)
            }
          />

          {/* Confirm Password Message */}
          {confirmError && (
            <p
              className={`text-sm ${
                confirmError.includes("✓")
                  ? "text-green-400"
                  : "text-red-400"
              }`}
            >
              {confirmError}
            </p>
          )}

          {/* Submit Button */}
          <button
            type="submit"
            className="cursor-pointer w-full py-3 rounded-lg bg-amber-400/80 hover:bg-amber-400 transition-all duration-300 text-slate-900 font-medium hover:scale-[1.02] active:scale-[0.98]"
          >
            Sign Up
          </button>
        </form>

        {/* Login Link */}
        <p className="text-sm text-slate-400 mt-6 text-center">
          Already have an account?{" "}
          <span
            onClick={() => navigate("/login")}
            className="text-teal-300 cursor-pointer hover:underline"
          >
            Login
          </span>
        </p>
      </div>
    </div>
  );
};

/* Reusable Input Component */
const InputBox = ({
  icon,
  placeholder,
  name,
  value,
  onChange,
  focus,
  setFocus,
  id,
  type = "text",
  isPassword = false,
  showPassword = false,
  togglePassword,
}) => {
  return (
    <div
      className={`flex items-center gap-3 px-4 py-3 rounded-lg border transition-all duration-300 ${
        focus === id
          ? "border-teal-400 bg-white/10"
          : "border-white/10 bg-white/5"
      }`}
    >
      {icon}

      <input
        type={type}
        name={name}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        onFocus={() => setFocus(id)}
        onBlur={() => setFocus(null)}
        className="w-full bg-transparent outline-none text-slate-200 placeholder-slate-400"
      />

      {/* Eye Icon appears only when user has typed something */}
      {isPassword && value.length > 0 && (
        <button
          type="button"
          onMouseDown={(e) => e.preventDefault()} // Prevent input blur when clicking
          onClick={togglePassword}
          className="cursor-pointer text-slate-400 hover:text-teal-300 transition-colors"
        >
          {showPassword ? <FiEyeOff size={18} /> : <FiEye size={18} />}
        </button>
      )}
    </div>
  );
};

export default Signup;
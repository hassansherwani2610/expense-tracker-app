import {
  BrowserRouter as Router,
  Routes,
  Route,
  useLocation,
} from "react-router-dom";
import { AnimatePresence, motion } from "framer-motion";

import Background from "./components/background/Background";
import Footer from "./components/footer/Footer";

import Login from "./pages/login_page/Login";
import Signup from "./pages/signup_page/Signup";
import Privacy from "./pages/privacy_page/Privacy";
import Terms from "./pages/terms_page/Terms";
import Support from "./pages/support_page/Support";
import Dashboard from "./pages/dashboard_page/Dashboard";
import ProtectedRoute from "./utils/ProtectedRoute";

import { Navigate } from "react-router-dom";

/* GLOBAL PAGE ANIMATION */
const PageWrapper = ({ children }) => {
  return (
    <motion.div
      initial={{ opacity: 0, x: 60 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -60 }}
      transition={{ duration: 0.35, ease: "easeInOut" }}
      className="w-full"
    >
      {children}
    </motion.div>
  );
};

/* ROUTES WITH ANIMATION */
const AnimatedRoutes = () => {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        {/* Default Page = Login */}
        <Route
          path="/login"
          element={
            <PageWrapper>
              <Login />
            </PageWrapper>
          }
        />

        {/* Signup Page */}
        <Route
          path="/signup"
          element={
            <PageWrapper>
              <Signup />
            </PageWrapper>
          }
        />

        {/* Protected Dashboard */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <PageWrapper>
                <Dashboard />
              </PageWrapper>
            </ProtectedRoute>
          }
        />

        {/* Other Pages */}
        <Route
          path="/privacy"
          element={
            <PageWrapper>
              <Privacy />
            </PageWrapper>
          }
        />

        <Route
          path="/terms"
          element={
            <PageWrapper>
              <Terms />
            </PageWrapper>
          }
        />

        <Route
          path="/support"
          element={
            <PageWrapper>
              <Support />
            </PageWrapper>
          }
        />

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </AnimatePresence>
  );
};

function App() {
  return (
    <Router>
      <div className="relative min-h-screen flex flex-col text-white">
        <Background />

        {/* Header */}
        <div className="relative z-10 p-6">
          <h1 className="text-3xl sm:text-4xl md:text-5xl font-semibold tracking-tight">
            <span className="text-teal-300/90">Expense</span>{" "}
            <span className="text-amber-200/90">Tracker</span>
          </h1>
        </div>

        {/* Content */}
        <div className="relative z-10 flex-grow overflow-hidden">
          <AnimatedRoutes />
        </div>

        {/* Footer */}
        <div className="relative z-10">
          <Footer />
        </div>
      </div>
    </Router>
  );
}

export default App;

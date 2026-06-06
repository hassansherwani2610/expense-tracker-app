import { FiGithub, FiLinkedin, FiTwitter } from "react-icons/fi";
import { useNavigate } from "react-router-dom";

const Footer = () => {
  const navigate = useNavigate();

  return (
    <footer className="w-full mt-10 px-6 py-6 border-t border-white/10 bg-white/5 backdrop-blur-md">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">

        {/* Left */}
        <p className="text-sm text-slate-400 text-center md:text-left">
          © {new Date().getFullYear()}{" "}
          <span className="text-teal-300/90 font-medium">Expense</span>{" "}
          <span className="text-amber-200/90 font-medium">Tracker</span>.
          All rights reserved.
        </p>

        {/* Center */}
        <div className="flex items-center gap-6 text-sm text-slate-400">
          <span onClick={() => navigate("/privacy")} className="cursor-pointer hover:text-teal-300 transition">
            Privacy
          </span>
          <span onClick={() => navigate("/terms")} className="cursor-pointer hover:text-teal-300 transition">
            Terms
          </span>
          <span onClick={() => navigate("/support")} className="cursor-pointer hover:text-teal-300 transition">
            Support
          </span>
        </div>

        {/* Right */}
        <div className="flex items-center gap-4 text-slate-400">
          <a className="p-2 rounded-lg hover:bg-white/10 hover:text-teal-300 transition">
            <FiGithub size={18} />
          </a>
          <a className="p-2 rounded-lg hover:bg-white/10 hover:text-teal-300 transition">
            <FiLinkedin size={18} />
          </a>
          <a className="p-2 rounded-lg hover:bg-white/10 hover:text-teal-300 transition">
            <FiTwitter size={18} />
          </a>
        </div>

      </div>
    </footer>
  );
};

export default Footer;
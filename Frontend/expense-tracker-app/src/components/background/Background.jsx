const Background = () => {
  return (
    <div className="fixed inset-0 -z-10 overflow-hidden bg-[#0f172a]">
      
      {/* Main Gradient Layer */}
      <div className="absolute inset-0 bg-gradient-to-br from-[#0f172a] via-[#020617] to-[#020617]" />

      {/* Radial Glow (Top) */}
      <div className="absolute top-[-20%] left-[50%] w-[800px] h-[800px] bg-purple-600 opacity-20 blur-[120px] rounded-full translate-x-[-50%]" />

      {/* Radial Glow (Bottom Left) */}
      <div className="absolute bottom-[-20%] left-[-10%] w-[600px] h-[600px] bg-cyan-500 opacity-20 blur-[120px] rounded-full" />

      {/* Radial Glow (Bottom Right) */}
      <div className="absolute bottom-[-20%] right-[-10%] w-[600px] h-[600px] bg-indigo-500 opacity-20 blur-[120px] rounded-full" />

      {/* Subtle Grid Overlay */}
      <div className="absolute inset-0 bg-[linear-gradient(rgba(255,255,255,0.03)_1px,transparent_1px),linear-gradient(to_right,rgba(255,255,255,0.03)_1px,transparent_1px)] bg-[size:40px_40px]" />

      {/* Soft Vignette */}
      <div className="absolute inset-0 bg-gradient-to-t from-black/40 via-transparent to-black/30" />

    </div>
  );
};

export default Background;
const Privacy = () => {
  return (
    <div className="max-w-4xl mx-auto px-6 py-12 text-slate-300">
      
      <h1 className="heading-xl mb-6 text-teal-300/90">
        Privacy Policy
      </h1>

      <p className="text-body mb-4">
        Your privacy is important to us. This Expense Tracker application
        collects only the necessary information to provide and improve our
        services.
      </p>

      <h2 className="heading-md mt-6 mb-2 text-amber-200/90">
        Information We Collect
      </h2>
      <p className="text-body mb-4">
        We may collect your name, email, and financial entries you input into
        the system. No sensitive banking credentials are stored.
      </p>

      <h2 className="heading-md mt-6 mb-2 text-amber-200/90">
        How We Use Your Data
      </h2>
      <p className="text-body mb-4">
        Your data is used to display insights, track expenses, and improve your
        experience. We do not sell your data.
      </p>

      <h2 className="heading-md mt-6 mb-2 text-amber-200/90">
        Security
      </h2>
      <p className="text-body">
        We use industry-standard practices to protect your information. However,
        no system is completely secure.
      </p>

    </div>
  );
};

export default Privacy;
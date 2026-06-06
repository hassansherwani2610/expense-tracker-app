const Terms = () => {
  return (
    <div className="max-w-4xl mx-auto px-6 py-12 text-slate-300">

      <h1 className="heading-xl mb-6 text-teal-300/90">
        Terms & Conditions
      </h1>

      <p className="text-body mb-4">
        By using this Expense Tracker, you agree to the following terms.
      </p>

      <h2 className="heading-md mt-6 mb-2 text-amber-200/90">
        Usage
      </h2>
      <p className="text-body mb-4">
        This application is intended for personal financial tracking only.
        Misuse or unauthorized access is strictly prohibited.
      </p>

      <h2 className="heading-md mt-6 mb-2 text-amber-200/90">
        Responsibility
      </h2>
      <p className="text-body mb-4">
        Users are responsible for maintaining the confidentiality of their
        accounts and data.
      </p>

      <h2 className="heading-md mt-6 mb-2 text-amber-200/90">
        Limitation of Liability
      </h2>
      <p className="text-body">
        We are not liable for any financial decisions made based on the data
        provided by this application.
      </p>

    </div>
  );
};

export default Terms;
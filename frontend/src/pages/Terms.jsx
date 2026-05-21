import { useTranslation } from "react-i18next";

const Terms = () => {
  const { t } = useTranslation("common");

  return (
    <div className="min-h-screen bg-background text-white">
      <div className="max-w-3xl mx-auto px-6 py-16">
        <h1 className="text-3xl font-bold mb-8">{t("terms.title")}</h1>
        <div className="space-y-6 text-white/80 leading-relaxed">
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("terms.intro")}</h2>
            <p>{t("terms.content")}</p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("terms.acceptance")}</h2>
            <p>{t("terms.acceptanceContent")}</p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("terms.use")}</h2>
            <p>{t("terms.useContent")}</p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("terms.disclaimer")}</h2>
            <p>{t("terms.disclaimerContent")}</p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("terms.termination")}</h2>
            <p>{t("terms.terminationContent")}</p>
          </section>
        </div>
      </div>
    </div>
  );
};

export default Terms;
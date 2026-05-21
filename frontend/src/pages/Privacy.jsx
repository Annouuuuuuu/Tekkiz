import { useTranslation } from "react-i18next";

const Privacy = () => {
  const { t } = useTranslation("common");

  return (
    <div className="min-h-screen bg-background text-white">
      <div className="max-w-3xl mx-auto px-6 py-16">
        <h1 className="text-3xl font-bold mb-8">{t("privacy.title")}</h1>
        <div className="space-y-6 text-white/80 leading-relaxed">
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("privacy.intro")}</h2>
            <p>{t("privacy.content")}</p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("privacy.dataCollection")}</h2>
            <p>{t("privacy.dataCollectionContent")}</p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("privacy.dataUsage")}</h2>
            <p>{t("privacy.dataUsageContent")}</p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("privacy.cookies")}</h2>
            <p>{t("privacy.cookiesContent")}</p>
          </section>
          <section>
            <h2 className="text-xl font-semibold mb-3">{t("privacy.rights")}</h2>
            <p>{t("privacy.rightsContent")}</p>
          </section>
        </div>
      </div>
    </div>
  );
};

export default Privacy;

import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ArrowLeft } from "lucide-react";

const SECTIONS = [
  { title: "collectionTitle", content: "collectionContent" },
  { title: "usageTitle",      content: "usageContent" },
  { title: "sharingTitle",    content: "sharingContent" },
  { title: "cookiesTitle",    content: "cookiesContent" },
  { title: "securityTitle",   content: "securityContent" },
  { title: "rightsTitle",     content: "rightsContent" },
  { title: "changesTitle",    content: "changesContent" },
  { title: "contactTitle",    content: "contactContent" },
];

const Privacy = () => {
  const { t } = useTranslation("common");

  return (
    <div className="min-h-screen bg-[#080808] text-white">
      <div className="max-w-2xl mx-auto px-6 py-16">

        <Link
          to="/"
          className="inline-flex items-center gap-2 text-sm text-white/40 hover:text-white/70 transition-colors mb-12"
        >
          <ArrowLeft className="w-4 h-4" />
          {t("privacy.backToHome")}
        </Link>

        <header className="mb-12">
          <h1 className="text-3xl font-black tracking-tight mb-3">
            {t("privacy.title")}
          </h1>
          <p className="text-sm text-white/30">{t("privacy.lastUpdated")}</p>
          <p className="mt-6 text-white/60 leading-relaxed">
            {t("privacy.intro")}
          </p>
        </header>

        <div className="space-y-10">
          {SECTIONS.map(({ title, content }, i) => (
            <section key={title} className="border-t border-white/[0.07] pt-8">
              <div className="flex items-start gap-4">
                <span className="text-[11px] font-black tabular-nums text-white/20 w-6 shrink-0 pt-0.5">
                  {String(i + 1).padStart(2, "0")}
                </span>
                <div>
                  <h2 className="text-base font-bold text-white mb-3">
                    {t(`privacy.${title}`)}
                  </h2>
                  <p className="text-sm text-white/55 leading-relaxed">
                    {t(`privacy.${content}`)}
                  </p>
                </div>
              </div>
            </section>
          ))}
        </div>

      </div>
    </div>
  );
};

export default Privacy;

import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { ArrowLeft } from "lucide-react";

const SECTIONS = [
  { title: "acceptanceTitle", content: "acceptanceContent" },
  { title: "serviceTitle",    content: "serviceContent" },
  { title: "accountTitle",    content: "accountContent" },
  { title: "useTitle",        content: "useContent" },
  { title: "ipTitle",         content: "ipContent" },
  { title: "liabilityTitle",  content: "liabilityContent" },
  { title: "terminationTitle",content: "terminationContent" },
  { title: "changesTitle",    content: "changesContent" },
  { title: "contactTitle",    content: "contactContent" },
];

const Terms = () => {
  const { t } = useTranslation("common");

  return (
    <div className="min-h-screen bg-[#080808] text-white">
      <div className="max-w-2xl mx-auto px-6 py-16">

        <Link
          to="/"
          className="inline-flex items-center gap-2 text-sm text-white/40 hover:text-white/70 transition-colors mb-12"
        >
          <ArrowLeft className="w-4 h-4" />
          {t("terms.backToHome")}
        </Link>

        <header className="mb-12">
          <h1 className="text-3xl font-black tracking-tight mb-3">
            {t("terms.title")}
          </h1>
          <p className="text-sm text-white/30">{t("terms.lastUpdated")}</p>
          <p className="mt-6 text-white/60 leading-relaxed">
            {t("terms.intro")}
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
                    {t(`terms.${title}`)}
                  </h2>
                  <p className="text-sm text-white/55 leading-relaxed">
                    {t(`terms.${content}`)}
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

export default Terms;

import { useTranslation } from "react-i18next";
import { Card, CardContent } from "@/components/ui/card";
import { Mail, Github, Globe } from "lucide-react";

const AboutPage = () => {
  const { t } = useTranslation("common");

  return (
    <div className="p-6 flex items-center justify-center min-h-[calc(100vh-4rem)]">
      <Card className="max-w-lg w-full">
        <CardContent className="p-8 text-center space-y-6">
          {/* Logo */}
          <div className="flex justify-center">
            <div className="flex items-center gap-3">
              <div className="flex items-center justify-center w-16 h-16 rounded-xl bg-primary">
                <span className="text-3xl font-black text-primary-foreground">T</span>
              </div>
            </div>
          </div>

          {/* Mission */}
          <div className="space-y-2">
            <h2 className="text-2xl font-bold">{t("about.title")}</h2>
            <p className="text-muted-foreground">
              {t("about.mission")}
            </p>
          </div>

          {/* Description */}
          <div className="space-y-2 text-sm text-muted-foreground">
            <p>
              {t("about.description")}
            </p>
          </div>

          {/* Version */}
          <div className="pt-4 border-t">
            <p className="text-sm text-muted-foreground">{t("about.version")}</p>
          </div>

          {/* Contact */}
          <div className="flex justify-center gap-6 pt-2">
            <a
              href="mailto:contact@tekizz.com"
              className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground transition-colors"
            >
              <Mail className="w-4 h-4" />
              {t("about.contact")}
            </a>
            <a
              href="https://github.com/tekizz"
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground transition-colors"
            >
              <Github className="w-4 h-4" />
              {t("about.github")}
            </a>
            <a
              href="https://tekizz.com"
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground transition-colors"
            >
              <Globe className="w-4 h-4" />
              {t("about.website")}
            </a>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default AboutPage;

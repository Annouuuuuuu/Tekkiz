import { useState } from "react";
import { motion } from "framer-motion";
import { Button } from "@/components/ui/button";
import { ArrowRight, ChevronDown, Zap, Target, Trophy, BarChart3, Users, Gamepad2, BookOpen } from "lucide-react";
import { useTranslation } from "react-i18next";
import AuthModal from "@/components/auth/AuthModal";
import tekizzPreview from "@/assets/images/Tekizz.png";

const Home = () => {
  const { t } = useTranslation("home");
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [openFaqIndex, setOpenFaqIndex] = useState(null);

  const features = [
    {
      icon: Gamepad2,
      title: t("home.features.games.title"),
      description: t("home.features.games.description")
    },
    {
      icon: Zap,
      title: t("home.features.adaptive.title"),
      description: t("home.features.adaptive.description")
    },
    {
      icon: BarChart3,
      title: t("home.features.stats.title"),
      description: t("home.features.stats.description")
    },
    {
      icon: Trophy,
      title: t("home.features.leaderboard.title"),
      description: t("home.features.leaderboard.description")
    },
    {
      icon: BookOpen,
      title: t("home.features.resources.title"),
      description: t("home.features.resources.description")
    },
    {
      icon: Users,
      title: t("home.features.community.title"),
      description: t("home.features.community.description")
    }
  ];

  const faqs = [
    {
      question: t("home.faq.q1.question"),
      answer: t("home.faq.q1.answer")
    },
    {
      question: t("home.faq.q2.question"),
      answer: t("home.faq.q2.answer")
    },
    {
      question: t("home.faq.q3.question"),
      answer: t("home.faq.q3.answer")
    },
    {
      question: t("home.faq.q4.question"),
      answer: t("home.faq.q4.answer")
    },
    {
      question: t("home.faq.q5.question"),
      answer: t("home.faq.q5.answer")
    }
  ];

  return (
    <>
      {/* Hero Section */}
      <section className="relative pt-32 pb-24 min-h-screen flex items-center overflow-hidden bg-background">
        <div className="max-w-5xl mx-auto px-6 grid grid-cols-1 gap-16 items-center">
          
          {/* Centered Text */}
          <motion.div
            initial={{ opacity: 0, y: -30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="text-center z-10"
          >
            <h1 className="text-5xl sm:text-6xl md:text-7xl font-black leading-[0.9] tracking-tighter text-foreground">
              {t("home.hero.title")} <br />
              <span className="text-primary">
                {t("home.hero.highlight")}
              </span>
            </h1>

            <p className="mt-6 text-lg sm:text-xl text-muted-foreground max-w-2xl mx-auto leading-relaxed font-medium">
              {t("home.hero.description")}
            </p>

            <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
              <Button
                size="lg"
                onClick={() => setIsAuthModalOpen(true)}
                className="h-14 px-10 text-lg rounded-full shadow-xl shadow-primary/20 hover:shadow-primary/30 transition-all"
              >
                {t("home.hero.cta")}
                <ArrowRight className="ml-2 w-5 h-5" />
              </Button>

              <div className="flex items-center gap-2">
                <div className="flex -space-x-2">
                  {[1, 3, 4, 10].map((i) => (
                    <img
                      key={i}
                      src={`https://i.pravatar.cc/100?img=${i}`}
                      alt="User"
                      className="w-7 h-7 rounded-full border-2 border-background object-cover shadow-sm"
                    />
                  ))}
                </div>
                <div className="flex flex-col justify-center">
                  <p className="text-xs font-bold leading-none text-primary">+500</p>
                  <p className="text-[9px] uppercase tracking-wider text-muted-foreground font-semibold">
                    {t("home.hero.members")}
                  </p>
                </div>
              </div>
            </div>
          </motion.div>

          {/* Centered Preview Image */}
          <motion.div 
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.3 }}
            className="relative"
          >
            <div className="relative rounded-2xl border border-border bg-card shadow-2xl overflow-hidden">
               <img 
                 src={tekizzPreview} 
                 alt="Tekizz Dashboard Preview" 
                 className="w-full h-auto"
               />
            </div>
          </motion.div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-24 bg-muted/30">
        <div className="max-w-6xl mx-auto px-6">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="text-center mb-16"
          >
            <h2 className="text-4xl sm:text-5xl font-black tracking-tight text-foreground">
              {t("home.features.title")}
            </h2>
            <p className="mt-4 text-lg text-muted-foreground max-w-2xl mx-auto">
              {t("home.features.subtitle")}
            </p>
          </motion.div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((feature, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                className="group p-8 rounded-2xl border border-border bg-background hover:border-primary/30 transition-colors"
              >
                <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center mb-5 group-hover:bg-primary/20 transition-colors">
                  <feature.icon className="w-6 h-6 text-primary" />
                </div>
                <h3 className="text-xl font-bold text-foreground mb-3">
                  {feature.title}
                </h3>
                <p className="text-muted-foreground leading-relaxed">
                  {feature.description}
                </p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section id="faq" className="py-24 bg-background">
        <div className="max-w-3xl mx-auto px-6">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
            className="text-center mb-16"
          >
            <h2 className="text-4xl sm:text-5xl font-black tracking-tight text-foreground">
              {t("home.faq.title")}
            </h2>
            <p className="mt-4 text-lg text-muted-foreground">
              {t("home.faq.subtitle")}
            </p>
          </motion.div>

          <div className="space-y-4">
            {faqs.map((faq, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 10 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.4, delay: index * 0.05 }}
                className="border border-border rounded-xl overflow-hidden bg-background"
              >
                <button
                  onClick={() => setOpenFaqIndex(openFaqIndex === index ? null : index)}
                  className="w-full px-6 py-5 flex items-center justify-between text-left hover:bg-muted/30 transition-colors"
                >
                  <span className="font-semibold text-foreground pr-4">
                    {faq.question}
                  </span>
                  <ChevronDown 
                    className={`w-5 h-5 text-muted-foreground flex-shrink-0 transition-transform duration-300 ${
                      openFaqIndex === index ? "rotate-180" : ""
                    }`}
                  />
                </button>
                <div
                  className={`overflow-hidden transition-all duration-300 ${
                    openFaqIndex === index ? "max-h-96" : "max-h-0"
                  }`}
                >
                  <p className="px-6 pb-5 text-muted-foreground leading-relaxed">
                    {faq.answer}
                  </p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-24 bg-muted/30">
        <div className="max-w-4xl mx-auto px-6 text-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6 }}
          >
            <h2 className="text-3xl sm:text-4xl font-black tracking-tight text-foreground mb-4">
              {t("home.cta.title")}
            </h2>
            <p className="text-lg text-muted-foreground mb-8 max-w-xl mx-auto">
              {t("home.cta.description")}
            </p>
            <Button
              size="lg"
              onClick={() => setIsAuthModalOpen(true)}
              className="h-14 px-10 text-lg rounded-full shadow-xl shadow-primary/20 hover:shadow-primary/30 transition-all"
            >
              {t("home.cta.button")}
              <ArrowRight className="ml-2 w-5 h-5" />
            </Button>
          </motion.div>
        </div>
      </section>

      <AuthModal 
        isOpen={isAuthModalOpen} 
        onClose={() => setIsAuthModalOpen(false)} 
        initialMode="signup"
      />
    </>
  );
};

export default Home;

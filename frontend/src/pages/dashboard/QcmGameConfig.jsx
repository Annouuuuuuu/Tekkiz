import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { QcmGameProvider, useQcmGame } from '../../contexts/QcmGameContext';
import qcmGameService from '../../services/qcmGame.service';
import { Loader2, HelpCircle, Hash, Zap, Timer, Clock, ArrowLeft, ArrowRight, BookOpen, Check } from 'lucide-react';

const MODES = [
  {
    key: 'BLITZ',
    label: 'Blitz',
    sub: '2 min',
    icon: Zap,
    initialTime: 60,
    maxTime: 90,
    desc: 'Sprint intense. Questions enchaînées à vitesse maximale.',
  },
  {
    key: 'RUSH',
    label: 'Rush',
    sub: '5 min',
    icon: Timer,
    initialTime: 120,
    maxTime: 150,
    desc: 'Rythme soutenu. Bon équilibre entre vitesse et précision.',
  },
  {
    key: 'CLASSIC',
    label: 'Classic',
    sub: '10 min',
    icon: Clock,
    initialTime: 300,
    maxTime: 360,
    desc: 'Session longue. Conçu pour atteindre les niveaux EXPERT.',
  },
];

export const GAME_MODES = Object.fromEntries(
  MODES.map(m => [m.key, { initialTime: m.initialTime, maxTime: m.maxTime, label: m.label, description: m.sub, icon: m.icon }])
);

const STEPS = ['Category', 'Tags', 'Mode', 'Options'];

function StepIndicator({ step, total, labels }) {
  return (
    <div className="flex items-center gap-2">
      {labels.map((label, i) => {
        const idx = i + 1;
        const done = idx < step;
        const active = idx === step;
        return (
          <div key={label} className="flex items-center gap-2">
            <div className={`flex items-center justify-center w-6 h-6 rounded-full text-[10px] font-black transition-colors ${
              done ? 'bg-primary text-primary-foreground' :
              active ? 'border-2 border-primary text-primary' :
              'border border-border text-muted-foreground'
            }`}>
              {done ? <Check className="h-3 w-3" /> : idx}
            </div>
            <span className={`text-xs font-semibold hidden sm:block ${active ? 'text-foreground' : 'text-muted-foreground'}`}>
              {label}
            </span>
            {i < labels.length - 1 && (
              <div className={`w-6 h-px mx-1 ${done ? 'bg-primary' : 'bg-border'}`} />
            )}
          </div>
        );
      })}
    </div>
  );
}

function QcmGameConfigContent() {
  const navigate = useNavigate();
  const { setSession, setConfig, setLoading, setError } = useQcmGame();

  const [step, setStep] = useState(1);
  const [categories, setCategories] = useState([]);
  const [availableTags, setAvailableTags] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedTags, setSelectedTags] = useState([]);
  const [gameMode, setGameMode] = useState(null);
  const [showHints, setShowHints] = useState(true);
  const [showExplanations, setShowExplanations] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [tagsLoading, setTagsLoading] = useState(false);

  // Steps: 1=category, 2=tags (skip if none), 3=mode, 4=options
  const steps = selectedCategory?.hasTags !== false && availableTags.length > 0
    ? STEPS
    : ['Category', 'Mode', 'Options'];

  useEffect(() => {
    setIsLoading(true);
    qcmGameService.getCategories()
      .then(r => setCategories(r.data || r))
      .catch(() => setError('Failed to load categories'))
      .finally(() => setIsLoading(false));
  }, []);

  const handleSelectCategory = async (cat) => {
    setSelectedCategory(cat);
    setSelectedTags([]);
    setTagsLoading(true);
    try {
      const r = await qcmGameService.getTagsByCategory(cat.id);
      const tags = r.data || r;
      setAvailableTags(tags);
      // If no tags, skip tags step
      setStep(tags.length > 0 ? 2 : 3);
    } catch {
      setAvailableTags([]);
      setStep(3);
    } finally {
      setTagsLoading(false);
    }
  };

  const handleSelectMode = (key) => {
    setGameMode(key);
    setStep(4);
  };

  const toggleTag = (id) =>
    setSelectedTags(prev => prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]);

  const handleSubmit = async () => {
    if (!selectedCategory || !gameMode) return;
    setIsSubmitting(true);
    setLoading(true);
    try {
      const config = {
        categoryId: selectedCategory.id,
        tagIds: selectedTags.length > 0 ? selectedTags : undefined,
        gameMode,
        showHints,
        showExplanations,
      };
      // Persist config for QcmGamePlay (separate provider instance)
      sessionStorage.setItem('qcmConfig', JSON.stringify({ showHints, showExplanations }));
      const response = await qcmGameService.createGameSession(config);
      setSession(response);
      setConfig(config);
      navigate(`/dashboard/play/qcm/${response.sessionId}`);
    } catch (error) {
      setError(error.message || 'Failed to create game session');
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  };

  const goBack = () => {
    if (step === 1) navigate('/dashboard/play');
    else if (step === 2) { setStep(1); }
    else if (step === 3) { setStep(availableTags.length > 0 ? 2 : 1); }
    else if (step === 4) { setStep(3); }
  };

  const visibleSteps = availableTags.length > 0 ? STEPS : ['Category', 'Mode', 'Options'];
  const visibleStep = availableTags.length > 0 ? step : (step >= 3 ? step - 1 : step);

  return (
    <div className="min-h-screen bg-background px-6 py-10">
      <div className="max-w-2xl mx-auto space-y-8">

        {/* Back + Step indicator */}
        <div className="flex items-center justify-between">
          <button onClick={goBack}
            className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground transition-colors">
            <ArrowLeft className="h-4 w-4" />
            {step === 1 ? 'Back' : 'Previous'}
          </button>
          <StepIndicator step={visibleStep} total={visibleSteps.length} labels={visibleSteps} />
        </div>

        {/* Header */}
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-primary/30 bg-primary/15">
            <HelpCircle className="h-5 w-5 text-primary" />
          </div>
          <div>
            <h1 className="text-3xl font-black leading-none">QCM</h1>
            <p className="text-xs text-muted-foreground mt-0.5">Multiple choice · ranked</p>
          </div>
        </div>

        <AnimatePresence mode="wait">

          {/* ── Step 1: Category ── */}
          {step === 1 && (
            <motion.div key="step1" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }}
              transition={{ duration: 0.2 }} className="space-y-4">
              <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">Choose a category</p>
              {isLoading ? (
                <div className="flex items-center justify-center py-16">
                  <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                </div>
              ) : (
                <div className="space-y-3">
                  {categories.map((cat) => (
                    <div
                      key={cat.id}
                      className="group relative overflow-hidden rounded-2xl border border-border bg-card p-5 cursor-pointer hover:bg-muted/20 transition-all"
                      onClick={() => handleSelectCategory(cat)}
                    >
                      <div className="flex items-center gap-4">
                        <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-border bg-muted/20 shrink-0">
                          <BookOpen className="h-5 w-5 text-muted-foreground" />
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-base font-black">{cat.name}</p>
                          {cat.description && (
                            <p className="text-xs text-muted-foreground mt-0.5 truncate">{cat.description}</p>
                          )}
                        </div>
                        {tagsLoading && selectedCategory?.id === cat.id
                          ? <Loader2 className="h-4 w-4 animate-spin text-muted-foreground shrink-0" />
                          : <ArrowRight className="h-5 w-5 text-muted-foreground opacity-0 group-hover:opacity-60 transition-opacity shrink-0" />
                        }
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </motion.div>
          )}

          {/* ── Step 2: Tags ── */}
          {step === 2 && (
            <motion.div key="step2" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }}
              transition={{ duration: 0.2 }} className="space-y-4">
              <div>
                <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">Filter by tags</p>
                <p className="text-xs text-muted-foreground mt-1">Optional — leave empty to include all topics in <span className="font-semibold text-foreground">{selectedCategory?.name}</span>.</p>
              </div>
              <div className="flex flex-wrap gap-2">
                {availableTags.map(tag => {
                  const sel = selectedTags.includes(tag.id);
                  return (
                    <button key={tag.id} type="button" onClick={() => toggleTag(tag.id)}
                      className={`px-3 py-1.5 rounded-full border text-xs font-semibold transition-all ${
                        sel
                          ? 'border-primary bg-primary/10 text-primary'
                          : 'border-border text-muted-foreground hover:border-primary/40'
                      }`}>
                      {tag.name}
                    </button>
                  );
                })}
              </div>
              <button onClick={() => setStep(3)}
                className="w-full flex items-center justify-center gap-2 py-3.5 rounded-2xl bg-primary text-primary-foreground text-sm font-black hover:bg-primary/90 transition-colors">
                {selectedTags.length > 0
                  ? `Continue with ${selectedTags.length} tag${selectedTags.length > 1 ? 's' : ''}`
                  : 'Continue (all topics)'
                }
                <ArrowRight className="h-4 w-4" />
              </button>
            </motion.div>
          )}

          {/* ── Step 3: Mode ── */}
          {step === 3 && (
            <motion.div key="step3" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }}
              transition={{ duration: 0.2 }} className="space-y-4">
              <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">Select mode</p>
              <div className="space-y-3">
                {MODES.map((mode) => {
                  const Icon = mode.icon;
                  return (
                    <div
                      key={mode.key}
                      className="group relative overflow-hidden rounded-2xl border border-border bg-card p-5 cursor-pointer hover:bg-muted/20 transition-all"
                      onClick={() => handleSelectMode(mode.key)}
                    >
                      <div className="flex items-center gap-5">
                        <div className="flex h-12 w-12 items-center justify-center rounded-xl border border-border bg-muted/20 shrink-0">
                          <Icon className="h-6 w-6 text-muted-foreground" />
                        </div>
                        <div className="flex-1">
                          <div className="flex items-center gap-2">
                            <h3 className="text-base font-black">{mode.label}</h3>
                            <span className="text-xs text-muted-foreground">{mode.sub}</span>
                          </div>
                          <p className="text-xs text-muted-foreground mt-0.5 leading-relaxed">{mode.desc}</p>
                        </div>
                        <ArrowRight className="h-5 w-5 text-muted-foreground opacity-0 group-hover:opacity-60 transition-opacity shrink-0" />
                      </div>
                    </div>
                  );
                })}
              </div>
            </motion.div>
          )}

          {/* ── Step 4: Options ── */}
          {step === 4 && (
            <motion.div key="step4" initial={{ opacity: 0, x: 20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -20 }}
              transition={{ duration: 0.2 }} className="space-y-6">
              <div>
                <p className="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">Game options</p>
                <p className="text-xs text-muted-foreground mt-1">
                  {selectedCategory?.name} · {MODES.find(m => m.key === gameMode)?.label} · {MODES.find(m => m.key === gameMode)?.sub}
                </p>
              </div>

              <div className="rounded-2xl border border-border bg-card divide-y divide-border">
                {[
                  { id: 'hints', label: 'Show hints', sub: 'Reveal a hint before answering', value: showHints, set: setShowHints },
                  { id: 'expl', label: 'Show explanations', sub: 'Display explanation after each answer', value: showExplanations, set: setShowExplanations },
                ].map(opt => (
                  <div key={opt.id} className="flex items-center justify-between gap-4 p-5">
                    <div>
                      <p className="text-sm font-semibold">{opt.label}</p>
                      <p className="text-xs text-muted-foreground mt-0.5">{opt.sub}</p>
                    </div>
                    <button type="button" onClick={() => opt.set(!opt.value)}
                      className={`relative w-11 h-6 rounded-full transition-colors shrink-0 ${opt.value ? 'bg-primary' : 'bg-border'}`}>
                      <span className={`absolute top-1 w-4 h-4 rounded-full bg-white shadow transition-transform ${opt.value ? 'translate-x-6' : 'translate-x-1'}`} />
                    </button>
                  </div>
                ))}
              </div>

              <button
                onClick={handleSubmit}
                disabled={isSubmitting}
                className="w-full flex items-center justify-center gap-2 py-4 rounded-2xl bg-primary text-primary-foreground text-sm font-black hover:bg-primary/90 transition-colors disabled:opacity-40"
              >
                {isSubmitting
                  ? <><Loader2 className="h-4 w-4 animate-spin" /> Creating session…</>
                  : <><ArrowRight className="h-4 w-4" /> Start game</>
                }
              </button>
            </motion.div>
          )}

        </AnimatePresence>
      </div>
    </div>
  );
}

export default function QcmGameConfig() {
  return (
    <QcmGameProvider>
      <QcmGameConfigContent />
    </QcmGameProvider>
  );
}

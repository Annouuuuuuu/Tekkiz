import { useState, useEffect, useCallback, useRef, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { QcmGameProvider, useQcmGame } from '../../contexts/QcmGameContext';
import qcmGameService from '../../services/qcmGame.service';
import {
  Heart, Clock, Trophy, Loader2, Lightbulb,
  CheckCircle2, XCircle, ArrowRight, AlertTriangle, Zap,
} from 'lucide-react';

function fmt(s) {
  return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
}

const LABELS = ['A', 'B', 'C', 'D', 'E', 'F'];

const DIFF_STYLE = {
  EASY:   'text-emerald-400 border-emerald-500/30 bg-emerald-500/10',
  MEDIUM: 'text-yellow-400 border-yellow-500/30 bg-yellow-500/10',
  HARD:   'text-orange-400 border-orange-500/30 bg-orange-500/10',
  EXPERT: 'text-red-400 border-red-500/30 bg-red-500/10',
};

function TimerBar({ remaining, max }) {
  const pct = max > 0 ? (remaining / max) * 100 : 100;
  const urgent = remaining <= 20;
  const warn = remaining <= 60 && remaining > 20;
  const color = urgent ? 'bg-red-500' : warn ? 'bg-yellow-400' : 'bg-primary';

  return (
    <div className="flex items-center gap-3">
      <Clock className={`h-4 w-4 shrink-0 ${urgent ? 'text-red-400 animate-pulse' : 'text-muted-foreground'}`} />
      <div className="relative flex-1 h-1.5 bg-white/8 rounded-full overflow-hidden">
        <motion.div className={`absolute inset-y-0 left-0 rounded-full ${color}`}
          style={{ width: `${pct}%` }} transition={{ duration: 0.6 }} />
      </div>
      <span className={`font-mono text-sm font-black w-10 text-right tabular-nums ${urgent ? 'text-red-400' : ''}`}>
        {fmt(remaining)}
      </span>
    </div>
  );
}

function Hearts({ count }) {
  return (
    <div className="flex items-center gap-0.5">
      {Array.from({ length: Math.max(count, 0) }).map((_, i) => (
        <motion.div key={i} initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ delay: i * 0.06 }}>
          <Heart className="h-5 w-5 fill-red-500 text-red-500" />
        </motion.div>
      ))}
      {count === 0 && <span className="text-xs text-red-400 font-semibold">No lives</span>}
    </div>
  );
}

function AnswerBtn({ answer, index, selected, correct, revealed, onClick, disabled }) {
  let ring = 'border-white/10 bg-white/4';
  let labelRing = 'bg-white/10 text-foreground';

  if (revealed) {
    if (correct) {
      ring = 'border-emerald-500 bg-emerald-500/12 shadow-lg shadow-emerald-500/10';
      labelRing = 'bg-emerald-500/30 text-emerald-300';
    } else if (selected && !correct) {
      ring = 'border-red-500 bg-red-500/12';
      labelRing = 'bg-red-500/30 text-red-300';
    }
  } else if (selected) {
    ring = 'border-primary bg-primary/12 shadow-lg shadow-primary/15';
    labelRing = 'bg-primary/30 text-primary';
  }

  return (
    <motion.button
      onClick={onClick}
      disabled={disabled}
      whileHover={!disabled && !revealed ? { scale: 1.015 } : {}}
      whileTap={!disabled && !revealed ? { scale: 0.985 } : {}}
      className={`w-full p-4 text-left rounded-2xl border-2 transition-all duration-150 ${ring} ${
        !disabled && !revealed ? 'cursor-pointer hover:border-primary/50' : 'cursor-default'
      }`}
    >
      <div className="flex items-center gap-4">
        <span className={`shrink-0 flex items-center justify-center w-9 h-9 rounded-xl text-sm font-black transition-colors ${labelRing}`}>
          {LABELS[index]}
        </span>
        <span className="flex-1 text-sm leading-snug font-medium">{answer.content}</span>
        {revealed && correct && <CheckCircle2 className="h-5 w-5 text-emerald-400 shrink-0" />}
        {revealed && selected && !correct && <XCircle className="h-5 w-5 text-red-400 shrink-0" />}
      </div>
    </motion.button>
  );
}

function QcmGamePlayContent() {
  const { sessionId } = useParams();
  const navigate = useNavigate();

  const {
    currentQuestion, questionIndex, score, livesRemaining,
    isLoading, isGameOver, lastAnswerResult, config,
    globalTimerDuration, maxTimerDuration,
    setSession, setQuestion, updateGameState, setLoading, setError, gameOver,
  } = useQcmGame();

  // Config comes from sessionStorage (separate QcmGameProvider instance per page)
  const savedConfig = useMemo(() => {
    try { return JSON.parse(sessionStorage.getItem('qcmConfig') || '{}'); } catch { return {}; }
  }, []);
  const hintsEnabled = savedConfig.showHints !== false;
  const explanationsEnabled = savedConfig.showExplanations !== false;

  const [selectedId, setSelectedId] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [showHint, setShowHint] = useState(false);
  const [timeLeft, setTimeLeft] = useState(0);
  const [timerOn, setTimerOn] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const timerRef = useRef(null);

  useEffect(() => {
    if (!sessionId || loaded) return;
    setLoading(true);
    qcmGameService.getSessionState(sessionId)
      .then((data) => { setSession(data); setLoaded(true); })
      .catch((e) => { setError(e.message); navigate('/dashboard/play'); })
      .finally(() => setLoading(false));
  }, [sessionId, loaded]);

  useEffect(() => {
    if (globalTimerDuration && !timerOn) {
      setTimeLeft(globalTimerDuration);
      setTimerOn(true);
    }
  }, [globalTimerDuration, timerOn]);

  useEffect(() => {
    if (lastAnswerResult?.timeAdjustment && maxTimerDuration) {
      setTimeLeft((p) => Math.min(Math.max(p + lastAnswerResult.timeAdjustment, 0), maxTimerDuration));
    }
  }, [lastAnswerResult?.timeAdjustment]);

  useEffect(() => {
    if (timerOn && timeLeft > 0 && !isGameOver) {
      timerRef.current = setInterval(() => {
        setTimeLeft((p) => {
          if (p <= 1) { clearInterval(timerRef.current); onTimerEnd(); return 0; }
          return p - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timerRef.current);
  }, [timerOn, isGameOver]);

  const onTimerEnd = useCallback(async () => {
    try { await qcmGameService.abandonGameSession(sessionId); } catch (_) {}
    navigate(`/dashboard/play/qcm/${sessionId}/results`);
  }, [sessionId, navigate]);

  const fetchNext = useCallback(async () => {
    if (!sessionId) return;
    setLoading(true);
    setSelectedId(null);
    setShowHint(false);
    try {
      const res = await qcmGameService.getNextQuestion(sessionId);
      setQuestion(res);
    } catch (e) {
      if (e.message?.includes('completed') || e.message?.includes('Game over')) gameOver();
      else setError(e.message);
    } finally { setLoading(false); }
  }, [sessionId, setQuestion, setLoading, setError, gameOver]);

  useEffect(() => {
    if (loaded && sessionId && !currentQuestion) fetchNext();
  }, [loaded, sessionId, currentQuestion, fetchNext]);


  const submit = async (id = selectedId) => {
    if (!id || submitting || !currentQuestion) return;
    setSubmitting(true);
    try {
      const res = await qcmGameService.submitAnswer(sessionId, {
        questionId: currentQuestion.questionId,
        selectedAnswerId: id,
        timeTakenSeconds: 0,
        usedHint: showHint,
      });
      updateGameState(res);
      if (res.isGameOver) setTimeout(() => navigate(`/dashboard/play/qcm/${sessionId}/results`), 2000);
    } catch (e) { setError(e.message); }
    finally { setSubmitting(false); }
  };

  const abandon = async () => {
    if (!window.confirm("Êtes-vous sûr de vouloir abandonner cette partie ?")) return;
    try { await qcmGameService.abandonGameSession(sessionId); } catch (_) {}
    navigate('/dashboard/play');
  };

  /* ─── Loading screen ─── */
  if ((isLoading && !currentQuestion) || !loaded) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-background">
        <div className="text-center space-y-4">
          <div className="relative w-16 h-16 mx-auto">
            <Loader2 className="h-16 w-16 animate-spin text-primary/30" />
            <Zap className="h-6 w-6 text-primary absolute inset-0 m-auto" />
          </div>
          <p className="text-sm text-muted-foreground">Chargement de la question...</p>
        </div>
      </div>
    );
  }

  /* ─── Game over screen ─── */
  if (isGameOver) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-background px-4">
        <motion.div initial={{ scale: 0.85, opacity: 0 }} animate={{ scale: 1, opacity: 1 }}
          className="text-center max-w-sm w-full space-y-8">
          <div className="relative w-28 h-28 mx-auto">
            <div className="absolute inset-0 bg-red-500/15 rounded-full animate-ping" />
            <div className="relative flex items-center justify-center w-28 h-28 bg-red-500/10 border-2 border-red-500/30 rounded-full">
              <AlertTriangle className="h-12 w-12 text-red-400" />
            </div>
          </div>
          <div>
            <h2 className="text-4xl font-black">Fin du jeu !</h2>
            <p className="text-muted-foreground mt-2 text-sm">Vous n'avez plus de vies.</p>
          </div>
          <div className="flex justify-center gap-10">
            <div className="text-center">
              <p className="text-4xl font-black text-primary tabular-nums">{score}</p>
              <p className="text-[11px] uppercase tracking-widest text-muted-foreground mt-1">Points</p>
            </div>
            <div className="w-px bg-border" />
            <div className="text-center">
              <p className="text-4xl font-black tabular-nums">{questionIndex - 1}</p>
              <p className="text-[11px] uppercase tracking-widest text-muted-foreground mt-1">Correct</p>
            </div>
          </div>
          <button onClick={() => navigate(`/dashboard/play/qcm/${sessionId}/results`)}
            className="w-full py-4 rounded-2xl bg-primary text-primary-foreground font-black text-sm hover:bg-primary/90 transition-colors">
            Voir les résultats
          </button>
        </motion.div>
      </div>
    );
  }

  const diff = currentQuestion?.difficultyLevel;

  return (
    <div className="min-h-screen bg-background flex flex-col">

      {/* ─── HUD ─── */}
      <div className="sticky top-0 z-20 bg-background/90 backdrop-blur-xl border-b border-white/6">
        <div className="max-w-2xl mx-auto px-5 py-3 space-y-2.5">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Hearts count={livesRemaining} />
              <span className="text-xs font-black font-mono text-muted-foreground border border-white/10 rounded-lg px-2 py-0.5">
                Q{questionIndex}
              </span>
              {diff && (
                <span className={`text-[11px] font-black border rounded-lg px-2 py-0.5 ${DIFF_STYLE[diff] || 'text-muted-foreground border-white/10'}`}>
                  {diff}
                </span>
              )}
            </div>
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-1.5 bg-primary/12 border border-primary/20 rounded-full px-3.5 py-1.5">
                <Trophy className="h-3.5 w-3.5 text-primary" />
                <span className="font-black text-primary text-sm tabular-nums">{score}</span>
              </div>
              <button onClick={abandon}
                className="text-xs text-muted-foreground hover:text-foreground px-2 py-1 rounded-lg hover:bg-white/5 transition-colors">
                Quit
              </button>
            </div>
          </div>
          {globalTimerDuration && (
            <TimerBar remaining={timeLeft} max={maxTimerDuration || globalTimerDuration} />
          )}
        </div>
      </div>

      {/* ─── Content ─── */}
      <div className="flex-1 max-w-2xl mx-auto w-full px-5 py-8 pb-36 flex flex-col gap-5">

        {/* Question */}
        <AnimatePresence mode="wait">
          <motion.div
            key={currentQuestion?.questionId}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.22 }}
          >
            {/* Question number accent */}
            <div className="flex items-baseline gap-3 mb-5">
              <span className="text-[3.5rem] font-black leading-none text-border tabular-nums select-none">{String(questionIndex).padStart(2, '0')}</span>
              <span className="text-xs font-semibold uppercase tracking-widest text-muted-foreground pb-1">Question</span>
            </div>

            <div className="rounded-2xl border border-border bg-card p-7">
              {hintsEnabled && currentQuestion?.showHint && currentQuestion?.hint && (
                <button onClick={() => setShowHint(!showHint)}
                  className="mb-4 flex items-center gap-2 text-xs text-yellow-400 hover:text-yellow-300 transition-colors">
                  <Lightbulb className="h-3.5 w-3.5" />
                  {showHint ? 'Masquer l\'indice' : 'Indice'}
                </button>
              )}

              <AnimatePresence>
                {showHint && currentQuestion?.hint && (
                  <motion.div initial={{ height: 0, opacity: 0 }} animate={{ height: 'auto', opacity: 1 }}
                    exit={{ height: 0, opacity: 0 }} className="overflow-hidden mb-5">
                    <div className="p-4 rounded-xl bg-yellow-500/10 border border-yellow-500/20 text-sm text-yellow-300 leading-relaxed">
                      {currentQuestion.hint}
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>

              <p className="text-xl font-semibold leading-relaxed">
                {currentQuestion?.content}
              </p>
            </div>
          </motion.div>
        </AnimatePresence>

        {/* Answers */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {currentQuestion?.answers?.map((ans, i) => (
            <AnswerBtn
              key={ans.answerId}
              answer={ans}
              index={i}
              selected={selectedId === ans.answerId}
              correct={lastAnswerResult?.correctAnswerId === ans.answerId}
              revealed={!!lastAnswerResult}
              onClick={() => !lastAnswerResult && setSelectedId(ans.answerId)}
              disabled={!!lastAnswerResult || submitting}
            />
          ))}
        </div>

      </div>

      {/* ─── Fixed bottom action panel ─── */}
      <div className="fixed bottom-0 left-0 right-0 lg:left-[240px] z-30 bg-background/95 backdrop-blur-xl border-t border-white/8">
        <div className="max-w-2xl mx-auto px-5 py-4">
          <AnimatePresence mode="wait">
            {!lastAnswerResult ? (
              <motion.button
                key="submit"
                initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 8 }}
                onClick={() => submit()}
                disabled={!selectedId || submitting}
                className="w-full h-14 rounded-2xl bg-primary text-primary-foreground font-black text-sm shadow-lg shadow-primary/20 hover:bg-primary/90 transition-all disabled:opacity-30 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                {submitting
                  ? <Loader2 className="h-5 w-5 animate-spin" />
                  : <><CheckCircle2 className="h-5 w-5" />Valider la réponse</>
                }
              </motion.button>
            ) : (
              <motion.div
                key="feedback"
                initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: 8 }}
                className={`rounded-2xl border px-4 py-3 flex items-center gap-3 ${
                  lastAnswerResult.isCorrect
                    ? 'border-emerald-500/30 bg-emerald-500/8'
                    : 'border-red-500/30 bg-red-500/8'
                }`}
              >
                {lastAnswerResult.isCorrect
                  ? <CheckCircle2 className="h-5 w-5 text-emerald-400 shrink-0" />
                  : <XCircle className="h-5 w-5 text-red-400 shrink-0" />
                }
                <div className="flex-1 min-w-0">
                  <p className={`text-sm font-black ${lastAnswerResult.isCorrect ? 'text-emerald-400' : 'text-red-400'}`}>
                    {lastAnswerResult.isCorrect ? 'Bonne réponse !' : 'Mauvaise réponse'}
                  </p>
                  {!lastAnswerResult.isCorrect && (
                    <p className="text-xs text-muted-foreground truncate">
                      La bonne réponse était : <span className="text-emerald-400 font-semibold">{lastAnswerResult.correctAnswerContent}</span>
                    </p>
                  )}
                  {explanationsEnabled && lastAnswerResult.explanation && (
                    <p className="text-xs text-muted-foreground mt-0.5 line-clamp-2 leading-relaxed">{lastAnswerResult.explanation}</p>
                  )}
                </div>
                <div className="flex items-center gap-2 shrink-0">
                  {lastAnswerResult.timeAdjustment && (
                    <span className={`text-xs font-black tabular-nums ${lastAnswerResult.timeAdjustment > 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                      {lastAnswerResult.timeAdjustment > 0 ? `+${lastAnswerResult.timeAdjustment}s` : `${lastAnswerResult.timeAdjustment}s`}
                    </span>
                  )}
                  {lastAnswerResult.hasNextQuestion && (
                    <button
                      onClick={fetchNext}
                      className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-primary text-primary-foreground text-sm font-black hover:bg-primary/90 transition-colors"
                    >
                      Question suivante <ArrowRight className="h-4 w-4" />
                    </button>
                  )}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>

    </div>
  );
}

export default function QcmGamePlay() {
  return (
    <QcmGameProvider>
      <QcmGamePlayContent />
    </QcmGameProvider>
  );
}

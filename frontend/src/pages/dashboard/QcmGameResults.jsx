/**
 * QCM Game Results Screen
 *
 * Displays relevant and precise game results including:
 * - Final score
 * - Accuracy and answer counts
 * - Duration played
 * - Difficulty reached
 * - Key insights
 */

import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import qcmGameService from '../../services/qcmGame.service';
import {
  Trophy,
  Target,
  Clock,
  TrendingUp,
  CheckCircle2,
  XCircle,
  Lightbulb,
  Loader2,
  Play,
  Home,
  ChevronDown,
  ChevronUp,
  Zap,
} from 'lucide-react';

// Difficulty badge text colors
const DIFFICULTY_COLORS = {
  EASY: 'text-emerald-400',
  MEDIUM: 'text-yellow-400',
  HARD: 'text-orange-400',
  EXPERT: 'text-red-400',
};

// Game mode display labels
const GAME_MODE_CONFIG = {
  BLITZ: { label: 'Blitz', color: 'text-violet-400' },
  RUSH: { label: 'Rush', color: 'text-blue-400' },
  CLASSIC: { label: 'Classic', color: 'text-emerald-400' },
};

// Question review card component
function QuestionReviewCard({ review, index }) {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div
      className={`rounded-xl bg-card p-4 border-l-2 ${
        review.wasCorrect ? 'border-l-emerald-500' : 'border-l-red-500/60'
      }`}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-start gap-3 flex-1 min-w-0">
          <span className="flex items-center justify-center w-7 h-7 rounded-full bg-muted font-medium text-sm shrink-0">
            {index}
          </span>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-2 mb-1">
              <span
                className={`text-xs font-bold ${
                  DIFFICULTY_COLORS[review.difficultyLevel] || 'text-muted-foreground'
                }`}
              >
                {review.difficultyLevel}
              </span>
              {review.wasCorrect ? (
                <CheckCircle2 className="h-4 w-4 text-emerald-400 shrink-0" />
              ) : (
                <XCircle className="h-4 w-4 text-red-400 shrink-0" />
              )}
            </div>
            <p className="text-sm font-medium truncate">{review.questionContent}</p>
          </div>
        </div>
        <button
          type="button"
          onClick={() => setIsExpanded(!isExpanded)}
          className="text-muted-foreground hover:text-foreground transition-colors shrink-0 p-1"
        >
          {isExpanded ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
        </button>
      </div>

      {isExpanded && (
        <div className="mt-3 pl-10 space-y-2">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <p className="text-xs text-muted-foreground mb-0.5">Votre réponse</p>
              <p
                className={`text-sm font-medium ${
                  review.wasCorrect ? 'text-emerald-400' : 'text-red-400'
                }`}
              >
                {review.userAnswerContent}
              </p>
            </div>
            {!review.wasCorrect && (
              <div>
                <p className="text-xs text-muted-foreground mb-0.5">Réponse correcte :</p>
                <p className="text-sm font-medium text-emerald-400">{review.correctAnswerContent}</p>
              </div>
            )}
          </div>

          {review.explanation && (
            <div className="bg-muted/30 rounded-xl p-3 text-xs text-muted-foreground leading-relaxed">
              <Lightbulb className="h-3 w-3 inline mr-1 text-yellow-400" />
              {review.explanation}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

// Main results component
export default function QcmGameResults() {
  const { sessionId } = useParams();
  const navigate = useNavigate();

  const [results, setResults] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchResults = async () => {
      if (!sessionId) return;

      setIsLoading(true);
      try {
        const response = await qcmGameService.getGameResults(sessionId);
        setResults(response);
      } catch (error) {
        console.error('Failed to fetch results:', error);
        setError(error.message || 'Failed to load results');
      } finally {
        setIsLoading(false);
      }
    };

    fetchResults();
  }, [sessionId]);

  // Loading state
  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="text-center space-y-3">
          <Loader2 className="h-12 w-12 animate-spin mx-auto text-primary" />
          <p className="text-sm text-muted-foreground">Chargement des résultats...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="max-w-md w-full mx-auto px-6 text-center space-y-4">
          <XCircle className="h-12 w-12 text-red-400 mx-auto" />
          <h2 className="text-xl font-bold">Erreur</h2>
          <p className="text-sm text-muted-foreground">{error}</p>
          <button
            onClick={() => navigate('/dashboard/play')}
            className="px-5 py-2.5 rounded-xl bg-primary text-primary-foreground text-sm font-bold hover:bg-primary/90 transition-colors"
          >
            Retour aux jeux
          </button>
        </div>
      </div>
    );
  }

  if (!results) return null;

  const {
    categoryName,
    gameMode,
    totalScore,
    correctAnswers,
    wrongAnswers,
    totalQuestionsAnswered,
    accuracy,
    efficiency,
    durationSeconds,
    startingDifficulty,
    endingDifficulty,
    maxDifficultyReached,
    questionReviews,
    endReason,
  } = results;

  // Format duration
  const formatDuration = (seconds) => {
    if (!seconds) return '0s';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return mins > 0 ? `${mins}m ${secs}s` : `${secs}s`;
  };

  // Generate insight based on performance
  const getInsight = () => {
    if (accuracy >= 90) {
      return { icon: Trophy, text: "Excellent ! Vous avez maîtrisé cette catégorie !", color: 'text-yellow-400' };
    } else if (accuracy >= 75) {
      return { icon: TrendingUp, text: "Belle performance ! Continuez à vous entraîner.", color: 'text-primary' };
    } else if (accuracy >= 50) {
      return { icon: Target, text: "Bon effort ! Révisez les questions manquées.", color: 'text-yellow-400' };
    } else {
      return { icon: Lightbulb, text: "Continuez à vous entraîner ! Concentrez-vous sur les concepts.", color: 'text-orange-400' };
    }
  };

  const insight = getInsight();
  const InsightIcon = insight.icon;
  const modeConfig = GAME_MODE_CONFIG[gameMode];
  const wrongReviews = questionReviews ? questionReviews.filter((r) => !r.wasCorrect) : [];

  return (
    <div className="max-w-3xl mx-auto px-6 py-8 space-y-8">
      {/* Header */}
      <div className="text-center space-y-3">
        <Trophy className="h-14 w-14 text-yellow-400 mx-auto" />
        <div>
          <h1 className="text-5xl font-black tabular-nums">{totalScore}</h1>
          <p className="text-sm text-muted-foreground mt-1">Points</p>
        </div>
        <p className="text-sm text-muted-foreground">{categoryName}</p>
        {gameMode && modeConfig && (
          <span className={`inline-block text-xs font-bold px-3 py-1 rounded-full border border-border bg-card ${modeConfig.color}`}>
            {modeConfig.label} Mode
          </span>
        )}
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-4 gap-3">
        {[
          { icon: Target, value: `${(accuracy || 0).toFixed(0)}%`, label: "Précision", iconClass: 'text-primary' },
          { icon: Zap, value: (efficiency || 0).toFixed(1), label: "Efficacité", iconClass: 'text-orange-400' },
          { icon: Clock, value: formatDuration(durationSeconds), label: "Durée", iconClass: 'text-purple-400' },
          { icon: CheckCircle2, value: `${correctAnswers || 0}/${totalQuestionsAnswered || 0}`, label: "Correct", iconClass: 'text-emerald-400' },
        ].map(({ icon: Icon, value, label, iconClass }) => (
          <div key={label} className="rounded-2xl border border-border bg-card p-4 text-center space-y-1.5">
            <Icon className={`h-5 w-5 mx-auto ${iconClass}`} />
            <p className="text-lg font-black tabular-nums">{value}</p>
            <p className="text-xs text-muted-foreground">{label}</p>
          </div>
        ))}
      </div>

      {/* Insight card */}
      <div className="rounded-2xl border border-border bg-card p-5">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-muted shrink-0">
            <InsightIcon className={`h-5 w-5 ${insight.color}`} />
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-semibold text-sm">{insight.text}</p>
            <div className="flex flex-wrap items-center gap-x-4 gap-y-1 mt-1.5 text-xs text-muted-foreground">
              <span className="flex items-center gap-1">
                <TrendingUp className="h-3.5 w-3.5" />
                Atteint:{' '}
                <span className={`font-bold ml-0.5 ${DIFFICULTY_COLORS[maxDifficultyReached || endingDifficulty] || ''}`}>
                  {maxDifficultyReached || endingDifficulty}
                </span>
              </span>
              <span>
                {endReason === 'LIVES_DEPLETED'
                  ? "Vies épuisées"
                  : "Temps écoulé"}
              </span>
            </div>
          </div>
        </div>

        {/* Accuracy bar */}
        <div className="mt-4 space-y-1.5">
          <div className="flex justify-between text-xs text-muted-foreground">
            <span>Précision</span>
            <span className="font-semibold text-foreground">{(accuracy || 0).toFixed(1)}%</span>
          </div>
          <div className="h-2 rounded-full bg-muted overflow-hidden">
            <div
              className="h-full rounded-full bg-primary transition-all"
              style={{ width: `${Math.min(accuracy || 0, 100)}%` }}
            />
          </div>
        </div>

        {efficiency && (
          <div className="mt-3 flex items-center gap-2 text-xs text-muted-foreground">
            <Zap className="h-3.5 w-3.5 text-orange-400" />
            <span>Score par minute :</span>
            <span className="font-semibold text-foreground">{efficiency.toFixed(1)} pts/min</span>
          </div>
        )}
      </div>

      {/* Wrong answers review */}
      {wrongReviews.length > 0 && (
        <div className="space-y-3">
          <div className="flex items-center gap-2">
            <XCircle className="h-4 w-4 text-red-400" />
            <h2 className="text-sm font-bold">
              Erreurs à revoir ({wrongReviews.length})
            </h2>
          </div>
          <div className="space-y-2 max-h-80 overflow-y-auto pr-1">
            {wrongReviews.map((review, index) => (
              <QuestionReviewCard
                key={index}
                review={review}
                index={questionReviews.indexOf(review) + 1}
              />
            ))}
          </div>
        </div>
      )}

      {/* Action buttons */}
      <div className="flex gap-3">
        <button
          onClick={() => navigate('/dashboard/play/qcm/configure')}
          className="flex-1 flex items-center justify-center gap-2 px-5 py-3 rounded-xl bg-primary text-primary-foreground text-sm font-bold hover:bg-primary/90 transition-colors"
        >
          <Play className="h-4 w-4" />
          Rejouer
        </button>
        <button
          onClick={() => navigate('/dashboard/play')}
          className="flex-1 flex items-center justify-center gap-2 px-5 py-3 rounded-xl border border-border bg-card text-sm font-bold hover:bg-muted/30 transition-colors"
        >
          <Home className="h-4 w-4" />
          Retour aux jeux
        </button>
      </div>
    </div>
  );
}

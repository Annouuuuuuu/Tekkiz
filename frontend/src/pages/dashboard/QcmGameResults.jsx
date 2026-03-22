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
import { useTranslation } from 'react-i18next';
import qcmGameService from '../../services/qcmGame.service';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Progress } from '../../components/ui/progress';
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

// Difficulty level colors
const DIFFICULTY_COLORS = {
  EASY: 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400',
  MEDIUM: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400',
  HARD: 'bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400',
  EXPERT: 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
};

// Game mode colors and labels
const GAME_MODE_CONFIG = {
  BLITZ: { color: 'text-red-500', bgColor: 'bg-red-100 dark:bg-red-900/30', label: 'Blitz' },
  RUSH: { color: 'text-yellow-500', bgColor: 'bg-yellow-100 dark:bg-yellow-900/30', label: 'Rush' },
  CLASSIC: { color: 'text-green-500', bgColor: 'bg-green-100 dark:bg-green-900/30', label: 'Classic' },
};

// Question review card component - simplified
function QuestionReviewCard({ review, index, t }) {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <Card className={`${review.wasCorrect ? 'border-l-4 border-l-green-500' : 'border-l-4 border-l-red-500'}`}>
      <CardContent className="pt-3 pb-3">
        <div className="flex items-start justify-between">
          <div className="flex items-start gap-3 flex-1">
            <span className="flex items-center justify-center w-7 h-7 rounded-full bg-muted font-medium text-sm flex-shrink-0">
              {index}
            </span>
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2 mb-1">
                <Badge variant="outline" className={`text-xs ${DIFFICULTY_COLORS[review.difficultyLevel] || ''}`}>
                  {review.difficultyLevel}
                </Badge>
                {review.wasCorrect ? (
                  <CheckCircle2 className="h-4 w-4 text-green-500" />
                ) : (
                  <XCircle className="h-4 w-4 text-red-500" />
                )}
              </div>
              <p className="font-medium text-sm truncate">{review.questionContent}</p>
            </div>
          </div>
          <Button
            variant="ghost"
            size="sm"
            onClick={() => setIsExpanded(!isExpanded)}
            className="flex-shrink-0"
          >
            {isExpanded ? <ChevronUp className="h-4 w-4" /> : <ChevronDown className="h-4 w-4" />}
          </Button>
        </div>

        {isExpanded && (
          <div className="mt-3 pl-10 space-y-2">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <p className="text-xs text-muted-foreground">{t('qcm.yourAnswer')}</p>
                <p className={`text-sm font-medium ${review.wasCorrect ? 'text-green-600' : 'text-red-600'}`}>
                  {review.userAnswerContent}
                </p>
              </div>
              {!review.wasCorrect && (
                <div>
                  <p className="text-xs text-muted-foreground">{t('qcm.correctAnswer')}</p>
                  <p className="text-sm font-medium text-green-600">{review.correctAnswerContent}</p>
                </div>
              )}
            </div>

            {review.explanation && (
              <div className="p-2 bg-blue-50 dark:bg-blue-950/30 rounded text-xs text-blue-800 dark:text-blue-200">
                <Lightbulb className="h-3 w-3 inline mr-1" />
                {review.explanation}
              </div>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  );
}

// Main results component
export default function QcmGameResults() {
  const { sessionId } = useParams();
  const { t } = useTranslation("common");
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
        <div className="text-center">
          <Loader2 className="h-12 w-12 animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">{t('qcm.loadingResults')}</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Card className="max-w-md">
          <CardContent className="pt-6 text-center">
            <XCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h2 className="text-xl font-bold mb-2">{t('qcm.error')}</h2>
            <p className="text-muted-foreground mb-4">{error}</p>
            <Button onClick={() => navigate('/dashboard/play')}>
              {t('qcm.backToPlay')}
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  // No results
  if (!results) {
    return null;
  }

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
      return { icon: Trophy, text: t('qcm.insightExcellent'), color: 'text-green-600' };
    } else if (accuracy >= 75) {
      return { icon: TrendingUp, text: t('qcm.insightGood'), color: 'text-blue-600' };
    } else if (accuracy >= 50) {
      return { icon: Target, text: t('qcm.insightAverage'), color: 'text-yellow-600' };
    } else {
      return { icon: Lightbulb, text: t('qcm.insightNeedsWork'), color: 'text-orange-600' };
    }
  };

  const insight = getInsight();
  const InsightIcon = insight.icon;

  return (
    <div className="container max-w-3xl mx-auto py-6 px-4">
      {/* Header */}
      <div className="text-center mb-6">
        <Trophy className="h-14 w-14 text-yellow-500 mx-auto mb-3" />
        <h1 className="text-2xl font-bold mb-1">{t('qcm.gameComplete')}</h1>
        <p className="text-muted-foreground text-sm">{categoryName}</p>
        {gameMode && (
          <Badge className={`mt-2 ${GAME_MODE_CONFIG[gameMode]?.bgColor || ''} ${GAME_MODE_CONFIG[gameMode]?.color || ''}`}>
            {GAME_MODE_CONFIG[gameMode]?.label || gameMode} Mode
          </Badge>
        )}
      </div>

      {/* Main Stats - Compact Grid */}
      <div className="grid grid-cols-4 gap-3 mb-6">
        <Card>
          <CardContent className="pt-4 pb-3 text-center">
            <Trophy className="h-6 w-6 text-yellow-500 mx-auto mb-1" />
            <p className="text-2xl font-bold">{totalScore}</p>
            <p className="text-xs text-muted-foreground">{t('qcm.points')}</p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-4 pb-3 text-center">
            <Target className="h-6 w-6 text-blue-500 mx-auto mb-1" />
            <p className="text-2xl font-bold">{accuracy?.toFixed(0) || 0}%</p>
            <p className="text-xs text-muted-foreground">{t('qcm.accuracy')}</p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-4 pb-3 text-center">
            <Zap className="h-6 w-6 text-orange-500 mx-auto mb-1" />
            <p className="text-2xl font-bold">{efficiency?.toFixed(1) || 0}</p>
            <p className="text-xs text-muted-foreground">{t('qcm.efficiency') || 'Efficiency'}</p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="pt-4 pb-3 text-center">
            <Clock className="h-6 w-6 text-purple-500 mx-auto mb-1" />
            <p className="text-2xl font-bold">{formatDuration(durationSeconds)}</p>
            <p className="text-xs text-muted-foreground">{t('qcm.duration')}</p>
          </CardContent>
        </Card>
      </div>

      {/* Insight Card - Key Takeaway */}
      <Card className="mb-6">
        <CardContent className="pt-4 pb-4">
          <div className="flex items-center gap-3">
            <div className={`p-2 rounded-full bg-muted ${insight.color}`}>
              <InsightIcon className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <p className="font-medium">{insight.text}</p>
              <div className="flex items-center gap-4 mt-2 text-sm text-muted-foreground">
                <span className="flex items-center gap-1">
                  <Zap className="h-4 w-4" />
                  {t('qcm.difficultyReached')}: <Badge className={DIFFICULTY_COLORS[maxDifficultyReached || endingDifficulty] || ''}>{maxDifficultyReached || endingDifficulty}</Badge>
                </span>
                <span className="flex items-center gap-1">
                  <CheckCircle2 className="h-4 w-4" />
                  {correctAnswers}/{totalQuestionsAnswered || 0} {t('qcm.correct')}
                </span>
                <span>
                  {endReason === 'LIVES_DEPLETED' 
                    ? t('qcm.endReasonLives')
                    : t('qcm.endReasonTime')}
                </span>
              </div>
            </div>
          </div>
          
          {/* Accuracy Progress Bar */}
          <div className="mt-4">
            <div className="flex justify-between text-sm mb-1">
              <span>{t('qcm.accuracy')}</span>
              <span className="font-medium">{accuracy?.toFixed(1) || 0}%</span>
            </div>
            <Progress value={accuracy || 0} className="h-2" />
          </div>
          
          {/* Efficiency indicator */}
          {efficiency && (
            <div className="mt-3 flex items-center gap-2 text-sm">
              <Zap className="h-4 w-4 text-orange-500" />
              <span className="text-muted-foreground">{t('qcm.efficiencyNote') || 'Score per minute:'}</span>
              <span className="font-medium">{efficiency.toFixed(1)} pts/min</span>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Wrong Answers Review - Only show if there are mistakes */}
      {questionReviews && questionReviews.filter(r => !r.wasCorrect).length > 0 && (
        <Card className="mb-6">
          <CardHeader className="pb-2">
            <CardTitle className="text-base flex items-center gap-2">
              <XCircle className="h-4 w-4 text-red-500" />
              {t('qcm.mistakesToReview')} ({questionReviews.filter(r => !r.wasCorrect).length})
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-2 max-h-64 overflow-y-auto">
            {questionReviews.filter(r => !r.wasCorrect).map((review, index) => (
              <QuestionReviewCard
                key={index}
                review={review}
                index={questionReviews.indexOf(review) + 1}
                t={t}
              />
            ))}
          </CardContent>
        </Card>
      )}

      {/* Action Buttons */}
      <div className="flex gap-3">
        <Button
          onClick={() => navigate('/dashboard/play/qcm/configure')}
          className="flex-1"
          size="lg"
        >
          <Play className="h-4 w-4 mr-2" />
          {t('qcm.playAgain')}
        </Button>
        <Button
          variant="outline"
          onClick={() => navigate('/dashboard/play')}
          className="flex-1"
          size="lg"
        >
          <Home className="h-4 w-4 mr-2" />
          {t('qcm.backToPlay')}
        </Button>
      </div>
    </div>
  );
}

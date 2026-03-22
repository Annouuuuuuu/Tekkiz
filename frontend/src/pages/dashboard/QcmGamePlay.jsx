/**
 * QCM Game Play Screen
 * 
 * Main game interface where users:
 * - View current question
 * - Select answers
 * - See global timer countdown
 * - Track score and lives
 * - View answer feedback
 */

import { useState, useEffect, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { QcmGameProvider, useQcmGame } from '../../contexts/QcmGameContext';
import qcmGameService from '../../services/qcmGame.service';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Progress } from '../../components/ui/progress';
import {
  Heart,
  Clock,
  Trophy,
  Loader2,
  Lightbulb,
  CheckCircle2,
  XCircle,
  ArrowRight,
  AlertTriangle,
} from 'lucide-react';

// Format time as MM:SS
function formatTime(seconds) {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs.toString().padStart(2, '0')}`;
}

// Global Timer component with max time cap
function GlobalTimer({ timeRemaining, totalTime, maxTime }) {
  const percentage = maxTime > 0 ? (timeRemaining / maxTime) * 100 : 100;
  const isLow = timeRemaining <= 30;
  const isOverInitial = timeRemaining > totalTime;

  return (
    <div className="flex items-center gap-2">
      <Clock className={`h-5 w-5 ${isLow ? 'text-destructive animate-pulse' : 'text-muted-foreground'}`} />
      <div className="flex-1">
        <Progress
          value={percentage}
          className={`h-2 ${isLow ? 'bg-destructive/20' : ''} ${isOverInitial ? 'bg-green-500/20' : ''}`}
        />
      </div>
      <span className={`font-mono font-bold ${isLow ? 'text-destructive' : ''} ${isOverInitial ? 'text-green-500' : ''}`}>
        {formatTime(timeRemaining)}
      </span>
    </div>
  );
}

// Lives display component
function LivesDisplay({ lives }) {
  return (
    <div className="flex items-center gap-1">
      {Array.from({ length: lives }).map((_, i) => (
        <Heart key={i} className="h-5 w-5 fill-destructive text-destructive" />
      ))}
      {lives === 0 && (
        <span className="text-destructive text-sm font-medium">No lives remaining</span>
      )}
    </div>
  );
}

// Answer option component
function AnswerOption({ answer, selected, correct, revealed, onClick, disabled }) {
  let borderColor = 'border-border';
  let bgColor = 'bg-card';

  if (revealed) {
    if (correct) {
      borderColor = 'border-green-500';
      bgColor = 'bg-green-50 dark:bg-green-950/30';
    } else if (selected && !correct) {
      borderColor = 'border-destructive';
      bgColor = 'bg-red-50 dark:bg-red-950/30';
    }
  } else if (selected) {
    borderColor = 'border-primary';
    bgColor = 'bg-primary/10';
  }

  return (
    <button
      onClick={onClick}
      disabled={disabled}
      className={`w-full p-4 text-left rounded-lg border-2 transition-all ${borderColor} ${bgColor} ${
        !disabled && !revealed ? 'hover:border-primary/50 hover:bg-primary/5 cursor-pointer' : 'cursor-default'
      } ${disabled ? 'opacity-50' : ''}`}
    >
      <div className="flex items-center gap-3">
        <span className="flex items-center justify-center w-8 h-8 rounded-full bg-muted font-medium">
          {String.fromCharCode(65 + answer.displayOrder - 1)}
        </span>
        <span className="flex-1">{answer.content}</span>
        {revealed && correct && (
          <CheckCircle2 className="h-5 w-5 text-green-500" />
        )}
        {revealed && selected && !correct && (
          <XCircle className="h-5 w-5 text-destructive" />
        )}
      </div>
    </button>
  );
}

// Main game content
function QcmGamePlayContent() {
  const { sessionId } = useParams();
  const { t } = useTranslation("common");
  const navigate = useNavigate();
  
  const {
    currentQuestion,
    questionIndex,
    score,
    correctAnswers,
    wrongAnswers,
    livesRemaining,
    isLoading,
    isGameOver,
    lastAnswerResult,
    sessionState,
    globalTimerDuration, // Initial time from context
    maxTimerDuration,    // Max time cap from context
    gameMode,            // Game mode from context
    setSession,
    setQuestion,
    updateGameState,
    setLoading,
    setError,
    gameOver,
    config,
  } = useQcmGame();

  const [selectedAnswerId, setSelectedAnswerId] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showHint, setShowHint] = useState(false);
  const [globalTimeRemaining, setGlobalTimeRemaining] = useState(0);
  const [timerStarted, setTimerStarted] = useState(false);
  const [sessionLoaded, setSessionLoaded] = useState(false);
  const timerRef = useRef(null);

  // Fetch session state on mount to get timer duration
  useEffect(() => {
    const fetchSessionState = async () => {
      if (!sessionId || sessionLoaded) return;
      
      try {
        setLoading(true);
        const sessionData = await qcmGameService.getSessionState(sessionId);
        setSession(sessionData);
        setSessionLoaded(true);
      } catch (error) {
        console.error('Failed to fetch session state:', error);
        setError(error.message || 'Failed to load game session');
        navigate('/dashboard/play');
      } finally {
        setLoading(false);
      }
    };

    fetchSessionState();
  }, [sessionId, sessionLoaded, setSession, setLoading, setError, navigate]);

  // Initialize global timer from context
  useEffect(() => {
    if (globalTimerDuration && !timerStarted) {
      setGlobalTimeRemaining(globalTimerDuration);
      setTimerStarted(true);
    }
  }, [globalTimerDuration, timerStarted]);

  // Apply time adjustment from answer result
  useEffect(() => {
    if (lastAnswerResult?.timeAdjustment && maxTimerDuration) {
      setGlobalTimeRemaining(prev => {
        const newTime = prev + lastAnswerResult.timeAdjustment;
        // Cap at max time
        return Math.min(Math.max(newTime, 0), maxTimerDuration);
      });
    }
  }, [lastAnswerResult?.timeAdjustment, maxTimerDuration]);

  // Global timer countdown
  useEffect(() => {
    if (timerStarted && globalTimeRemaining > 0 && !isGameOver) {
      timerRef.current = setInterval(() => {
        setGlobalTimeRemaining(prev => {
          if (prev <= 1) {
            clearInterval(timerRef.current);
            handleTimerExpired();
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }

    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, [timerStarted, isGameOver]);

  // Handle timer expiration
  const handleTimerExpired = useCallback(async () => {
    try {
      await qcmGameService.abandonGameSession(sessionId);
      navigate(`/dashboard/play/qcm/${sessionId}/results`);
    } catch (error) {
      console.error('Failed to end game:', error);
      navigate(`/dashboard/play/qcm/${sessionId}/results`);
    }
  }, [sessionId, navigate]);

  // Fetch next question
  const fetchNextQuestion = useCallback(async () => {
    if (!sessionId) return;
    
    setLoading(true);
    setSelectedAnswerId(null);
    setShowHint(false);

    try {
      const response = await qcmGameService.getNextQuestion(sessionId);
      setQuestion(response);
    } catch (error) {
      console.error('Failed to fetch question:', error);
      if (error.message?.includes('completed') || error.message?.includes('Game over')) {
        gameOver();
      } else {
        setError(error.message || 'Failed to load question');
      }
    } finally {
      setLoading(false);
    }
  }, [sessionId, setQuestion, setLoading, setError, gameOver]);

  // Load first question after session is loaded
  useEffect(() => {
    if (sessionLoaded && sessionId && !currentQuestion) {
      fetchNextQuestion();
    }
  }, [sessionLoaded, sessionId, currentQuestion, fetchNextQuestion]);

  // Handle answer submission
  const handleSubmitAnswer = async (answerId = selectedAnswerId) => {
    if (!answerId || isSubmitting || !currentQuestion) return;

    setIsSubmitting(true);

    try {
      const response = await qcmGameService.submitAnswer(sessionId, {
        questionId: currentQuestion.questionId,
        selectedAnswerId: answerId,
        timeTakenSeconds: 0,
        usedHint: showHint,
      });

      updateGameState(response);

      if (response.isGameOver) {
        setTimeout(() => {
          navigate(`/dashboard/play/qcm/${sessionId}/results`);
        }, 2000);
      }
    } catch (error) {
      console.error('Failed to submit answer:', error);
      setError(error.message || 'Failed to submit answer');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Handle next question
  const handleNextQuestion = () => {
    if (lastAnswerResult?.hasNextQuestion) {
      fetchNextQuestion();
    }
  };

  // Handle abandon game
  const handleAbandonGame = async () => {
    if (window.confirm(t('qcm.confirmAbandon'))) {
      try {
        await qcmGameService.abandonGameSession(sessionId);
        navigate('/dashboard/play');
      } catch (error) {
        console.error('Failed to abandon game:', error);
      }
    }
  };

  // Loading state - show spinner while loading session or question
  if ((isLoading && !currentQuestion) || !sessionLoaded) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="text-center">
          <Loader2 className="h-12 w-12 animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">{t('qcm.loadingQuestion')}</p>
        </div>
      </div>
    );
  }

  // Game over state
  if (isGameOver) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Card className="max-w-md w-full">
          <CardContent className="pt-6 text-center">
            <AlertTriangle className="h-16 w-16 text-destructive mx-auto mb-4" />
            <h2 className="text-2xl font-bold mb-2">{t('qcm.gameOver')}</h2>
            <p className="text-muted-foreground mb-4">
              {t('qcm.noLivesRemaining')}
            </p>
            <div className="grid grid-cols-2 gap-4 mb-6">
              <div className="text-center">
                <p className="text-2xl font-bold">{score}</p>
                <p className="text-sm text-muted-foreground">{t('qcm.points')}</p>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold">{correctAnswers}</p>
                <p className="text-sm text-muted-foreground">{t('qcm.correct')}</p>
              </div>
            </div>
            <Button onClick={() => navigate(`/dashboard/play/qcm/${sessionId}/results`)}>
              {t('qcm.viewResults')}
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="h-screen flex flex-col py-4 px-4 overflow-hidden">
      {/* Game Header - Compact */}
      <div className="flex items-center justify-between mb-4 flex-shrink-0">
        <div className="flex items-center gap-3">
          <Badge variant="outline" className="text-base px-3 py-1">
            {t('qcm.questionNumber', { number: questionIndex })}
          </Badge>
          <LivesDisplay lives={livesRemaining} />
          <Badge variant="secondary" className="text-base">
            {currentQuestion?.difficultyLevel}
          </Badge>
        </div>
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2">
            <Trophy className="h-5 w-5 text-yellow-500" />
            <span className="font-bold">{score}</span>
          </div>
          <Button variant="ghost" size="sm" onClick={handleAbandonGame}>
            {t('qcm.abandon')}
          </Button>
        </div>
      </div>

      {/* Global Timer - Compact */}
      {globalTimerDuration && (
        <div className="mb-4 flex-shrink-0">
          <GlobalTimer
            timeRemaining={globalTimeRemaining}
            totalTime={globalTimerDuration}
            maxTime={maxTimerDuration || globalTimerDuration}
          />
        </div>
      )}

      {/* Main Game Area - Side by Side Layout */}
      <div className="flex-1 flex gap-4 min-h-0">
        {/* Left Side - Question Card */}
        <div className="flex-1 flex flex-col min-w-0">
          <Card className="flex-1 flex flex-col overflow-hidden">
            <CardHeader className="flex-shrink-0 pb-2">
              <div className="flex items-center justify-between">
                {currentQuestion?.showHint && currentQuestion?.hint && (
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setShowHint(!showHint)}
                    className="text-yellow-600"
                  >
                    <Lightbulb className="h-4 w-4 mr-1" />
                    {t('qcm.hint')}
                  </Button>
                )}
              </div>
              <CardTitle className="text-lg mt-2">
                {currentQuestion?.content}
              </CardTitle>
            </CardHeader>
            <CardContent className="flex-1 overflow-y-auto">
              {/* Hint display */}
              {showHint && currentQuestion?.hint && (
                <div className="mb-3 p-2 bg-yellow-50 dark:bg-yellow-950/30 border border-yellow-200 dark:border-yellow-800 rounded-lg">
                  <p className="text-sm text-yellow-800 dark:text-yellow-200">
                    <Lightbulb className="h-4 w-4 inline mr-1" />
                    {currentQuestion.hint}
                  </p>
                </div>
              )}

              {/* Answer options */}
              <div className="space-y-2">
                {currentQuestion?.answers?.map((answer) => (
                  <AnswerOption
                    key={answer.answerId}
                    answer={answer}
                    selected={selectedAnswerId === answer.answerId}
                    correct={lastAnswerResult?.correctAnswerId === answer.answerId}
                    revealed={!!lastAnswerResult}
                    onClick={() => !lastAnswerResult && setSelectedAnswerId(answer.answerId)}
                    disabled={!!lastAnswerResult || isSubmitting}
                  />
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Right Side - Action Panel */}
        <div className="w-64 flex-shrink-0 flex flex-col gap-4">
          {/* Submit Button - Always visible on right side */}
          {!lastAnswerResult && (
            <Card className="flex-1 flex items-center justify-center">
              <CardContent className="p-4 w-full">
                <Button
                  onClick={() => handleSubmitAnswer()}
                  disabled={!selectedAnswerId || isSubmitting}
                  className="w-full h-16 text-lg"
                  size="lg"
                >
                  {isSubmitting ? (
                    <Loader2 className="h-6 w-6 animate-spin" />
                  ) : (
                    <>
                      <CheckCircle2 className="mr-2 h-5 w-5" />
                      {t('qcm.submitAnswer')}
                    </>
                  )}
                </Button>
              </CardContent>
            </Card>
          )}

          {/* Answer Feedback - On right side */}
          {lastAnswerResult && (
            <Card className={`flex-1 ${lastAnswerResult.isCorrect ? 'border-green-500' : 'border-destructive'}`}>
              <CardContent className="p-4 h-full flex flex-col justify-center">
                <div className="text-center mb-4">
                  {lastAnswerResult.isCorrect ? (
                    <CheckCircle2 className="h-12 w-12 text-green-500 mx-auto" />
                  ) : (
                    <XCircle className="h-12 w-12 text-destructive mx-auto" />
                  )}
                  <h3 className={`font-bold text-lg mt-2 ${lastAnswerResult.isCorrect ? 'text-green-700 dark:text-green-400' : 'text-destructive'}`}>
                    {lastAnswerResult.isCorrect
                      ? t('qcm.correctAnswer')
                      : t('qcm.wrongAnswer')}
                  </h3>
                </div>
                
                {!lastAnswerResult.isCorrect && (
                  <div className="text-sm mb-3 p-2 bg-muted rounded">
                    <span className="font-medium">{t('qcm.correctAnswerWas')}</span>
                    <p className="text-green-600 dark:text-green-400">{lastAnswerResult.correctAnswerContent}</p>
                  </div>
                )}

                {lastAnswerResult.explanation && (
                  <p className="text-xs text-muted-foreground mb-3">
                    {lastAnswerResult.explanation}
                  </p>
                )}

                {/* Next button */}
                {lastAnswerResult.hasNextQuestion && (
                  <Button
                    onClick={handleNextQuestion}
                    className="w-full"
                    size="lg"
                  >
                    {t('qcm.nextQuestion')}
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </Button>
                )}
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}

// Wrapper component with provider
export default function QcmGamePlay() {
  return (
    <QcmGameProvider>
      <QcmGamePlayContent />
    </QcmGameProvider>
  );
}

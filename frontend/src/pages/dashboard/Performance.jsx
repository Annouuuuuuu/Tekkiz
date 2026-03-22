import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { TrendingUp, Trophy, Target, Brain, Zap, Loader2, Flame, BarChart3, HelpCircle } from "lucide-react";
import StatsCard from "@/components/dashboard/Performance/StatsCard";
import GlobalScoreChart from "@/components/dashboard/Performance/GlobalScoreChart";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Separator } from "@/components/ui/separator";
import qcmGameService from "@/services/qcmGame.service";

const PerformancePage = () => {
  const { t } = useTranslation("common");
  const [activeTab, setActiveTab] = useState("qcm");
  const [stats, setStats] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchStats = async () => {
      setIsLoading(true);
      try {
        const response = await qcmGameService.getUserStats();
        setStats(response.data || response);
      } catch (err) {
        console.error("Failed to fetch QCM stats:", err);
        setError(err.message || "Failed to load statistics");
      } finally {
        setIsLoading(false);
      }
    };

    if (activeTab === "qcm") {
      fetchStats();
    }
  }, [activeTab]);

  // Difficulty colors
  const difficultyColors = {
    EASY: "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400",
    MEDIUM: "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400",
    HARD: "bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400",
    EXPERT: "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400",
  };

  // Loading state
  const renderLoading = () => (
    <div className="flex items-center justify-center py-12">
      <div className="text-center">
        <Loader2 className="h-8 w-8 animate-spin mx-auto mb-3" />
        <p className="text-muted-foreground">{t("performance.loading")}</p>
      </div>
    </div>
  );

  // Error state
  const renderError = () => (
    <div className="text-center py-12">
      <p className="text-destructive">{error}</p>
    </div>
  );

  // No data state
  const renderNoData = () => (
    <div className="text-center py-12">
      <Target className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
      <h3 className="text-lg font-semibold mb-2">{t("performance.noData")}</h3>
      <p className="text-muted-foreground">
        {t("performance.noDataDescription")}
      </p>
    </div>
  );

  // QCM Performance Content
  const renderQCMContent = () => {
    if (isLoading) return renderLoading();
    if (error) return renderError();
    if (!stats || stats.totalGamesPlayed === 0) return renderNoData();

    return (
      <div className="space-y-6">
        {/* Primary KPIs - Most Important Metrics */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <StatsCard
            title={t("performance.avgScore")}
            value={Math.round(stats.averageScore || 0)}
            icon={TrendingUp}
            description={t("performance.primaryMetric")}
            className="border-primary/50 bg-primary/5"
          />
          <StatsCard
            title={t("performance.accuracy")}
            value={`${stats.overallAccuracy?.toFixed(0) || 0}%`}
            icon={Target}
            trend={stats.recentAccuracy > stats.overallAccuracy ? `+${(stats.recentAccuracy - stats.overallAccuracy).toFixed(0)}%` : null}
            trendUp={stats.recentAccuracy > stats.overallAccuracy}
          />
          <StatsCard
            title={t("performance.bestScore")}
            value={stats.bestScore || 0}
            icon={Trophy}
          />
          <StatsCard
            title={t("performance.leaderboardPosition")}
            value={`#${stats.leaderboardPosition || "-"}`}
            icon={Trophy}
            description={t("performance.outOfPlayers", { count: stats.totalPlayers || 0 })}
          />
        </div>

        <Separator />

        {/* Secondary Stats - Contextual Information */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <StatsCard
            title={t("performance.gamesPlayed")}
            value={stats.totalGamesPlayed}
            icon={Brain}
          />
          <StatsCard
            title={t("performance.currentStreak")}
            value={`${stats.currentStreak || 0} ${t("performance.days")}`}
            icon={Flame}
          />
          <StatsCard
            title={t("performance.totalPoints")}
            value={stats.totalPointsEarned || 0}
            icon={Zap}
          />
          <StatsCard
            title={t("performance.bestPerformingLevel")}
            value={stats.bestPerformingLevel || "N/A"}
            icon={BarChart3}
          />
        </div>

        <Separator />

        {/* Level & Progress Section */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Best Performing Level */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-base">{t("performance.bestPerformingLevel")}</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-3">
                <Badge className={`text-lg px-4 py-1 ${difficultyColors[stats.bestPerformingLevel] || difficultyColors.EASY}`}>
                  {stats.bestPerformingLevel || "N/A"}
                </Badge>
                <div className="flex-1">
                  <p className="text-sm text-muted-foreground">
                    {t("performance.bestLevelDescription")}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Progress Summary */}
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-base">{t("performance.progressSummary")}</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex items-center gap-3">
                <div className="text-center">
                  <p className="text-2xl font-bold text-primary">{stats.totalCorrectAnswers || 0}</p>
                  <p className="text-xs text-muted-foreground">{t("performance.correctAnswers")}</p>
                </div>
                <div className="flex-1">
                  <Progress value={stats.overallAccuracy || 0} className="h-2" />
                  <p className="text-sm text-muted-foreground mt-1">
                    {t("performance.questionsAnswered")}: {stats.totalQuestionsAnswered || 0}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Score Evolution Chart */}
        <Separator />
        <GlobalScoreChart 
          recentGames={stats.recentGames || []} 
          averageScore={stats.averageScore || 0} 
        />

        {/* Category Breakdown */}
        {stats.categoryStats && stats.categoryStats.length > 0 && (
          <>
            <Separator />
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">{t("performance.performanceByCategory")}</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {stats.categoryStats.map((category) => (
                    <div key={category.categoryId} className="space-y-2">
                      <div className="flex justify-between items-center">
                        <span className="font-medium">{category.categoryName}</span>
                        <span className="text-sm text-muted-foreground">
                          {category.gamesPlayed} {t("performance.games")} • {t("performance.avg")}: {Math.round(category.averageScore || 0)} pts
                        </span>
                      </div>
                      <div className="flex items-center gap-2">
                        <Progress value={category.accuracy} className="h-2 flex-1" />
                        <span className="text-sm font-medium w-12 text-right">
                          {category.accuracy?.toFixed(0) || 0}%
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </>
        )}

        {/* Game Mode Breakdown */}
        {stats.gameModeStats && stats.gameModeStats.length > 0 && (
          <>
            <Separator />
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">{t("performance.performanceByGameMode")}</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  {stats.gameModeStats.map((mode) => (
                    <div key={mode.gameMode} className="p-4 rounded-lg bg-muted/50">
                      <div className="flex items-center justify-between mb-2">
                        <Badge variant="outline" className="text-sm">{mode.gameMode}</Badge>
                        <span className="text-sm text-muted-foreground">
                          {mode.gamesPlayed} {t("performance.games")}
                        </span>
                      </div>
                      <div className="space-y-2">
                        <div className="flex justify-between text-sm">
                          <span className="text-muted-foreground">{t("performance.avgScore")}</span>
                          <span className="font-medium">{Math.round(mode.averageScore)}</span>
                        </div>
                        <div className="flex justify-between text-sm">
                          <span className="text-muted-foreground">{t("performance.bestScore")}</span>
                          <span className="font-medium">{mode.bestScore}</span>
                        </div>
                        <div className="flex justify-between text-sm">
                          <span className="text-muted-foreground">{t("performance.accuracy")}</span>
                          <span className="font-medium">{mode.accuracy?.toFixed(0)}%</span>
                        </div>
                        <div className="flex justify-between text-sm">
                          <span className="text-muted-foreground">{t("performance.totalQuestions")}</span>
                          <span className="font-medium">{mode.totalQuestions}</span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </>
        )}

        {/* Recent Games */}
        {stats.recentGames && stats.recentGames.length > 0 && (
          <>
            <Separator />
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">{t("performance.recentGames")}</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {stats.recentGames.map((game) => (
                    <div 
                      key={game.sessionId} 
                      className="flex items-center justify-between p-3 rounded-lg bg-muted/50"
                    >
                      <div className="flex items-center gap-3">
                        <div className="text-center">
                          <p className="text-lg font-bold">{game.score}</p>
                          <p className="text-xs text-muted-foreground">pts</p>
                        </div>
                        <div>
                          <p className="font-medium">{game.categoryName}</p>
                          <p className="text-sm text-muted-foreground">
                            {game.correctAnswers}/{game.totalQuestions} {t("performance.correct").toLowerCase()} • {game.accuracy?.toFixed(0) || 0}%
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <Badge variant="outline" className={difficultyColors[game.difficultyReached]}>
                          {game.difficultyReached}
                        </Badge>
                        <p className="text-xs text-muted-foreground mt-1">
                          {new Date(game.completedAt).toLocaleDateString()}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </>
        )}
      </div>
    );
  };

  // Smatch Performance Content (placeholder)
  const renderSmatchContent = () => (
    <div className="text-center py-12">
      <Zap className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
      <h3 className="text-lg font-semibold mb-2">{t("performance.smatchComingSoon")}</h3>
      <p className="text-muted-foreground">
        {t("performance.smatchDescription")}
      </p>
    </div>
  );

  return (
    <div className="p-6 max-w-6xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold">{t("performance.title")}</h1>
        <p className="text-muted-foreground text-sm mt-1">
          {t("performance.description")}
        </p>
      </div>

      {/* Game Type Tabs */}
      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="mb-6">
          <TabsTrigger value="qcm" className="flex items-center gap-2">
            <HelpCircle className="h-4 w-4" />
            {t("performance.qcm")}
          </TabsTrigger>
          <TabsTrigger value="smatch" className="flex items-center gap-2">
            <Zap className="h-4 w-4" />
            {t("performance.smatch")}
          </TabsTrigger>
        </TabsList>

        <TabsContent value="qcm">
          {renderQCMContent()}
        </TabsContent>

        <TabsContent value="smatch">
          {renderSmatchContent()}
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default PerformancePage;

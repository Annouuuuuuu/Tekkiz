import { useTranslation } from "react-i18next";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

/**
 * GlobalScoreChart - Displays score evolution over recent games
 * @param {Array} recentGames - Array of recent game data with score and date
 * @param {number} averageScore - User's average score
 */
const GlobalScoreChart = ({ recentGames = [], averageScore = 0 }) => {
  const { t } = useTranslation("common");

  // Transform recent games into chart data
  // Take up to 6 most recent games and normalize scores
  const chartData = recentGames
    .slice(0, 6)
    .reverse()
    .map((game, index) => ({
      label: `G${index + 1}`,
      score: game.score || 0,
      date: game.completedAt ? new Date(game.completedAt).toLocaleDateString() : '',
    }));

  // If no real data, show placeholder
  const hasData = chartData.length > 0;
  const displayData = hasData ? chartData : [
    { label: "G1", score: 0, date: '' },
    { label: "G2", score: 0, date: '' },
    { label: "G3", score: 0, date: '' },
    { label: "G4", score: 0, date: '' },
    { label: "G5", score: 0, date: '' },
    { label: "G6", score: 0, date: '' },
  ];

  // Calculate max score for normalization
  const maxScore = Math.max(...displayData.map(d => d.score), 100);

  return (
    <Card className="w-full">
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg">{t("performance.scoreEvolution", "Score Evolution")}</CardTitle>
          <div className="text-sm text-muted-foreground">
            {t("performance.avgScore", "Avg Score")}: <span className="font-semibold text-foreground">{Math.round(averageScore)}</span>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        {!hasData ? (
          <div className="h-40 flex items-center justify-center text-muted-foreground">
            {t("performance.noData", "No games played yet")}
          </div>
        ) : (
          <div className="relative h-40">
            {/* Y-axis labels */}
            <div className="absolute left-0 top-0 bottom-6 w-8 flex flex-col justify-between text-xs text-muted-foreground">
              <span>{maxScore}</span>
              <span>{Math.round(maxScore / 2)}</span>
              <span>0</span>
            </div>

            {/* Chart area */}
            <div className="ml-10 h-34 relative">
              {/* Grid lines */}
              <div className="absolute inset-0 flex flex-col justify-between">
                <div className="border-b border-border/50" />
                <div className="border-b border-border/50" />
                <div className="border-b border-border/50" />
              </div>

              {/* Bars */}
              <div className="absolute inset-0 flex items-end justify-around px-2">
                {displayData.map((data) => {
                  const heightPercent = maxScore > 0 ? (data.score / maxScore) * 100 : 0;
                  return (
                    <div
                      key={data.label}
                      className="flex flex-col items-center gap-1"
                    >
                      <div
                        className="w-10 bg-primary/80 rounded-t-sm transition-all hover:bg-primary relative group"
                        style={{ height: `${heightPercent}%`, minHeight: data.score > 0 ? '4px' : '0' }}
                      >
                        {/* Tooltip */}
                        <div className="absolute -top-6 left-1/2 -translate-x-1/2 bg-popover text-popover-foreground text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap z-10">
                          {data.score} pts
                          {data.date && <span className="text-muted-foreground ml-1">• {data.date}</span>}
                        </div>
                      </div>
                      <span className="text-xs text-muted-foreground">{data.label}</span>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        )}

        {/* Legend */}
        <div className="flex items-center justify-center gap-4 mt-4 text-sm">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 rounded-sm bg-primary" />
            <span className="text-muted-foreground">{t("performance.scorePerGame", "Score per game")}</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default GlobalScoreChart;

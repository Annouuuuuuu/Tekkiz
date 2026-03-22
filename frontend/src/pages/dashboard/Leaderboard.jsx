import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Trophy, Search, HelpCircle, Zap, Loader2 } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import qcmGameService from "@/services/qcmGame.service";

const getInitials = (username) => {
  return username?.slice(0, 2).toUpperCase() || "??";
};

const getRankBadge = (rank) => {
  if (rank === 1) {
    return (
      <div className="flex items-center justify-center w-8 h-8 rounded-full bg-yellow-500/20">
        <Trophy className="w-5 h-5 text-yellow-500" />
      </div>
    );
  }
  if (rank === 2) {
    return (
      <div className="flex items-center justify-center w-8 h-8 rounded-full bg-gray-400/20">
        <Trophy className="w-5 h-5 text-gray-400" />
      </div>
    );
  }
  if (rank === 3) {
    return (
      <div className="flex items-center justify-center w-8 h-8 rounded-full bg-amber-600/20">
        <Trophy className="w-5 h-5 text-amber-600" />
      </div>
    );
  }
  return <span className="font-medium text-muted-foreground">{rank}</span>;
};

// QCM Leaderboard Component
const QcmLeaderboard = () => {
  const { t } = useTranslation("common");
  const [entries, setEntries] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [categoryId, setCategoryId] = useState("all");
  const [gameMode, setGameMode] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    // Fetch categories for filter
    const fetchCategories = async () => {
      try {
        const response = await qcmGameService.getCategories();
        setCategories(response.data || response);
      } catch (err) {
        console.error("Failed to fetch categories:", err);
      }
    };
    fetchCategories();
  }, []);

  useEffect(() => {
    const fetchLeaderboard = async () => {
      setIsLoading(true);
      try {
        const params = {
          page: 0,
          size: 50,
          categoryId: categoryId !== "all" ? parseInt(categoryId) : undefined,
          gameMode: gameMode !== "all" ? gameMode : "ALL",
        };
        const response = await qcmGameService.getLeaderboard(params);
        setEntries(response.data?.entries || response.entries || []);
      } catch (err) {
        console.error("Failed to fetch leaderboard:", err);
        setError(err.message || "Failed to load leaderboard");
      } finally {
        setIsLoading(false);
      }
    };
    fetchLeaderboard();
  }, [categoryId, gameMode]);

  const filteredEntries = entries.filter((entry) =>
    entry.username?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="text-center">
          <Loader2 className="h-8 w-8 animate-spin mx-auto mb-3" />
          <p className="text-muted-foreground">{t("leaderboard.loading")}</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <p className="text-destructive">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* Filters */}
      <div className="flex flex-wrap items-center gap-3">
        <div className="flex items-center gap-2">
          <span className="text-sm text-muted-foreground">{t("leaderboard.category")}:</span>
          <Select value={categoryId} onValueChange={setCategoryId}>
            <SelectTrigger className="w-[160px] h-8">
              <SelectValue placeholder={t("leaderboard.allCategories")} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">{t("leaderboard.allCategories")}</SelectItem>
              {categories.map((cat) => (
                <SelectItem key={cat.id} value={cat.id.toString()}>
                  {cat.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        <div className="flex items-center gap-2">
          <span className="text-sm text-muted-foreground">{t("leaderboard.gameMode")}:</span>
          <Select value={gameMode} onValueChange={setGameMode}>
            <SelectTrigger className="w-[120px] h-8">
              <SelectValue placeholder={t("leaderboard.allModes")} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">{t("leaderboard.allModes")}</SelectItem>
              <SelectItem value="BLITZ">Blitz</SelectItem>
              <SelectItem value="RUSH">Rush</SelectItem>
              <SelectItem value="CLASSIC">Classic</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <div className="flex-1 min-w-[200px] ml-auto">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder={t("leaderboard.searchPlayer")}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 h-8"
            />
          </div>
        </div>
      </div>

      {/* Table */}
      {filteredEntries.length === 0 ? (
        <div className="text-center py-12">
          <Trophy className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
          <p className="text-muted-foreground">{t("leaderboard.noPlayers")}</p>
        </div>
      ) : (
        <Card>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[80px]">{t("leaderboard.rank")}</TableHead>
                  <TableHead>{t("leaderboard.player")}</TableHead>
                  <TableHead className="bg-primary/5">{t("leaderboard.avgScore")}</TableHead>
                  <TableHead>{t("leaderboard.accuracy")}</TableHead>
                  <TableHead>{t("leaderboard.games")}</TableHead>
                  <TableHead>{t("leaderboard.bestScore")}</TableHead>
                  <TableHead>{t("leaderboard.topCategory")}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredEntries.map((entry) => (
                  <TableRow key={entry.userId || entry.rank} className="hover:bg-muted/50">
                    <TableCell>{getRankBadge(entry.rank)}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-3">
                        <Avatar className="h-8 w-8">
                          <AvatarImage src={entry.avatarUrl} />
                          <AvatarFallback>{getInitials(entry.username)}</AvatarFallback>
                        </Avatar>
                        <span className="font-medium">{entry.username}</span>
                      </div>
                    </TableCell>
                    <TableCell className="bg-primary/5">
                      <span className="font-bold text-primary text-lg">{Math.round(entry.averageScore || 0)}</span>
                      <span className="text-xs text-muted-foreground ml-1">pts</span>
                    </TableCell>
                    <TableCell>
                      <span
                        className={cn(
                          "font-medium",
                          entry.accuracy >= 70
                            ? "text-green-500"
                            : entry.accuracy >= 50
                            ? "text-yellow-500"
                            : "text-muted-foreground"
                        )}
                      >
                        {entry.accuracy?.toFixed(0) || 0}%
                      </span>
                    </TableCell>
                    <TableCell>{entry.gamesPlayed}</TableCell>
                    <TableCell>
                      <span className="font-medium">{entry.bestScore}</span>
                    </TableCell>
                    <TableCell>
                      <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-primary/10 text-primary">
                        {entry.topCategory || "N/A"}
                      </span>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      )}
    </div>
  );
};

// Smatch Leaderboard Component (placeholder)
const SmatchLeaderboard = () => {
  const { t } = useTranslation("common");

  return (
    <div className="text-center py-12">
      <Zap className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
      <h3 className="text-lg font-semibold mb-2">{t("leaderboard.smatchComingSoon")}</h3>
      <p className="text-muted-foreground">
        {t("leaderboard.smatchDescription")}
      </p>
    </div>
  );
};

// Main Leaderboard Page
const LeaderboardPage = () => {
  const { t } = useTranslation("common");
  const [activeTab, setActiveTab] = useState("qcm");

  return (
    <div className="p-6 max-w-6xl mx-auto">
      {/* Header */}
      <div className="mb-6">
        <h1 className="text-2xl font-bold">{t("leaderboard.title")}</h1>
        <p className="text-muted-foreground text-sm mt-1">
          {t("leaderboard.description")}
        </p>
      </div>

      {/* Game Type Tabs */}
      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="mb-6">
          <TabsTrigger value="qcm" className="flex items-center gap-2">
            <HelpCircle className="h-4 w-4" />
            {t("leaderboard.qcm")}
          </TabsTrigger>
          <TabsTrigger value="smatch" className="flex items-center gap-2">
            <Zap className="h-4 w-4" />
            {t("leaderboard.smatch")}
          </TabsTrigger>
        </TabsList>

        <TabsContent value="qcm">
          <QcmLeaderboard />
        </TabsContent>

        <TabsContent value="smatch">
          <SmatchLeaderboard />
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default LeaderboardPage;

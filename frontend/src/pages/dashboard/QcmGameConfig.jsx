/**
 * QCM Game Configuration Screen
 * 
 * Allows users to configure their QCM game session including:
 * - Category selection
 * - Tag filtering
 * - Game mode selection (BLITZ, RUSH, CLASSIC)
 * 
 * Game Modes:
 * - BLITZ: 60s initial, 90s max - Fast-paced, high points
 * - RUSH: 120s initial, 150s max - Medium pace, balanced
 * - CLASSIC: 300s initial, 360s max - Long game, endurance
 * 
 * Note: In-game difficulty progression is score-based:
 * - Everyone starts at EASY
 * - Progresses to MEDIUM/HARD/EXPERT based on points earned
 */

import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { QcmGameProvider, useQcmGame } from '../../contexts/QcmGameContext';
import qcmGameService from '../../services/qcmGame.service';
import { Button } from '../../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../../components/ui/card';
import { Label } from '../../components/ui/label';
import { Badge } from '../../components/ui/badge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../../components/ui/select';
import { Loader2, Play, BookOpen, Hash, Zap, Timer, Clock } from 'lucide-react';

// Game modes with their configurations
const GAME_MODES = {
  BLITZ: {
    initialTime: 60,
    maxTime: 90,
    label: 'Blitz',
    description: '1 min',
    icon: Zap,
    color: 'text-red-500',
    borderColor: 'border-red-500',
    bgColor: 'bg-red-500/10',
  },
  RUSH: {
    initialTime: 120,
    maxTime: 150,
    label: 'Rush',
    description: '2 min',
    icon: Timer,
    color: 'text-yellow-500',
    borderColor: 'border-yellow-500',
    bgColor: 'bg-yellow-500/10',
  },
  CLASSIC: {
    initialTime: 300,
    maxTime: 360,
    label: 'Classic',
    description: '5 min',
    icon: Clock,
    color: 'text-green-500',
    borderColor: 'border-green-500',
    bgColor: 'bg-green-500/10',
  },
};

function QcmGameConfigContent() {
  const { t } = useTranslation("common");
  const navigate = useNavigate();
  const { setSession, setConfig, setLoading, setError } = useQcmGame();

  // Form state
  const [categories, setCategories] = useState([]);
  const [availableTags, setAvailableTags] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedTags, setSelectedTags] = useState([]);
  const [gameMode, setGameMode] = useState('CLASSIC');
  const [showHints, setShowHints] = useState(true);
  const [showExplanations, setShowExplanations] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Fetch categories on mount
  useEffect(() => {
    const fetchCategories = async () => {
      setIsLoading(true);
      try {
        const response = await qcmGameService.getCategories();
        setCategories(response.data || response);
      } catch (error) {
        console.error('Failed to fetch categories:', error);
        setError('Failed to load categories');
      } finally {
        setIsLoading(false);
      }
    };

    fetchCategories();
  }, [setError]);

  // Fetch tags when category changes
  useEffect(() => {
    const fetchTags = async () => {
      if (!selectedCategory) {
        setAvailableTags([]);
        return;
      }

      try {
        const response = await qcmGameService.getTagsByCategory(selectedCategory);
        setAvailableTags(response.data || response);
      } catch (error) {
        console.error('Failed to fetch tags:', error);
        setAvailableTags([]);
      }
    };

    fetchTags();
    setSelectedTags([]);
  }, [selectedCategory]);

  const handleTagToggle = (tagId) => {
    setSelectedTags((prev) =>
      prev.includes(tagId)
        ? prev.filter((id) => id !== tagId)
        : [...prev, tagId]
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!selectedCategory) {
      setError('Please select a category');
      return;
    }

    setIsSubmitting(true);
    setLoading(true);

    try {
      const config = {
        categoryId: parseInt(selectedCategory, 10),
        tagIds: selectedTags.length > 0 ? selectedTags : undefined,
        gameMode: gameMode,
        showHints,
        showExplanations,
      };

      const response = await qcmGameService.createGameSession(config);
      setSession(response);
      setConfig(config);

      // Navigate to game play screen
      navigate(`/dashboard/play/qcm/${response.sessionId}`);
    } catch (error) {
      console.error('Failed to create game session:', error);
      setError(error.message || 'Failed to create game session');
    } finally {
      setIsSubmitting(false);
      setLoading(false);
    }
  };

  return (
    <div className="container max-w-3xl mx-auto py-8 px-4">
      <div className="mb-6">
        <h1 className="text-2xl font-bold mb-1">{t('qcm.configureGame')}</h1>
        <p className="text-muted-foreground text-sm">
          {t('qcm.configureDescription')}
        </p>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="grid gap-5">
          {/* Category Selection */}
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="flex items-center gap-2 text-lg">
                <BookOpen className="h-4 w-4" />
                {t('qcm.category')}
              </CardTitle>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="flex items-center justify-center py-4">
                  <Loader2 className="h-5 w-5 animate-spin" />
                </div>
              ) : (
                <Select value={selectedCategory} onValueChange={setSelectedCategory}>
                  <SelectTrigger>
                    <SelectValue placeholder={t('qcm.selectCategory')} />
                  </SelectTrigger>
                  <SelectContent>
                    {categories.map((category) => (
                      <SelectItem key={category.id} value={category.id.toString()}>
                        {category.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            </CardContent>
          </Card>

          {/* Tag Selection */}
          {selectedCategory && availableTags.length > 0 && (
            <Card>
              <CardHeader className="pb-3">
                <CardTitle className="flex items-center gap-2 text-lg">
                  <Hash className="h-4 w-4" />
                  {t('qcm.tags')}
                </CardTitle>
                <CardDescription className="text-xs">
                  {t('qcm.tagsDescription')}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="flex flex-wrap gap-2">
                  {availableTags.map((tag) => (
                    <Badge
                      key={tag.id}
                      variant={selectedTags.includes(tag.id) ? 'default' : 'outline'}
                      className="cursor-pointer"
                      onClick={() => handleTagToggle(tag.id)}
                    >
                      {tag.name}
                    </Badge>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

          {/* Game Mode Selection */}
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-lg">{t('qcm.gameMode')}</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-3 gap-3">
                {Object.entries(GAME_MODES).map(([key, mode]) => {
                  const IconComponent = mode.icon;
                  const isSelected = gameMode === key;
                  return (
                    <div
                      key={key}
                      onClick={() => setGameMode(key)}
                      className={`cursor-pointer rounded-lg border-2 p-3 transition-all ${
                        isSelected
                          ? `${mode.borderColor} ${mode.bgColor}`
                          : 'border-gray-200 hover:border-gray-300'
                      }`}
                    >
                      <div className="flex flex-col items-center text-center">
                        <IconComponent className={`h-6 w-6 mb-1 ${mode.color}`} />
                        <span className="font-semibold text-sm">{mode.label}</span>
                        <span className="text-xs text-muted-foreground">{mode.description}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </CardContent>
          </Card>

          {/* Game Options */}
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-lg">{t('qcm.options')}</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex flex-wrap gap-4">
                <div className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    id="hints"
                    checked={showHints}
                    onChange={(e) => setShowHints(e.target.checked)}
                    className="rounded border-gray-300"
                  />
                  <Label htmlFor="hints" className="text-sm">{t('qcm.showHints')}</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    id="explanations"
                    checked={showExplanations}
                    onChange={(e) => setShowExplanations(e.target.checked)}
                    className="rounded border-gray-300"
                  />
                  <Label htmlFor="explanations" className="text-sm">{t('qcm.showExplanations')}</Label>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Submit Button */}
          <Button
            type="submit"
            size="lg"
            className="w-full"
            disabled={!selectedCategory || isSubmitting}
          >
            {isSubmitting ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                {t('qcm.creating')}
              </>
            ) : (
              <>
                <Play className="mr-2 h-4 w-4" />
                {t('qcm.startGame')}
              </>
            )}
          </Button>
        </div>
      </form>
    </div>
  );
}

// Wrapper component with provider
export default function QcmGameConfig() {
  return (
    <QcmGameProvider>
      <QcmGameConfigContent />
    </QcmGameProvider>
  );
}

export { GAME_MODES };

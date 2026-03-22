import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import MainLayout from "./layouts/MainLayout";
import DashboardLayout from "./layouts/DashboardLayout";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import OAuthCallback from "./pages/OAuthCallback";
import PlayPage from "./pages/dashboard/Play";
import QcmGameConfig from "./pages/dashboard/QcmGameConfig";
import QcmGamePlay from "./pages/dashboard/QcmGamePlay";
import QcmGameResults from "./pages/dashboard/QcmGameResults";
import PerformancePage from "./pages/dashboard/Performance";
import LeaderboardPage from "./pages/dashboard/Leaderboard";
import SettingsPage from "./pages/dashboard/Settings";
import AboutPage from "./pages/dashboard/About";
import { ThemeProvider } from "./components/theme-provider";
import { AuthProvider, useAuth } from "./contexts/AuthContext";

// Protected Route wrapper
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();
  
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
      </div>
    );
  }
  
  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }
  
  return children;
};

export default function App() {
  return (
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <BrowserRouter>
        <AuthProvider>
          <Routes>

            {/* Public layout */}
            <Route element={<MainLayout />}>
              <Route path="/" element={<Home />}></Route>
              <Route path="/about" element={<Home />}></Route>
            </Route>

            {/* Auth routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/oauth/callback" element={<OAuthCallback />} />

            {/* Protected dashboard routes */}
            <Route
              element={
                <ProtectedRoute>
                  <DashboardLayout />
                </ProtectedRoute>
              }
            >
              <Route path="/dashboard" element={<Navigate to="/dashboard/play" replace />} />
              <Route path="/dashboard/play" element={<PlayPage />} />
              <Route path="/dashboard/play/qcm/config" element={<QcmGameConfig />} />
              <Route path="/dashboard/play/qcm/:sessionId" element={<QcmGamePlay />} />
              <Route path="/dashboard/play/qcm/:sessionId/results" element={<QcmGameResults />} />
              <Route path="/dashboard/performance" element={<PerformancePage />} />
              <Route path="/dashboard/leaderboard" element={<LeaderboardPage />} />
              <Route path="/dashboard/settings" element={<SettingsPage />} />
              <Route path="/dashboard/about" element={<AboutPage />} />
            </Route>

          </Routes>
        </AuthProvider>
      </BrowserRouter>
    </ThemeProvider>
  );
}

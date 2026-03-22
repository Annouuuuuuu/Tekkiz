import { Outlet } from "react-router-dom";
import Sidebar from "@/components/dashboard/Sidebar";
import Topbar from "@/components/dashboard/Topbar";

const DashboardLayout = () => {
  return (
    <div className="min-h-screen bg-background">
      {/* Sidebar fixe à gauche */}
      <Sidebar />

      {/* Zone principale à droite */}
      <div className="ml-[240px]">
        {/* Topbar horizontale */}
        <Topbar />

        {/* Contenu principal scrollable */}
        <main className="pt-16 min-h-screen">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;

import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Topbar from './Topbar';

const MainLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const closeMobileSidebar = () => {
    setSidebarOpen(false);
  };

  return (
    <div className="sp-layout">
      <Sidebar isOpen={sidebarOpen} onCloseMobile={closeMobileSidebar} />

      {sidebarOpen && (
        <div
          className="sp-sidebar-backdrop d-lg-none"
          onClick={closeMobileSidebar}
        ></div>
      )}

      <div className="sp-main-wrapper">
        <Topbar onToggleSidebar={toggleSidebar} />
        <main className="sp-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default MainLayout;

import React, { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { useAuthStore } from './store/authStore';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Bot, Sparkles, Shield, Cpu, LogOut, User as UserIcon } from 'lucide-react';

const DashboardView: React.FC = () => {
  const { user, tenant, logout } = useAuthStore();

  return (
    <div className="min-h-screen bg-[#0b0f19] text-slate-100 flex flex-col items-center justify-center p-6">
      <div className="max-w-3xl w-full text-center space-y-8 bg-slate-900/60 border border-slate-800 backdrop-blur-xl p-10 rounded-2xl shadow-2xl">
        <div className="flex justify-between items-center pb-4 border-b border-slate-800">
          <div className="flex items-center gap-2">
            <Bot className="w-6 h-6 text-purple-400" />
            <span className="font-bold text-lg text-white">UltronAI</span>
          </div>
          <div className="flex items-center gap-4 text-xs">
            <span className="px-3 py-1 rounded-full bg-slate-800 text-slate-300 border border-slate-700 flex items-center gap-1.5">
              <UserIcon className="w-3.5 h-3.5 text-purple-400" />
              {user?.fullName} ({user?.role})
            </span>
            <button
              onClick={() => logout()}
              className="px-3 py-1.5 rounded-lg bg-red-500/10 text-red-400 border border-red-500/20 hover:bg-red-500/20 transition flex items-center gap-1 font-medium"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span>Logout</span>
            </button>
          </div>
        </div>

        <div className="inline-flex items-center gap-3 px-4 py-2 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-sm font-medium">
          <Sparkles className="w-4 h-4 text-emerald-400" />
          <span>Tenant Isolated Context Active: {tenant?.name || 'Default Tenant'} ({tenant?.slug})</span>
        </div>

        <h1 className="text-4xl md:text-5xl font-bold tracking-tight text-white">
          Authenticated Security Context
        </h1>

        <p className="text-slate-400 text-lg max-w-xl mx-auto">
          Welcome <span className="text-white font-medium">{user?.fullName}</span>. Phase 2 JWT Stateless Security, Role-Based Access Control, and Tenant Context Isolation are active.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-left pt-4">
          <div className="p-4 rounded-xl bg-slate-800/40 border border-slate-800/80">
            <Shield className="w-6 h-6 text-purple-400 mb-2" />
            <h3 className="font-semibold text-white">User Role</h3>
            <p className="text-xs text-slate-400 mt-1">{user?.role}</p>
          </div>
          <div className="p-4 rounded-xl bg-slate-800/40 border border-slate-800/80">
            <Cpu className="w-6 h-6 text-indigo-400 mb-2" />
            <h3 className="font-semibold text-white">Tenant ID</h3>
            <p className="text-xs text-slate-400 mt-1">Tenant #{user?.tenantId}</p>
          </div>
          <div className="p-4 rounded-xl bg-slate-800/40 border border-slate-800/80">
            <Bot className="w-6 h-6 text-emerald-400 mb-2" />
            <h3 className="font-semibold text-white">Security Status</h3>
            <p className="text-xs text-slate-400 mt-1">JWT Bearer Verified</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export const App: React.FC = () => {
  const { checkAuth } = useAuthStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<DashboardView />} />
        </Route>

        <Route path="*" element={<Link to="/" replace className="text-purple-400 underline">Return Home</Link>} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;

import React from 'react';
import { Bot, Sparkles, Shield, Cpu } from 'lucide-react';

export const App: React.FC = () => {
  return (
    <div className="min-h-screen bg-[#0b0f19] text-slate-100 flex flex-col items-center justify-center p-6">
      <div className="max-w-3xl w-full text-center space-y-8 bg-slate-900/60 border border-slate-800 backdrop-blur-xl p-10 rounded-2xl shadow-2xl">
        <div className="inline-flex items-center gap-3 px-4 py-2 rounded-full bg-purple-500/10 border border-purple-500/20 text-purple-400 text-sm font-medium">
          <Sparkles className="w-4 h-4 text-purple-400" />
          <span>UltronAI Platform Foundation Active</span>
        </div>

        <h1 className="text-4xl md:text-5xl font-bold tracking-tight text-white">
          Intelligent Conversational AI & Automation
        </h1>

        <p className="text-slate-400 text-lg max-w-xl mx-auto">
          Enterprise multi-tenant conversational engine powered by real-time WebSocket communication, NLP intent classification, and deterministic workflow automation.
        </p>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-left pt-4">
          <div className="p-4 rounded-xl bg-slate-800/40 border border-slate-800/80">
            <Bot className="w-6 h-6 text-purple-400 mb-2" />
            <h3 className="font-semibold text-white">AI Engine</h3>
            <p className="text-xs text-slate-400 mt-1">Intent detection & dynamic workflow execution</p>
          </div>
          <div className="p-4 rounded-xl bg-slate-800/40 border border-slate-800/80">
            <Cpu className="w-6 h-6 text-indigo-400 mb-2" />
            <h3 className="font-semibold text-white">Real-Time Chat</h3>
            <p className="text-xs text-slate-400 mt-1">Low latency WebSocket gateway transport</p>
          </div>
          <div className="p-4 rounded-xl bg-slate-800/40 border border-slate-800/80">
            <Shield className="w-6 h-6 text-emerald-400 mb-2" />
            <h3 className="font-semibold text-white">Multi-Tenant</h3>
            <p className="text-xs text-slate-400 mt-1">Isolated datasets and strict SSRF security</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default App;

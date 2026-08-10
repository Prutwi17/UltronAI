import React, { useEffect } from 'react';
import { useAuthStore } from '../store/authStore';
import { useChatStore } from '../store/chatStore';
import { ConversationList } from '../components/chat/ConversationList';
import { ChatWindow } from '../components/chat/ChatWindow';
import { Bot, LogOut, Shield } from 'lucide-react';
import { Link } from 'react-router-dom';

export const ChatPage: React.FC = () => {
  const { user, tenant, logout } = useAuthStore();
  const {
    conversations,
    activeConversation,
    messages,
    isLoadingConversations,
    isLoadingMessages,
    wsStatus,
    fetchConversations,
    createConversation,
    selectConversation,
    sendMessage,
    closeActiveConversation,
    connectWebSocket,
    disconnectWebSocket,
  } = useChatStore();

  useEffect(() => {
    fetchConversations();
    connectWebSocket();

    return () => {
      disconnectWebSocket();
    };
  }, []);

  return (
    <div className="min-h-screen bg-[#0b0f19] text-slate-100 flex flex-col h-screen overflow-hidden">
      {/* Header */}
      <header className="px-6 py-3 border-b border-slate-800 bg-slate-900/90 backdrop-blur flex items-center justify-between shrink-0">
        <div className="flex items-center gap-3">
          <div className="p-1.5 rounded-lg bg-purple-500/10 border border-purple-500/20 text-purple-400">
            <Bot className="w-5 h-5" />
          </div>
          <div>
            <h1 className="font-bold text-white text-sm">UltronAI Chat Gateway</h1>
            <p className="text-[10px] text-slate-400">Tenant: {tenant?.name || 'Default'} ({tenant?.slug})</p>
          </div>
        </div>

        <div className="flex items-center gap-3 text-xs">
          <Link to="/" className="px-3 py-1.5 rounded-lg bg-slate-800 text-slate-300 border border-slate-700 hover:bg-slate-700 transition">
            Dashboard
          </Link>
          <span className="px-3 py-1 rounded-full bg-purple-500/10 text-purple-400 border border-purple-500/20 flex items-center gap-1">
            <Shield className="w-3 h-3" />
            {user?.role}
          </span>
          <button
            onClick={() => logout()}
            className="p-2 rounded-lg bg-red-500/10 text-red-400 border border-red-500/20 hover:bg-red-500/20 transition"
            title="Logout"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </header>

      {/* Main Chat Interface */}
      <div className="flex-1 flex overflow-hidden">
        <ConversationList
          conversations={conversations}
          activeConversation={activeConversation}
          onSelect={selectConversation}
          onNew={() => createConversation()}
          isLoading={isLoadingConversations}
        />

        <ChatWindow
          conversation={activeConversation}
          messages={messages}
          currentUserId={user?.id}
          isLoading={isLoadingMessages}
          wsStatus={wsStatus}
          onSendMessage={sendMessage}
          onCloseConversation={closeActiveConversation}
        />
      </div>
    </div>
  );
};

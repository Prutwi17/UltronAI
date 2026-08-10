import React from 'react';
import { Conversation } from '../../types/chat';
import { MessageSquare, Plus, CheckCircle2, Clock, XCircle } from 'lucide-react';

interface ConversationListProps {
  conversations: Conversation[];
  activeConversation: Conversation | null;
  onSelect: (conversation: Conversation) => void;
  onNew: () => void;
  isLoading: boolean;
}

export const ConversationList: React.FC<ConversationListProps> = ({
  conversations,
  activeConversation,
  onSelect,
  onNew,
  isLoading,
}) => {
  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return <span className="inline-flex items-center gap-1 text-[10px] text-emerald-400 font-medium px-2 py-0.5 rounded-full bg-emerald-500/10 border border-emerald-500/20"><CheckCircle2 className="w-3 h-3" /> Active</span>;
      case 'WAITING':
        return <span className="inline-flex items-center gap-1 text-[10px] text-amber-400 font-medium px-2 py-0.5 rounded-full bg-amber-500/10 border border-amber-500/20"><Clock className="w-3 h-3" /> Waiting</span>;
      case 'CLOSED':
      case 'RESOLVED':
        return <span className="inline-flex items-center gap-1 text-[10px] text-slate-400 font-medium px-2 py-0.5 rounded-full bg-slate-800 border border-slate-700"><XCircle className="w-3 h-3" /> Closed</span>;
      default:
        return null;
    }
  };

  return (
    <div className="w-80 border-r border-slate-800 bg-slate-900/60 flex flex-col h-full shrink-0">
      <div className="p-4 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <MessageSquare className="w-5 h-5 text-purple-400" />
          <h2 className="font-semibold text-white text-sm">Conversations</h2>
        </div>
        <button
          onClick={onNew}
          className="p-1.5 rounded-lg bg-purple-600 hover:bg-purple-500 text-white transition flex items-center gap-1 text-xs font-medium"
          title="New Conversation"
        >
          <Plus className="w-4 h-4" />
          <span>New</span>
        </button>
      </div>

      <div className="flex-1 overflow-y-auto divide-y divide-slate-800/40">
        {isLoading ? (
          <div className="p-6 text-center text-slate-500 text-sm">Loading conversations...</div>
        ) : conversations.length === 0 ? (
          <div className="p-6 text-center text-slate-500 text-sm">No conversations found</div>
        ) : (
          conversations.map((conv) => {
            const isActive = activeConversation?.id === conv.id;
            return (
              <button
                key={conv.id}
                onClick={() => onSelect(conv)}
                className={`w-full text-left p-3.5 transition flex flex-col gap-1.5 ${
                  isActive ? 'bg-purple-500/10 border-l-2 border-purple-500' : 'hover:bg-slate-800/40'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="font-medium text-slate-200 text-xs truncate">
                    #{conv.id} — {conv.userFullName}
                  </span>
                  {getStatusBadge(conv.status)}
                </div>

                <p className="text-[11px] text-slate-400 truncate">
                  {conv.lastMessage?.content || 'No messages yet'}
                </p>

                <span className="text-[10px] text-slate-500">
                  {new Date(conv.updatedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </span>
              </button>
            );
          })
        )}
      </div>
    </div>
  );
};

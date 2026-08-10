import React from 'react';
import { Conversation, Message } from '../../types/chat';
import { MessageList } from './MessageList';
import { MessageInput } from './MessageInput';
import { ConnectionStatus } from '../../services/websocketService';
import { Bot, Lock, Radio } from 'lucide-react';

interface ChatWindowProps {
  conversation: Conversation | null;
  messages: Message[];
  currentUserId?: number;
  isLoading: boolean;
  wsStatus: ConnectionStatus;
  onSendMessage: (content: string) => void;
  onCloseConversation: () => void;
}

export const ChatWindow: React.FC<ChatWindowProps> = ({
  conversation,
  messages,
  currentUserId,
  isLoading,
  wsStatus,
  onSendMessage,
  onCloseConversation,
}) => {
  if (!conversation) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center p-6 text-slate-500 bg-[#0b0f19]">
        <Bot className="w-12 h-12 text-slate-700 mb-3" />
        <p className="font-medium text-slate-400">Select a conversation or start a new one</p>
      </div>
    );
  }

  const isClosed = conversation.status === 'CLOSED' || conversation.status === 'RESOLVED';

  const getWsBadge = () => {
    switch (wsStatus) {
      case 'CONNECTED':
        return <span className="inline-flex items-center gap-1 text-[10px] text-emerald-400 font-medium px-2 py-0.5 rounded-full bg-emerald-500/10 border border-emerald-500/20"><Radio className="w-3 h-3 animate-pulse" /> Live STOMP</span>;
      case 'CONNECTING':
        return <span className="inline-flex items-center gap-1 text-[10px] text-amber-400 font-medium px-2 py-0.5 rounded-full bg-amber-500/10 border border-amber-500/20">Connecting...</span>;
      default:
        return <span className="inline-flex items-center gap-1 text-[10px] text-slate-400 font-medium px-2 py-0.5 rounded-full bg-slate-800 border border-slate-700">REST Fallback</span>;
    }
  };

  return (
    <div className="flex-1 flex flex-col h-full bg-[#0b0f19]">
      <div className="p-4 border-b border-slate-800 bg-slate-900/80 backdrop-blur flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-lg bg-purple-500/10 border border-purple-500/20 text-purple-400 flex items-center justify-center">
            <Bot className="w-5 h-5" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h3 className="font-semibold text-white text-xs">Conversation #{conversation.id}</h3>
              {getWsBadge()}
            </div>
            <p className="text-[11px] text-slate-400">Participant: {conversation.userFullName}</p>
          </div>
        </div>

        {!isClosed && (
          <button
            onClick={onCloseConversation}
            className="px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 text-xs font-medium transition flex items-center gap-1"
          >
            <Lock className="w-3.5 h-3.5 text-slate-400" />
            <span>Close Conversation</span>
          </button>
        )}
      </div>

      <MessageList messages={messages} currentUserId={currentUserId} isLoading={isLoading} />

      <MessageInput onSend={onSendMessage} disabled={isClosed} />
    </div>
  );
};

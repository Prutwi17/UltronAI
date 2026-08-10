import React from 'react';
import { Message } from '../../types/chat';
import { Bot, User, ShieldAlert } from 'lucide-react';

interface MessageBubbleProps {
  message: Message;
  currentUserId?: number;
}

export const MessageBubble: React.FC<MessageBubbleProps> = ({ message, currentUserId }) => {
  const isSelf = message.senderId === currentUserId;
  const isSystem = message.senderType === 'SYSTEM';
  const isAssistant = message.senderType === 'ASSISTANT' || message.senderType === 'AGENT';

  if (isSystem) {
    return (
      <div className="flex items-center justify-center my-3">
        <div className="px-3 py-1 rounded-full bg-slate-800/80 border border-slate-700 text-slate-400 text-[11px] flex items-center gap-1.5">
          <ShieldAlert className="w-3.5 h-3.5 text-amber-400" />
          <span>{message.content}</span>
        </div>
      </div>
    );
  }

  return (
    <div className={`flex gap-3 my-2 ${isSelf ? 'justify-end' : 'justify-start'}`}>
      {!isSelf && (
        <div className="w-7 h-7 rounded-full bg-purple-500/20 border border-purple-500/30 flex items-center justify-center text-purple-400 shrink-0 text-xs">
          {isAssistant ? <Bot className="w-4 h-4" /> : <User className="w-4 h-4" />}
        </div>
      )}

      <div className={`max-w-[70%] space-y-1 ${isSelf ? 'items-end' : 'items-start'}`}>
        <div
          className={`p-3 rounded-2xl text-xs leading-relaxed ${
            isSelf
              ? 'bg-purple-600 text-white rounded-br-none shadow-md shadow-purple-600/10'
              : 'bg-slate-800 border border-slate-700 text-slate-100 rounded-bl-none'
          }`}
        >
          {message.content}
        </div>

        <div className={`text-[10px] text-slate-500 px-1 ${isSelf ? 'text-right' : 'text-left'}`}>
          {new Date(message.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </div>
      </div>
    </div>
  );
};

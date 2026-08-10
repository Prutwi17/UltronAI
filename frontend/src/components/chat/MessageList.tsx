import React, { useEffect, useRef } from 'react';
import { Message } from '../../types/chat';
import { MessageBubble } from './MessageBubble';

interface MessageListProps {
  messages: Message[];
  currentUserId?: number;
  isLoading: boolean;
}

export const MessageList: React.FC<MessageListProps> = ({ messages, currentUserId, isLoading }) => {
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  if (isLoading) {
    return (
      <div className="flex-1 flex items-center justify-center p-6 text-slate-500 text-sm">
        <span className="animate-pulse">Loading message history...</span>
      </div>
    );
  }

  if (messages.length === 0) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center p-6 text-slate-500 text-sm space-y-2">
        <p>No messages in this conversation yet.</p>
        <p className="text-xs text-slate-600">Send a message below to start the conversation.</p>
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-y-auto p-4 space-y-2">
      {messages.map((msg) => (
        <MessageBubble key={msg.id} message={msg} currentUserId={currentUserId} />
      ))}
      <div ref={messagesEndRef} />
    </div>
  );
};

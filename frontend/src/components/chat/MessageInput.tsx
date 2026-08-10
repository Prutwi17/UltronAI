import React, { useState } from 'react';
import { Send } from 'lucide-react';

interface MessageInputProps {
  onSend: (content: string) => void;
  disabled?: boolean;
}

export const MessageInput: React.FC<MessageInputProps> = ({ onSend, disabled }) => {
  const [content, setContent] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (content.trim() && !disabled) {
      onSend(content.trim());
      setContent('');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="p-4 border-t border-slate-800 bg-slate-900/80 backdrop-blur flex gap-2 items-center">
      <input
        type="text"
        disabled={disabled}
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder={disabled ? 'Conversation closed' : 'Type a message...'}
        className="flex-1 px-4 py-2.5 rounded-xl bg-slate-800/80 border border-slate-700 text-white placeholder-slate-500 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 text-xs disabled:opacity-50"
      />
      <button
        type="submit"
        disabled={disabled || !content.trim()}
        className="p-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 active:bg-purple-700 disabled:opacity-50 text-white transition flex items-center justify-center shadow-lg shadow-purple-600/20"
      >
        <Send className="w-4 h-4" />
      </button>
    </form>
  );
};

import { create } from 'zustand';
import { Conversation, Message } from '../types/chat';
import { chatService } from '../services/chatService';
import { websocketService, ConnectionStatus } from '../services/websocketService';

interface ChatState {
  conversations: Conversation[];
  activeConversation: Conversation | null;
  messages: Message[];
  isLoadingConversations: boolean;
  isLoadingMessages: boolean;
  wsStatus: ConnectionStatus;
  error: string | null;

  fetchConversations: () => Promise<void>;
  createConversation: (initialMessage?: string) => Promise<Conversation>;
  selectConversation: (conversation: Conversation) => Promise<void>;
  sendMessage: (content: string) => Promise<void>;
  receiveMessage: (message: Message) => void;
  closeActiveConversation: () => Promise<void>;
  connectWebSocket: () => void;
  disconnectWebSocket: () => void;
  clearError: () => void;
}

export const useChatStore = create<ChatState>((set, get) => ({
  conversations: [],
  activeConversation: null,
  messages: [],
  isLoadingConversations: false,
  isLoadingMessages: false,
  wsStatus: 'DISCONNECTED',
  error: null,

  fetchConversations: async () => {
    set({ isLoadingConversations: true, error: null });
    try {
      const page = await chatService.listConversations(0, 50);
      set({ conversations: page.content, isLoadingConversations: false });
      
      // Auto select first conversation if available and none active
      if (page.content.length > 0 && !get().activeConversation) {
        get().selectConversation(page.content[0]);
      }
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to load conversations', isLoadingConversations: false });
    }
  },

  createConversation: async (initialMessage) => {
    set({ error: null });
    try {
      const conversation = await chatService.createConversation({ channel: 'WEB', initialMessage });
      const currentList = get().conversations;
      set({ conversations: [conversation, ...currentList] });
      await get().selectConversation(conversation);
      return conversation;
    } catch (err: any) {
      const msg = err.response?.data?.message || 'Failed to create conversation';
      set({ error: msg });
      throw new Error(msg);
    }
  },

  selectConversation: async (conversation) => {
    set({ activeConversation: conversation, isLoadingMessages: true, messages: [] });
    try {
      const page = await chatService.getMessages(conversation.id, 0, 100);
      set({ messages: page.content, isLoadingMessages: false });

      // Subscribe to WebSocket channel
      websocketService.subscribeToConversation(conversation.tenantId, conversation.id, (message) => {
        get().receiveMessage(message);
      });
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to load conversation history', isLoadingMessages: false });
    }
  },

  sendMessage: async (content) => {
    const active = get().activeConversation;
    if (!active) return;

    try {
      // Send via REST API fallback/persist
      const message = await chatService.sendMessage(active.id, {
        conversationId: active.id,
        content,
        senderType: 'USER',
        contentType: 'TEXT',
      });
      
      get().receiveMessage(message);
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to send message' });
    }
  },

  receiveMessage: (message) => {
    const currentMessages = get().messages;
    if (!currentMessages.some((m) => m.id === message.id)) {
      set({ messages: [...currentMessages, message] });
    }

    // Update conversation last message in list
    const conversations = get().conversations.map((c) =>
      c.id === message.conversationId ? { ...c, lastMessage: message, updatedAt: message.createdAt } : c
    );
    set({ conversations });
  },

  closeActiveConversation: async () => {
    const active = get().activeConversation;
    if (!active) return;

    try {
      const updated = await chatService.closeConversation(active.id);
      const conversations = get().conversations.map((c) => (c.id === updated.id ? updated : c));
      set({ activeConversation: updated, conversations });
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to close conversation' });
    }
  },

  connectWebSocket: () => {
    websocketService.onStatusChange((status) => {
      set({ wsStatus: status });
    });

    websocketService.connect();
  },

  disconnectWebSocket: () => {
    websocketService.disconnect();
    set({ wsStatus: 'DISCONNECTED' });
  },

  clearError: () => set({ error: null }),
}));

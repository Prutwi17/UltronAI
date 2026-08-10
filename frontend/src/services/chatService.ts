import { api } from './api';
import { Conversation, CreateConversationRequest, Message, PageResponse, SendMessageRequest } from '../types/chat';

export const chatService = {
  async createConversation(data: CreateConversationRequest = {}): Promise<Conversation> {
    const response = await api.post<Conversation>('/conversations', data);
    return response.data;
  },

  async listConversations(page = 0, size = 20): Promise<PageResponse<Conversation>> {
    const response = await api.get<PageResponse<Conversation>>('/conversations', {
      params: { page, size },
    });
    return response.data;
  },

  async getConversation(id: number): Promise<Conversation> {
    const response = await api.get<Conversation>(`/conversations/${id}`);
    return response.data;
  },

  async closeConversation(id: number): Promise<Conversation> {
    const response = await api.put<Conversation>(`/conversations/${id}/close`);
    return response.data;
  },

  async getMessages(conversationId: number, page = 0, size = 50): Promise<PageResponse<Message>> {
    const response = await api.get<PageResponse<Message>>(`/conversations/${conversationId}/messages`, {
      params: { page, size },
    });
    return response.data;
  },

  async sendMessage(conversationId: number, data: SendMessageRequest): Promise<Message> {
    const response = await api.post<Message>(`/conversations/${conversationId}/messages`, data);
    return response.data;
  },
};

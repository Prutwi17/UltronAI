export type ConversationStatus = 'ACTIVE' | 'WAITING' | 'RESOLVED' | 'CLOSED';
export type SenderType = 'USER' | 'ASSISTANT' | 'SYSTEM' | 'AGENT';
export type MessageType = 'TEXT' | 'IMAGE' | 'JSON';

export interface Message {
  id: number;
  tenantId: number;
  conversationId: number;
  senderType: SenderType;
  senderId: number;
  content: string;
  contentType: MessageType;
  createdAt: string;
}

export interface Conversation {
  id: number;
  tenantId: number;
  userId: number;
  userFullName: string;
  status: ConversationStatus;
  channel: string;
  lastMessage: Message | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateConversationRequest {
  channel?: string;
  initialMessage?: string;
}

export interface SendMessageRequest {
  conversationId: number;
  content: string;
  senderType?: SenderType;
  contentType?: MessageType;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

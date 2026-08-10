import { Client, IMessage } from '@stomp/stompjs';
import { Message } from '../types/chat';

export type ConnectionStatus = 'CONNECTING' | 'CONNECTED' | 'DISCONNECTED' | 'RECONNECTING' | 'ERROR';

type MessageCallback = (message: Message) => void;
type StatusCallback = (status: ConnectionStatus) => void;

class WebSocketService {
  private client: Client | null = null;
  private currentSubscription: any = null;
  private statusListeners: Set<StatusCallback> = new Set();

  public connect(): void {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      this.notifyStatus('DISCONNECTED');
      return;
    }

    if (this.client?.active) {
      return;
    }

    this.notifyStatus('CONNECTING');

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.hostname === 'localhost' ? 'localhost:8080' : window.location.host;
    const wsUrl = `${protocol}//${host}/ws/websocket`;

    this.client = new Client({
      brokerURL: wsUrl,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        this.notifyStatus('CONNECTED');
      },
      onDisconnect: () => {
        this.notifyStatus('DISCONNECTED');
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message'], frame.body);
        this.notifyStatus('ERROR');
      },
      onWebSocketClose: () => {
        this.notifyStatus('DISCONNECTED');
      },
    });

    this.client.activate();
  }

  public subscribeToConversation(tenantId: number, conversationId: number, callback: MessageCallback): void {
    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe();
      this.currentSubscription = null;
    }

    if (!this.client || !this.client.connected) {
      return;
    }

    const topic = `/topic/tenants/${tenantId}/conversations/${conversationId}`;
    this.currentSubscription = this.client.subscribe(topic, (message: IMessage) => {
      try {
        const parsedMessage: Message = JSON.parse(message.body);
        callback(parsedMessage);
      } catch (err) {
        console.error('Failed to parse STOMP message payload', err);
      }
    });
  }

  public sendMessage(conversationId: number, content: string): void {
    if (!this.client || !this.client.connected) {
      throw new Error('WebSocket client is not connected');
    }

    this.client.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify({
        conversationId,
        content,
        senderType: 'USER',
        contentType: 'TEXT',
      }),
    });
  }

  public disconnect(): void {
    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe();
      this.currentSubscription = null;
    }
    if (this.client) {
      this.client.deactivate();
      this.client = null;
    }
    this.notifyStatus('DISCONNECTED');
  }

  public onStatusChange(listener: StatusCallback): () => void {
    this.statusListeners.add(listener);
    return () => this.statusListeners.delete(listener);
  }

  private notifyStatus(status: ConnectionStatus): void {
    this.statusListeners.forEach((listener) => listener(status));
  }
}

export const websocketService = new WebSocketService();

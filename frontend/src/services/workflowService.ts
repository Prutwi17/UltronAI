import { api } from './api';
import {
  Workflow,
  WorkflowNode,
  WorkflowEdge,
  CreateWorkflowRequest,
  CreateWorkflowNodeRequest,
  CreateWorkflowEdgeRequest,
  WorkflowValidationResponse
} from '../types/workflow';

export const workflowService = {
  async listWorkflows(): Promise<Workflow[]> {
    const response = await api.get<Workflow[]>('/workflows');
    return response.data;
  },

  async getWorkflow(id: number): Promise<Workflow> {
    const response = await api.get<Workflow>(`/workflows/${id}`);
    return response.data;
  },

  async createWorkflow(data: CreateWorkflowRequest): Promise<Workflow> {
    const response = await api.post<Workflow>('/workflows', data);
    return response.data;
  },

  async addNode(workflowId: number, data: CreateWorkflowNodeRequest): Promise<WorkflowNode> {
    const response = await api.post<WorkflowNode>(`/workflows/${workflowId}/nodes`, data);
    return response.data;
  },

  async addEdge(workflowId: number, data: CreateWorkflowEdgeRequest): Promise<WorkflowEdge> {
    const response = await api.post<WorkflowEdge>(`/workflows/${workflowId}/edges`, data);
    return response.data;
  },

  async validateWorkflow(workflowId: number): Promise<WorkflowValidationResponse> {
    const response = await api.post<WorkflowValidationResponse>(`/workflows/${workflowId}/validate`);
    return response.data;
  },

  async publishWorkflow(workflowId: number): Promise<Workflow> {
    const response = await api.post<Workflow>(`/workflows/${workflowId}/publish`);
    return response.data;
  }
};

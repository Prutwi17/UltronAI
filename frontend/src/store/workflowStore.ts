import { create } from 'zustand';
import { Workflow, WorkflowNode, WorkflowEdge, CreateWorkflowRequest, CreateWorkflowNodeRequest, CreateWorkflowEdgeRequest } from '../types/workflow';
import { workflowService } from '../services/workflowService';

interface WorkflowState {
  workflows: Workflow[];
  selectedWorkflow: Workflow | null;
  isLoading: boolean;
  error: string | null;
  fetchWorkflows: () => Promise<void>;
  selectWorkflow: (id: number) => Promise<void>;
  createWorkflow: (data: CreateWorkflowRequest) => Promise<Workflow>;
  addNode: (workflowId: number, data: CreateWorkflowNodeRequest) => Promise<WorkflowNode>;
  addEdge: (workflowId: number, data: CreateWorkflowEdgeRequest) => Promise<WorkflowEdge>;
  publishWorkflow: (workflowId: number) => Promise<Workflow>;
}

export const useWorkflowStore = create<WorkflowState>((set, get) => ({
  workflows: [],
  selectedWorkflow: null,
  isLoading: false,
  error: null,

  fetchWorkflows: async () => {
    set({ isLoading: true, error: null });
    try {
      const list = await workflowService.listWorkflows();
      set({ workflows: list, isLoading: false });
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to fetch workflows', isLoading: false });
    }
  },

  selectWorkflow: async (id: number) => {
    set({ isLoading: true, error: null });
    try {
      const workflow = await workflowService.getWorkflow(id);
      set({ selectedWorkflow: workflow, isLoading: false });
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to fetch workflow details', isLoading: false });
    }
  },

  createWorkflow: async (data: CreateWorkflowRequest) => {
    set({ isLoading: true, error: null });
    try {
      const created = await workflowService.createWorkflow(data);
      set(state => ({
        workflows: [...state.workflows, created],
        selectedWorkflow: created,
        isLoading: false
      }));
      return created;
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to create workflow', isLoading: false });
      throw err;
    }
  },

  addNode: async (workflowId: number, data: CreateWorkflowNodeRequest) => {
    try {
      const node = await workflowService.addNode(workflowId, data);
      await get().selectWorkflow(workflowId);
      return node;
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to add node' });
      throw err;
    }
  },

  addEdge: async (workflowId: number, data: CreateWorkflowEdgeRequest) => {
    try {
      const edge = await workflowService.addEdge(workflowId, data);
      await get().selectWorkflow(workflowId);
      return edge;
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to add edge' });
      throw err;
    }
  },

  publishWorkflow: async (workflowId: number) => {
    set({ isLoading: true, error: null });
    try {
      const updated = await workflowService.publishWorkflow(workflowId);
      set(state => ({
        workflows: state.workflows.map(w => w.id === workflowId ? updated : w),
        selectedWorkflow: updated,
        isLoading: false
      }));
      return updated;
    } catch (err: any) {
      set({ error: err.response?.data?.message || 'Failed to publish workflow', isLoading: false });
      throw err;
    }
  }
}));

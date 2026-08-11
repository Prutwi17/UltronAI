export type WorkflowStatus = 'DRAFT' | 'PUBLISHED' | 'ACTIVE' | 'ARCHIVED';
export type NodeType = 'START' | 'MESSAGE' | 'CONDITION' | 'END';

export interface WorkflowNode {
  id: number;
  workflowId: number;
  nodeType: NodeType;
  name: string;
  configJson?: string;
  positionX: number;
  positionY: number;
  createdAt: string;
}

export interface WorkflowEdge {
  id: number;
  workflowId: number;
  sourceNodeId: number;
  targetNodeId: number;
  conditionExpression?: string;
  label?: string;
  createdAt: string;
}

export interface Workflow {
  id: number;
  tenantId: number;
  name: string;
  description?: string;
  triggerIntent?: string;
  status: WorkflowStatus;
  version: number;
  nodes?: WorkflowNode[];
  edges?: WorkflowEdge[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateWorkflowRequest {
  name: string;
  description?: string;
  triggerIntent?: string;
}

export interface CreateWorkflowNodeRequest {
  nodeType: NodeType;
  name: string;
  configJson?: string;
  positionX?: number;
  positionY?: number;
}

export interface CreateWorkflowEdgeRequest {
  sourceNodeId: number;
  targetNodeId: number;
  conditionExpression?: string;
  label?: string;
}

export interface WorkflowValidationResponse {
  valid: boolean;
  errors: string[];
}

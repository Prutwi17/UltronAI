import React, { useEffect } from 'react';
import { useWorkflowStore } from '../store/workflowStore';
import { WorkflowList } from '../components/workflow/WorkflowList';
import { WorkflowEditor } from '../components/workflow/WorkflowEditor';
import { NodeType } from '../types/workflow';

export const WorkflowPage: React.FC = () => {
  const {
    workflows,
    selectedWorkflow,
    fetchWorkflows,
    selectWorkflow,
    createWorkflow,
    addNode,
    addEdge,
    publishWorkflow
  } = useWorkflowStore();

  useEffect(() => {
    fetchWorkflows();
  }, [fetchWorkflows]);

  const handleCreate = async (name: string, description: string, triggerIntent: string) => {
    await createWorkflow({ name, description, triggerIntent });
  };

  const handleAddNode = async (nodeType: NodeType, name: string, configJson?: string) => {
    if (!selectedWorkflow) return;
    await addNode(selectedWorkflow.id, { nodeType, name, configJson });
  };

  const handleAddEdge = async (sourceNodeId: number, targetNodeId: number, conditionExpression?: string, label?: string) => {
    if (!selectedWorkflow) return;
    await addEdge(selectedWorkflow.id, { sourceNodeId, targetNodeId, conditionExpression, label });
  };

  const handlePublish = async () => {
    if (!selectedWorkflow) return;
    try {
      await publishWorkflow(selectedWorkflow.id);
      alert('Workflow successfully validated and published!');
    } catch (err: any) {
      alert(`Publishing failed: ${err.message || 'Graph validation errors'}`);
    }
  };

  return (
    <div className="flex h-screen bg-slate-950 text-slate-100 overflow-hidden">
      <WorkflowList
        workflows={workflows}
        selectedId={selectedWorkflow?.id || null}
        onSelect={(id) => selectWorkflow(id)}
        onCreate={handleCreate}
      />

      {selectedWorkflow ? (
        <WorkflowEditor
          workflow={selectedWorkflow}
          onAddNode={handleAddNode}
          onAddEdge={handleAddEdge}
          onPublish={handlePublish}
        />
      ) : (
        <div className="flex-1 flex flex-col items-center justify-center text-slate-500">
          <p className="text-sm">Select a workflow or create a new one to start building.</p>
        </div>
      )}
    </div>
  );
};

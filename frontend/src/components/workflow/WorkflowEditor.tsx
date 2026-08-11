import React, { useState } from 'react';
import { Workflow, NodeType } from '../../types/workflow';

interface Props {
  workflow: Workflow;
  onAddNode: (nodeType: NodeType, name: string, configJson?: string) => void;
  onAddEdge: (sourceNodeId: number, targetNodeId: number, conditionExpression?: string, label?: string) => void;
  onPublish: () => void;
}

export const WorkflowEditor: React.FC<Props> = ({ workflow, onAddNode, onAddEdge, onPublish }) => {
  const [nodeType, setNodeType] = useState<NodeType>('MESSAGE');
  const [nodeName, setNodeName] = useState('');
  const [messageConfig, setMessageConfig] = useState('');
  const [apiMethod, setApiMethod] = useState('GET');
  const [apiUrl, setApiUrl] = useState('');

  const [sourceId, setSourceId] = useState<number | ''>('');
  const [targetId, setTargetId] = useState<number | ''>('');
  const [conditionExpr, setConditionExpr] = useState('');

  const nodes = workflow.nodes || [];
  const edges = workflow.edges || [];

  const handleAddNodeSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!nodeName.trim()) return;
    let configJson: string | undefined;
    if (nodeType === 'MESSAGE') {
      configJson = JSON.stringify({ message: messageConfig });
    } else if (nodeType === 'API_CALL') {
      configJson = JSON.stringify({ url: apiUrl, method: apiMethod });
    }
    onAddNode(nodeType, nodeName, configJson);
    setNodeName('');
    setMessageConfig('');
    setApiUrl('');
  };

  const handleAddEdgeSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!sourceId || !targetId) return;
    onAddEdge(Number(sourceId), Number(targetId), conditionExpr || undefined, undefined);
    setSourceId('');
    setTargetId('');
    setConditionExpr('');
  };

  return (
    <div className="flex-1 bg-slate-950 flex flex-col h-full overflow-hidden">
      {/* Header */}
      <div className="p-4 bg-slate-900 border-b border-slate-800 flex justify-between items-center">
        <div>
          <div className="flex items-center space-x-3">
            <h2 className="text-xl font-bold text-slate-100">{workflow.name}</h2>
            <span className="text-xs px-2.5 py-0.5 rounded bg-indigo-500/20 text-indigo-300 font-mono">
              v{workflow.version}
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Trigger Intent: <span className="font-mono text-indigo-400">{workflow.triggerIntent || 'None'}</span>
          </p>
        </div>

        <button
          onClick={onPublish}
          className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded-lg text-xs font-semibold transition shadow-lg shadow-emerald-900/30"
        >
          Publish Workflow
        </button>
      </div>

      {/* Editor Main Content */}
      <div className="flex-1 flex overflow-hidden">
        {/* Graph Canvas Visualizer */}
        <div className="flex-1 p-6 overflow-auto space-y-6">
          <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-wider">Workflow Nodes</h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {nodes.map((node) => (
              <div
                key={node.id}
                className="bg-slate-900 border border-slate-800 rounded-xl p-4 shadow-md relative hover:border-slate-700 transition"
              >
                <div className="flex justify-between items-start mb-2">
                  <span className="text-xs font-mono font-semibold px-2 py-0.5 rounded bg-slate-800 text-indigo-300">
                    #{node.id} {node.nodeType}
                  </span>
                  <span className="text-xs text-slate-400 font-medium">{node.name}</span>
                </div>

                {node.configJson && (
                  <pre className="mt-2 text-xs bg-slate-950 p-2.5 rounded border border-slate-800/60 text-slate-300 font-mono whitespace-pre-wrap">
                    {node.configJson}
                  </pre>
                )}
              </div>
            ))}
          </div>

          <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-wider pt-4">Workflow Edges</h3>
          <div className="space-y-2">
            {edges.map((edge) => (
              <div key={edge.id} className="bg-slate-900 border border-slate-800 rounded-lg p-3 text-xs text-slate-300 flex items-center justify-between font-mono">
                <div>
                  Node #{edge.sourceNodeId} <span className="text-indigo-400 font-bold">➔</span> Node #{edge.targetNodeId}
                </div>
                {edge.conditionExpression && (
                  <span className="bg-slate-950 px-2 py-1 rounded text-amber-300 border border-slate-800">
                    if ({edge.conditionExpression})
                  </span>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Sidebar Configuration Form */}
        <div className="w-80 bg-slate-900 border-l border-slate-800 p-4 overflow-y-auto space-y-6">
          {/* Add Node Form */}
          <form onSubmit={handleAddNodeSubmit} className="space-y-3 bg-slate-950 p-4 rounded-xl border border-slate-800">
            <h4 className="text-sm font-bold text-slate-200">Add Node</h4>

            <div>
              <label className="block text-[11px] text-slate-400 mb-1">Node Type</label>
              <select
                value={nodeType}
                onChange={(e) => setNodeType(e.target.value as NodeType)}
                className="w-full bg-slate-900 border border-slate-800 rounded px-2.5 py-1.5 text-xs text-slate-200"
              >
                <option value="START">START</option>
                <option value="MESSAGE">MESSAGE</option>
                <option value="CONDITION">CONDITION</option>
                <option value="API_CALL">API_CALL</option>
                <option value="END">END</option>
              </select>
            </div>

            <div>
              <label className="block text-[11px] text-slate-400 mb-1">Name</label>
              <input
                type="text"
                required
                value={nodeName}
                onChange={(e) => setNodeName(e.target.value)}
                placeholder="Node name"
                className="w-full bg-slate-900 border border-slate-800 rounded px-2.5 py-1.5 text-xs text-slate-200"
              />
            </div>

            {nodeType === 'MESSAGE' && (
              <div>
                <label className="block text-[11px] text-slate-400 mb-1">Message Text</label>
                <textarea
                  value={messageConfig}
                  onChange={(e) => setMessageConfig(e.target.value)}
                  placeholder="e.g. Order #${entities.order_id} is in transit!"
                  className="w-full bg-slate-900 border border-slate-800 rounded px-2.5 py-1.5 text-xs text-slate-200 font-mono"
                  rows={3}
                />
              </div>
            )}

            {nodeType === 'API_CALL' && (
              <div className="space-y-2">
                <div>
                  <label className="block text-[11px] text-slate-400 mb-1">HTTP Method</label>
                  <select
                    value={apiMethod}
                    onChange={(e) => setApiMethod(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-800 rounded px-2.5 py-1.5 text-xs text-slate-200"
                  >
                    <option value="GET">GET</option>
                    <option value="POST">POST</option>
                    <option value="PUT">PUT</option>
                    <option value="DELETE">DELETE</option>
                  </select>
                </div>
                <div>
                  <label className="block text-[11px] text-slate-400 mb-1">Target URL</label>
                  <input
                    type="text"
                    required
                    value={apiUrl}
                    onChange={(e) => setApiUrl(e.target.value)}
                    placeholder="https://api.external.com/orders/${entities.order_id}"
                    className="w-full bg-slate-900 border border-slate-800 rounded px-2.5 py-1.5 text-xs text-slate-200 font-mono"
                  />
                </div>
              </div>
            )}

            <button
              type="submit"
              className="w-full py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded text-xs font-semibold transition"
            >
              + Add Node
            </button>
          </form>

          {/* Add Edge Form */}
          <form onSubmit={handleAddEdgeSubmit} className="space-y-3 bg-slate-950 p-4 rounded-xl border border-slate-800">
            <h4 className="text-sm font-bold text-slate-200">Add Edge (Transition)</h4>

            <div>
              <label className="block text-[11px] text-slate-400 mb-1">Source Node ID</label>
              <select
                value={sourceId}
                onChange={(e) => setSourceId(e.target.value ? Number(e.target.value) : '')}
                className="w-full bg-slate-900 border border-slate-800 rounded px-2.5 py-1.5 text-xs text-slate-200"
              >
                <option value="">Select Source</option>
                {nodes.map((n) => (
                  <option key={n.id} value={n.id}>
                    #{n.id} ({n.name})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-[11px] text-slate-400 mb-1">Target Node ID</label>
              <select
                value={targetId}
                onChange={(e) => setTargetId(e.target.value ? Number(e.target.value) : '')}
                className="w-full bg-slate-900 border border-slate-800 rounded px-2.5 py-1.5 text-xs text-slate-200"
              >
                <option value="">Select Target</option>
                {nodes.map((n) => (
                  <option key={n.id} value={n.id}>
                    #{n.id} ({n.name})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-[11px] text-slate-400 mb-1">Condition Expr (Optional)</label>
              <input
                type="text"
                value={conditionExpr}
                onChange={(e) => setConditionExpr(e.target.value)}
                placeholder="e.g. order_id != null"
                className="w-full bg-slate-900 border border-slate-800 rounded px-2.5 py-1.5 text-xs text-slate-200 font-mono"
              />
            </div>

            <button
              type="submit"
              className="w-full py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded text-xs font-semibold transition"
            >
              + Connect Edge
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

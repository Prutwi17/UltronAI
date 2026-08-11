import React, { useState } from 'react';
import { Workflow } from '../../types/workflow';

interface Props {
  workflows: Workflow[];
  selectedId: number | null;
  onSelect: (id: number) => void;
  onCreate: (name: string, description: string, triggerIntent: string) => void;
}

export const WorkflowList: React.FC<Props> = ({ workflows, selectedId, onSelect, onCreate }) => {
  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [triggerIntent, setTriggerIntent] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    onCreate(name, description, triggerIntent);
    setName('');
    setDescription('');
    setTriggerIntent('');
    setShowModal(false);
  };

  return (
    <div className="w-80 bg-slate-900 border-r border-slate-800 flex flex-col h-full">
      <div className="p-4 border-b border-slate-800 flex justify-between items-center">
        <h2 className="text-lg font-semibold text-slate-100">Workflows</h2>
        <button
          onClick={() => setShowModal(true)}
          className="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-md text-xs font-medium transition"
        >
          + New Flow
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-3 space-y-2">
        {workflows.map((w) => {
          const isSelected = w.id === selectedId;
          return (
            <div
              key={w.id}
              onClick={() => onSelect(w.id)}
              className={`p-3 rounded-lg cursor-pointer transition border ${
                isSelected
                  ? 'bg-indigo-900/40 border-indigo-500/50 text-indigo-200'
                  : 'bg-slate-850 hover:bg-slate-800 border-slate-800 text-slate-300'
              }`}
            >
              <div className="flex justify-between items-start mb-1">
                <h3 className="font-semibold text-sm text-slate-100">{w.name}</h3>
                <span
                  className={`text-[10px] px-2 py-0.5 rounded font-mono font-medium ${
                    w.status === 'PUBLISHED'
                      ? 'bg-emerald-500/20 text-emerald-300'
                      : 'bg-amber-500/20 text-amber-300'
                  }`}
                >
                  {w.status}
                </span>
              </div>
              <p className="text-xs text-slate-400 line-clamp-1">{w.description || 'No description'}</p>
              {w.triggerIntent && (
                <div className="mt-2 text-[11px] text-indigo-400 font-mono">
                  Trigger: {w.triggerIntent}
                </div>
              )}
            </div>
          );
        })}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 w-full max-w-md shadow-2xl">
            <h3 className="text-lg font-bold text-slate-100 mb-4">Create New Workflow</h3>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">Workflow Name</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Order Tracking Automation"
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">Description</label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Describe workflow purpose..."
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500"
                  rows={3}
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-slate-400 mb-1">Trigger Intent</label>
                <input
                  type="text"
                  value={triggerIntent}
                  onChange={(e) => setTriggerIntent(e.target.value)}
                  placeholder="e.g. ORDER_TRACKING"
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-200 focus:outline-none focus:border-indigo-500 font-mono"
                />
              </div>

              <div className="flex justify-end space-x-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 text-xs font-medium text-slate-400 hover:text-slate-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-xs font-medium transition"
                >
                  Create Flow
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

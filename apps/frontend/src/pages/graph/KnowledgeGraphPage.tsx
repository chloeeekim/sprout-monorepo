import { useState, useEffect, useCallback } from 'react';
import ReactFlow, {
    Controls,
    Background,
    applyEdgeChanges,
    applyNodeChanges,
    addEdge,
    type Node,
    type Edge,
    type NodeChange,
    type EdgeChange,
    type Connection,
    type NodeMouseHandler,
    type EdgeMouseHandler,
} from 'reactflow';

import 'reactflow/dist/style.css';
import {getAllNoteLinks} from "@/lib/noteLinkApi";
import {getAllNotes} from "@/lib/noteApi";
import type {
  NoteLinkResponse,
  NoteSimpleResponse,
} from '@sprout/shared-types';
import dagre from "dagre";
import * as d3 from "d3-force";
import MainLayout from "@/components/layout/MainLayout";
import NotePreviewPanel from "@/components/ui/NotePreviewPanel";

// Dagre 레이아웃 계산 함수
const getLayoutedElements = (nodes: Node[], edges: Edge[], direction = "TB") => {
    const dagreGraph = new dagre.graphlib.Graph();
    dagreGraph.setDefaultEdgeLabel(() => ({}));
    dagreGraph.setGraph({ rankdir: direction });

    const nodeWidth = 172;
    const nodeHeight = 36;

    nodes.forEach((node) => {
        dagreGraph.setNode(node.id, { width: nodeWidth, height: nodeHeight });
    });

    edges.forEach((edge) => {
        dagreGraph.setEdge(edge.source, edge.target);
    });

    dagre.layout(dagreGraph);

    nodes.forEach((node) => {
        const nodeWithPosition = dagreGraph.node(node.id);
        const x = nodeWithPosition.x - nodeWidth / 2;
        const y = nodeWithPosition.y - nodeHeight / 2;

        node.position = {x, y};
        (node as any).fx = x;
        (node as any).fy = y;
    });

    return { layoutedNodes: nodes, layoutedEdges: edges };
};

function applyD3Force(nodes: Node[]) {
    const simulation = d3
        .forceSimulation(nodes as any)
        .force("charge", d3.forceManyBody().strength(-70))
        .force("center", d3.forceCenter(0, 0))
        .force("collision", d3.forceCollide().radius(60))
        .stop();

    for (let i = 0 ; i < 100 ; i++) simulation.tick();

    return nodes.map((node, i) => ({
            ...node,
            position: {
                x: (nodes[i] as any).x || 0,
                y: (nodes[i] as any).y || 0
            }
        }
    ));
}

function KnowledgeGraphPage() {
    const [allNotes, setAllNotes] = useState<NoteSimpleResponse[]>([]);
    const [nodes, setNodes] = useState<Node[]>([]);
    const [edges, setEdges] = useState<Edge[]>([]);
    const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null);
    const [selectedEdgeId, setSelectedEdgeId] = useState<string | null>(null);

    const selectedNote = allNotes.find(note => note.id === selectedNodeId);

    useEffect(() => {
        const fetchGraphData = async () => {
            try {
                const [notes, links] = await Promise.all([
                    getAllNotes(),
                    getAllNoteLinks()
                ]);

                setAllNotes(notes);

                const initialNodes: Node[] = notes.map((note: NoteSimpleResponse, _index: number) => ({
                    id: note.id,
                    data: {label: note.title},
                    position: { x: 0, y: 0 }, // 초기 위치는 0, 0으로 설정
                    style: { opacity: 1 }
                }));

                const initialEdges: Edge[] = links.map((link: NoteLinkResponse) => ({
                    id: `e-${link.source}-${link.target}`,
                    source: link.source,
                    target: link.target,
                    animated: true,
                    style: { opacity: 1 }
                }));

                const connectedNodeIds = new Set(
                    initialEdges.flatMap((e) => [e.source, e.target])
                );
                const connected = initialNodes.filter((n) => connectedNodeIds.has(n.id));
                const isolated = initialNodes.filter((n) => !connectedNodeIds.has(n.id));

                const { layoutedNodes, layoutedEdges } = getLayoutedElements(connected, initialEdges);
                let allNodes = layoutedNodes;
                if (isolated.length > 0) {
                    allNodes = applyD3Force([...layoutedNodes, ...isolated]);
                }

                setNodes(allNodes);
                setEdges(layoutedEdges);
            } catch (err) {
                console.error("Failed to fetch graph data: ", err);
            }
        };

        fetchGraphData();
    }, []);

    // 노드 클릭 시 하이라이트 처리
    const onNodeClick: NodeMouseHandler = useCallback((_event, node) => {
        setSelectedNodeId(node.id);
        setSelectedEdgeId(null);
    }, []);

    // 엣지 클릭 시 하이라이트 처리
    const onEdgeClick: EdgeMouseHandler = useCallback((_event, edge) => {
        setSelectedEdgeId(edge.id);
        setSelectedNodeId(null);
    }, []);

    // 배경 클릭 시 하이라이트 해제
    const onPaneClick = useCallback(() => {
        setSelectedNodeId(null);
        setSelectedEdgeId(null);
    }, []);

    // 선택된 노드가 변경될 때마다 스타일 업데이트
    useEffect(() => {
        const inactiveStyle = { opacity: 0.3 };
        const activeStyle = { opacity: 1 };

        let highlightedNodes = new Set<string>();
        let highlightedEdges = new Set<string>();

        if (selectedNodeId) {
            const connectedEdges = edges.filter(edge => edge.source === selectedNodeId || edge.target === selectedNodeId);
            highlightedEdges = new Set(connectedEdges.map(e => e.id));
            highlightedNodes = new Set(connectedEdges.flatMap(edge => [edge.source, edge.target]));
            highlightedNodes.add(selectedNodeId);
        } else if (selectedEdgeId) {
            const selectedEdge = edges.find(edge => edge.id === selectedEdgeId);
            if (selectedEdge) {
                highlightedEdges.add(selectedEdge.id);
                highlightedNodes.add(selectedEdge.source);
                highlightedNodes.add(selectedEdge.target);
            }
        }

        setNodes(nodes.map(node => ({
            ...node,
            style: !selectedNodeId && !selectedEdgeId || highlightedNodes.has(node.id) ? activeStyle : inactiveStyle
        })));

        setEdges(edges.map(edge => ({
            ...edge,
            style: !selectedNodeId && !selectedEdgeId || highlightedEdges.has(edge.id) ? activeStyle : inactiveStyle
        })));
    }, [selectedNodeId, selectedEdgeId]);

    const onNodesChange = useCallback(
        (changes: NodeChange[]) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [setNodes]
    );

    const onEdgesChange = useCallback(
        (changes: EdgeChange[]) => setEdges((eds) => applyEdgeChanges(changes, eds)),
        [setEdges]
    );

    const onConnect = useCallback(
        (connection: Connection) => setEdges((eds) => addEdge(connection, eds)),
        [setEdges]
    );

    return (
        <MainLayout>
            <div style={{ height: '100vh' }}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onConnect={onConnect}
                    onNodeClick={onNodeClick}
                    onEdgeClick={onEdgeClick}
                    onPaneClick={onPaneClick}
                    fitView
                >
                    <Controls />
                    <Background />
                </ReactFlow>
                {selectedNote && (
                    <NotePreviewPanel note={selectedNote} />
                )}
            </div>
        </MainLayout>
    );
}

export default KnowledgeGraphPage;
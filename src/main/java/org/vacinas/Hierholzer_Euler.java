package org.vacinas;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;

public class Hierholzer_Euler {

    static void printEulerianCircuit(Graph graph) {
        // adj represents the adjacency list of
        // the directed graph
        // edge represents the number of edges emerging from a vertex

        Map<Integer, Integer> edges = new HashMap<>();

        for (int i = 0; i < graph.getNodeCount(); i++) {
            //find the count of edges to keep track of unused edges
            edges.put(i, graph.getNode(i).getDegree());
        }

        // Maintain a stack to keep vertices
        Stack<Node> curr_path = new Stack<>();

        // vector to store final circuit
        List<Node> circuit = new ArrayList<>();

        // We start from vertex 0
        curr_path.push(graph.getNode(0));

        // Current vertex
        int curr_v = 0;

        while (!curr_path.empty()) {
            // If there's remaining edge
            if (edges.get(curr_v) > 0) {
                // Push the vertex visited.
                curr_path.push(graph.getNode(curr_v) == graph.getNode(curr_v).getEdge(edges.get(curr_v) - 1).getNode0() ? graph.getNode(curr_v).getEdge(edges.get(curr_v) - 1).getNode1() : graph.getNode(curr_v).getEdge(edges.get(curr_v) - 1).getNode0());

                // and remove that edge or decrement the edge count.
                edges.put(curr_v, edges.get(curr_v) - 1);

                // Move to next vertex
                curr_v = curr_path.peek().getIndex();
            }

            // back-track to find remaining circuit
            else {
                circuit.add(curr_path.peek());
                curr_v = curr_path.pop().getIndex();
            }
        }

        // After getting the circuit, now print it in reverse
        for (int i = circuit.size() - 1; i >= 0; i--) {
            System.out.print(circuit.get(i));

            if (i != 0)
                System.out.print(" -> ");
        }

    }

}
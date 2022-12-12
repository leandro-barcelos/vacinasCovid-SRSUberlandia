package org.vacinas;

import org.graphstream.graph.Node;

public class DistanciaVertices {
    Node vertice1;
    Node vertice2;
    double distancia;

    public DistanciaVertices(Node vertice1, Node vertice2, double distancia) {
        this.vertice1 = vertice1;
        this.vertice2 = vertice2;
        this.distancia = distancia;
    }
}

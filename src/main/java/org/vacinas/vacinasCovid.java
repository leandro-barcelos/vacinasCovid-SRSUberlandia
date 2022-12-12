package org.vacinas;

import com.opencsv.CSVReader;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class vacinasCovid {
    public static void main(String[] args) throws IOException {

        List<Rota> rotas = lerCSVdistancias("src/main/resources/distancias.csv");

        Graph srsUberlandia = gerarGrafoSRS(rotas);

        // PASSO 1
        if (!isEuleriano(srsUberlandia)) {
            // PASSO 2
            List<Node> verticesImpares = new ArrayList<>();

            for (Node n : srsUberlandia) {
                if (n.getDegree() % 2 != 0) {
                    verticesImpares.add(n);
                }
            }

            // PASSO 3
            DistanciaVertices[][] distVerticesImpares = new DistanciaVertices[verticesImpares.size()][verticesImpares.size()];

            Dijkstra d = new Dijkstra(Dijkstra.Element.EDGE, null, "distance");
            d.init(srsUberlandia);

            for (int i = 0; i < verticesImpares.size(); i++) {
                Node n = verticesImpares.get(i);

                d.setSource(n);
                d.compute();

                for (int j = 0; j < verticesImpares.size(); j++) {
                    Node n2 = verticesImpares.get(j);

                    if (n == n2) {
                        distVerticesImpares[i][j] = new DistanciaVertices(n, n, Double.POSITIVE_INFINITY);
                    } else {
                        distVerticesImpares[i][j] = new DistanciaVertices(n, n2, d.getPathLength(n2));
                    }
                }
            }

            // PASSO 4

            double[][] matrizDistVerticesImpares = new double[distVerticesImpares.length][distVerticesImpares.length];

            for (int i = 0; i < distVerticesImpares.length; i++) {
                for (int j = 0; j < distVerticesImpares[i].length; j++) {
                    matrizDistVerticesImpares[i][j] = distVerticesImpares[i][j].distancia;
                }
            }

            HungarianAlgorithm ha = new HungarianAlgorithm(matrizDistVerticesImpares);
            int[][] resultadoAlgHungaro = ha.findOptimalAssignment();

            // PASSO 5

            for (int i = 0; i < resultadoAlgHungaro.length / 2; i++) {
                Node vertice1 = distVerticesImpares[resultadoAlgHungaro[i][0]][resultadoAlgHungaro[i][1]].vertice1;
                Node vertice2 = distVerticesImpares[resultadoAlgHungaro[i][0]][resultadoAlgHungaro[i][1]].vertice2;
                double distancia = distVerticesImpares[resultadoAlgHungaro[i][0]][resultadoAlgHungaro[i][1]].distancia;

                try {
                    srsUberlandia.addEdge(vertice1.getId() + "<->" + vertice2.getId(), vertice1, vertice2).setAttribute("distance", distancia);
                    srsUberlandia.getEdge(vertice1.getId() + "<->" + vertice2.getId()).setAttribute("ui.label", distancia / 1000 + "km");
                } catch (IdAlreadyInUseException e) {
                    srsUberlandia.removeEdge(vertice1.getId() + "<->" + vertice2.getId());

                    srsUberlandia.addEdge(vertice1.getId() + "<->" + vertice2.getId(), vertice1, vertice2, true).setAttribute("distance", distancia);
                    srsUberlandia.getEdge(vertice1.getId() + "<->" + vertice2.getId()).setAttribute("ui.label", distancia / 1000 + "km");

                    srsUberlandia.addEdge(vertice2.getId() + "<->" + vertice1.getId(), vertice2, vertice1, true).setAttribute("distance", distancia);
                    srsUberlandia.getEdge(vertice2.getId() + "<->" + vertice1.getId()).setAttribute("ui.label", distancia / 1000 + "km");
                }
            }
        }

        // PASSO 6
        Hierholzer_Euler.printEulerianCircuit(srsUberlandia);

        srsUberlandia.display();
    }

    public static boolean isEuleriano(Graph grafo) {
        for (Node n: grafo) {
            if (n.getDegree() % 2 != 0) return false;
        }

        return true;
    }

    public static List<Rota> lerCSVdistancias(String filename) throws IOException {
        List<Rota> rotas = new ArrayList<>();

        CSVReader cr = new CSVReader(new FileReader(filename));

        List<String[]> table = cr.readAll();

        for (String[] strings : table) {
            for (int j = 0; j < strings.length; j++) {
                try {
                    float distancia = Float.parseFloat(strings[j]);
                    String cidadeA = table.get(0)[j];
                    String cidadeB = strings[0];

                    Rota tmp = new Rota(cidadeA, cidadeB, distancia);
                    rotas.add(tmp);
                } catch (Exception ignored) {}
            }
        }

        return rotas;
    }

    public static Graph gerarGrafoSRS(List<Rota> rotas) {
        Graph srs = new SingleGraph("SRS Uberlândia");
        srs.setAttribute("ui.stylesheet", "url('src/main/resources/graphStyle.css')");

        for (Rota rota : rotas) {
            try {
                srs.addNode(rota.cidadeA).addAttribute("ui.label", rota.cidadeA);
            }
            catch (Exception ignored) {
            }
            try {
                srs.addNode(rota.cidadeB).addAttribute("ui.label", rota.cidadeB);
            }
            catch (Exception ignored) {
            }
            srs.addEdge(rota.cidadeA + "<->" + rota.cidadeB, rota.cidadeA, rota.cidadeB).setAttribute("distance", rota.distancia);
            srs.getEdge(rota.cidadeA + "<->" + rota.cidadeB).setAttribute("ui.label", rota.distancia / 1000 + "km");
        }

        return srs;
    }
}

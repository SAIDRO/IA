import java.util.*;

class Arista {
    int destino;
    int distancia;

    Arista(int destino, int distancia) {
        this.destino = destino;
        this.distancia = distancia;
    }
}

class Grafo {
    private final int nodos;
    private final List<List<Arista>> adyacencia;

    Grafo(int nodos) {
        this.nodos = nodos;
        adyacencia = new ArrayList<>();
        for (int i = 0; i < nodos; i++) {
            adyacencia.add(new ArrayList<>());
        }
    }

    // Método para añadir una arista
    void agregarArista(int origen, int destino, int distancia) {
        adyacencia.get(origen).add(new Arista(destino, distancia));
    }

    List<Arista> obtenerVecinos(int nodo) {
        return adyacencia.get(nodo);
    }

    int obtenerNodos() {
        return nodos;
    }
}

public class Dijkstra {

    public static void dijkstra(Grafo grafo, int inicio) {
        int N = grafo.obtenerNodos();
        int[] distancias = new int[N];
        Arrays.fill(distancias, Integer.MAX_VALUE);
        distancias[inicio] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.add(new int[]{0, inicio});

        while (!pq.isEmpty()) {
            int[] actual = pq.poll();
            int dist = actual[0];
            int nodo = actual[1];

            if (dist > distancias[nodo]) continue;

            for (Arista vecino : grafo.obtenerVecinos(nodo)) {
                int nuevaDistancia = dist + vecino.distancia;
                if (nuevaDistancia < distancias[vecino.destino]) {
                    distancias[vecino.destino] = nuevaDistancia;
                    pq.add(new int[]{nuevaDistancia, vecino.destino});
                }
            }
        }

        // Mostrar resultados
        System.out.println("Distancias más cortas desde el nodo " + inicio + ":");
        for (int i = 0; i < N; i++) {
            System.out.println("Nodo " + i + " -> " + distancias[i]);
        }
    }

    public static void main(String[] args) {
        Grafo grafo = new Grafo(6);

        grafo.agregarArista(0, 1, 4);
        grafo.agregarArista(0, 2, 2);
        grafo.agregarArista(1, 2, 5);
        grafo.agregarArista(1, 3, 10);
        grafo.agregarArista(2, 4, 3);
        grafo.agregarArista(4, 3, 4);
        grafo.agregarArista(3, 5, 11);

        dijkstra(grafo, 0);
    }
}

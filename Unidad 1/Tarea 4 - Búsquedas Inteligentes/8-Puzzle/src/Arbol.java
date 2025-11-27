import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class Arbol {
    Nodo raiz;
    private int nodosExpandidos; 

    public Arbol(Nodo raiz) {
        this.raiz = raiz;
    }

    // Método para obtener los nodos expandidos después de una búsqueda
    public int getNodosExpandidos() {
        return this.nodosExpandidos;
    }

    //1. Búsqueda primero en anchura
    public Nodo realizarBusquedaEnAnchura(String objetivo) {
        this.nodosExpandidos = 0;
        Queue<Nodo> cola = new LinkedList<>();
        HashSet<String> visitados = new HashSet<>();

        cola.add(raiz);
        visitados.add(raiz.estado);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            this.nodosExpandidos++;
            System.out.println("Procesando - " + actual.estado); // Mensaje

            if (actual.estado.equals(objetivo)) {
                return actual;
            }

            for (String sucesor : actual.obtenerSucesores()) {
                if (!visitados.contains(sucesor)) {
                    System.out.println("Agregando a cola - " + sucesor); // Mensaje
                    visitados.add(sucesor);
                    cola.add(new Nodo(sucesor, actual, actual.costo + 1, actual.profundidad + 1));
                }
            }
        }
        return null;
    }

    //2. Búsqueda de costo uniforme
    public Nodo realizarBusquedaDeCostoUniforme(String objetivo) {
        this.nodosExpandidos = 0;
        PriorityQueue<Nodo> colaPrioridad = new PriorityQueue<>(Comparator.comparingInt(n -> n.costo));
        HashSet<String> visitados = new HashSet<>();

        colaPrioridad.add(raiz);
        
        while (!colaPrioridad.isEmpty()) {
            Nodo actual = colaPrioridad.poll();
            this.nodosExpandidos++;
            System.out.println("Procesando - " + actual.estado); // Mensaje
            
            if(visitados.contains(actual.estado)) continue;
            visitados.add(actual.estado);

            if (actual.estado.equals(objetivo)) {
                return actual;
            }

            for (String sucesor : actual.obtenerSucesores()) {
                if (!visitados.contains(sucesor)) {
                    System.out.println("Agregando a cola - " + sucesor); // Mensaje
                    int nuevoCosto = actual.costo + 1;
                    colaPrioridad.add(new Nodo(sucesor, actual, nuevoCosto, actual.profundidad + 1));
                }
            }
        }
        return null;
    }

    //3. Búsqueda primero en profundidad
    public Nodo realizarBusquedaEnProfundidad(String objetivo) {
        this.nodosExpandidos = 0;
        Stack<Nodo> pila = new Stack<>();
        HashSet<String> visitados = new HashSet<>();

        pila.push(raiz);

        while (!pila.isEmpty()) {
            Nodo actual = pila.pop();
            this.nodosExpandidos++;
            System.out.println("Procesando - " + actual.estado); // Mensaje

            if (visitados.contains(actual.estado)) continue;
            visitados.add(actual.estado);

            if (actual.estado.equals(objetivo)) {
                return actual;
            }

            List<String> sucesores = actual.obtenerSucesores();
            for (int i = sucesores.size() - 1; i >= 0; i--) {
                String sucesor = sucesores.get(i);
                if (!visitados.contains(sucesor)) {
                    System.out.println("Agregando a pila - " + sucesor); // Mensaje
                    pila.push(new Nodo(sucesor, actual, actual.costo + 1, actual.profundidad + 1));
                }
            }
        }
        return null;
    }

    //4. Búsqueda en profundidad limitada
    public Nodo realizarBusquedaEnProfundidadLimitada(String objetivo, int limite) {
        this.nodosExpandidos = 0;
        Stack<Nodo> pila = new Stack<>();
        HashSet<String> visitados = new HashSet<>();
        
        pila.push(raiz);

        while (!pila.isEmpty()) {
            Nodo actual = pila.pop();
            this.nodosExpandidos++;
            System.out.println("Procesando - " + actual.estado);

            if (visitados.contains(actual.estado)) continue;
            visitados.add(actual.estado);

            if (actual.estado.equals(objetivo)) {
                return actual;
            }

            if (actual.profundidad < limite) {
                List<String> sucesores = actual.obtenerSucesores();
                for (int i = sucesores.size() - 1; i >= 0; i--) {
                    String sucesor = sucesores.get(i);
                     if (!visitados.contains(sucesor)) {
                        System.out.println("Agregando a pila - " + sucesor);
                        pila.push(new Nodo(sucesor, actual, actual.costo + 1, actual.profundidad + 1));
                    }
                }
            }
        }
        return null;
    }
    
    //5. Búsqueda AStar(A*) con heurística de diagonales
    public Nodo realizarBusquedaAEstrella(String objetivo) {
        this.nodosExpandidos = 0;
        PriorityQueue<Nodo> colaPrioridad = new PriorityQueue<>(
            Comparator.comparingInt(n -> n.costo + n.calcularHeuristicaDiagonales(objetivo))
        );
        
        HashSet<String> visitados = new HashSet<>();

        colaPrioridad.add(raiz);
        
        while (!colaPrioridad.isEmpty()) {
            Nodo actual = colaPrioridad.poll();
            this.nodosExpandidos++;
            System.out.println("Procesando A* - " + actual.estado + " (Costo + Heuristica: " + (actual.costo + actual.calcularHeuristicaDiagonales(objetivo)) + ")"); // Mensaje

            if(visitados.contains(actual.estado)) {
                continue;
            }
            visitados.add(actual.estado);

            if (actual.estado.equals(objetivo)) {
                return actual;
            }

            for (String sucesor : actual.obtenerSucesores()) {
                if (!visitados.contains(sucesor)) {
                    System.out.println("Agregando a cola A* - " + sucesor); 
                    int nuevoCosto = actual.costo + 1;
                    colaPrioridad.add(new Nodo(sucesor, actual, nuevoCosto, actual.profundidad + 1));
                }
            }
        }
        return null;
    }
}
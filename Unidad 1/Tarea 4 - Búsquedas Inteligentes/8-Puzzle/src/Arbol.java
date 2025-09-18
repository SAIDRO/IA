import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class Arbol {
    Nodo raiz;

    // Clase interna para devolver los resultados de la búsqueda
    public static class ResultadoBusqueda {
        public final Nodo nodoFinal;
        public final int nodosExpandidos;

        public ResultadoBusqueda(Nodo nodoFinal, int nodosExpandidos) {
            this.nodoFinal = nodoFinal;
            this.nodosExpandidos = nodosExpandidos;
        }
    }

    public Arbol(Nodo raiz) {
        this.raiz = raiz;
    }

    //Búsqueda primero en anchura
    public ResultadoBusqueda realizarBusquedaEnAnchura(String objetivo) {
        Queue<Nodo> cola = new LinkedList<>();
        HashSet<String> visitados = new HashSet<>();
        int nodosExpandidos = 0;

        cola.add(raiz);
        visitados.add(raiz.estado);

        while (!cola.isEmpty()) {
            Nodo actual = cola.poll();
            nodosExpandidos++;

            if (actual.estado.equals(objetivo)) {
                return new ResultadoBusqueda(actual, nodosExpandidos);
            }

            for (String sucesor : actual.obtenerSucesores()) {
                if (!visitados.contains(sucesor)) {
                    visitados.add(sucesor);
                    cola.add(new Nodo(sucesor, actual, actual.costo + 1, actual.profundidad + 1));
                }
            }
        }
        return new ResultadoBusqueda(null, nodosExpandidos);
    }

    //Búsqueda de costo uniforme
    public ResultadoBusqueda realizarBusquedaDeCostoUniforme(String objetivo) {
        PriorityQueue<Nodo> colaPrioridad = new PriorityQueue<>(Comparator.comparingInt(n -> n.costo));
        HashSet<String> visitados = new HashSet<>();
        int nodosExpandidos = 0;

        colaPrioridad.add(raiz);
        
        while (!colaPrioridad.isEmpty()) {
            Nodo actual = colaPrioridad.poll();
            nodosExpandidos++;
            
            if(visitados.contains(actual.estado)) continue;
            visitados.add(actual.estado);

            if (actual.estado.equals(objetivo)) {
                return new ResultadoBusqueda(actual, nodosExpandidos);
            }

            for (String sucesor : actual.obtenerSucesores()) {
                if (!visitados.contains(sucesor)) {
                    int nuevoCosto = actual.costo + 1;
                    colaPrioridad.add(new Nodo(sucesor, actual, nuevoCosto, actual.profundidad + 1));
                }
            }
        }
        return new ResultadoBusqueda(null, nodosExpandidos);
    }

    //Búsqueda primero en profundidad
    public ResultadoBusqueda realizarBusquedaEnProfundidad(String objetivo) {
        Stack<Nodo> pila = new Stack<>();
        HashSet<String> visitados = new HashSet<>();
        int nodosExpandidos = 0;

        pila.push(raiz);

        while (!pila.isEmpty()) {
            Nodo actual = pila.pop();
            nodosExpandidos++;

            if (visitados.contains(actual.estado)) continue;
            visitados.add(actual.estado);

            if (actual.estado.equals(objetivo)) {
                return new ResultadoBusqueda(actual, nodosExpandidos);
            }

            List<String> sucesores = actual.obtenerSucesores();
            // Se añaden en orden inverso para explorar de izquierda a derecha
            for (int i = sucesores.size() - 1; i >= 0; i--) {
                String sucesor = sucesores.get(i);
                if (!visitados.contains(sucesor)) {
                    pila.push(new Nodo(sucesor, actual, actual.costo + 1, actual.profundidad + 1));
                }
            }
        }
        return new ResultadoBusqueda(null, nodosExpandidos);
    }

    //Búsqueda en profundidad limitada
    public ResultadoBusqueda realizarBusquedaEnProfundidadLimitada(String objetivo, int limite) {
        Stack<Nodo> pila = new Stack<>();
        HashSet<String> visitados = new HashSet<>();
        int nodosExpandidos = 0;
        
        pila.push(raiz);

        while (!pila.isEmpty()) {
            Nodo actual = pila.pop();
            nodosExpandidos++;

            if (visitados.contains(actual.estado)) continue;
            visitados.add(actual.estado);

            if (actual.estado.equals(objetivo)) {
                return new ResultadoBusqueda(actual, nodosExpandidos);
            }

            if (actual.profundidad < limite) {
                List<String> sucesores = actual.obtenerSucesores();
                for (int i = sucesores.size() - 1; i >= 0; i--) {
                    String sucesor = sucesores.get(i);
                     if (!visitados.contains(sucesor)) {
                        pila.push(new Nodo(sucesor, actual, actual.costo + 1, actual.profundidad + 1));
                    }
                }
            }
        }
        return new ResultadoBusqueda(null, nodosExpandidos);
    }
}
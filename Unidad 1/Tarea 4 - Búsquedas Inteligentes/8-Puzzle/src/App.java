import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        mostrarMenu(scanner);
    }

     //Muestra el menú de opciones de forma recursiva hasta que el usuario elige salir.
    private static void mostrarMenu(Scanner scanner) {
        String estadoInicial = "7245 6831";
        String estadoFinal = " 12345678";

        System.out.println("\n===== SOLUCIONADOR DE 8-PUZZLE (ALGORITMOS DE BUSQUEDA) =====\n");
        System.out.println("Elige el algoritmo de busqueda que deseas ejecutar:");
        System.out.println("1. Busqueda primero en anchura");
        System.out.println("2. Busqueda costo uniforme");
        System.out.println("3. Busqueda primero en profundidad");
        System.out.println("4. Busqueda profundidad limitada");
        System.out.println("5. Busqueda AStar(A*) (Heuristica: Diagonales)");
        System.out.println("6. Ejecutar TODOS y mostrar tabla comparativa (Tardara un tiempo)");
        System.out.println("7. Salir del programa");
        System.out.print("Elige una opcion: ");
        int opcion = scanner.nextInt();

        if (opcion >= 1 && opcion <= 5) {
            ejecutarBusquedaUnica(opcion, estadoInicial, estadoFinal);
            mostrarMenu(scanner);
        } else if (opcion == 6) {
            ejecutarTodasLasBusquedas(estadoInicial, estadoFinal);
            mostrarMenu(scanner);
        } else if (opcion == 7) {
            return;
        } else {
            System.out.println("Opcion no valida. Intentalo de nuevo.");
            mostrarMenu(scanner);
        }
    }
    
     //Ejecuta un único algoritmo de búsqueda según la opción del usuario.
    private static void ejecutarBusquedaUnica(int opcion, String estadoInicial, String estadoFinal) {
        Arbol arbol = new Arbol(new Nodo(estadoInicial, null, 0, 0));
        Nodo nodoFinal = null;
        long startTime = System.currentTimeMillis();

        switch (opcion) {
            case 1:
                System.out.println("\nEjecutando: Busqueda primero en anchura...");
                nodoFinal = arbol.realizarBusquedaEnAnchura(estadoFinal);
                break;
            case 2:
                System.out.println("\nEjecutando: Busqueda costo uniforme...");
                nodoFinal = arbol.realizarBusquedaDeCostoUniforme(estadoFinal);
                break;
            case 3:
                System.out.println("\nEjecutando: Busqueda primero en profundidad...");
                nodoFinal = arbol.realizarBusquedaEnProfundidad(estadoFinal);
                break;
            case 4:
                System.out.println("\nEjecutando: Busqueda profundidad limitada (l=25)...");
                nodoFinal = arbol.realizarBusquedaEnProfundidadLimitada(estadoFinal, 40);
                break;
            case 5:
                System.out.println("\nEjecutando: Busqueda AStar(A*) (Heuristica: Diagonales)...");
                nodoFinal = arbol.realizarBusquedaAEstrella(estadoFinal);
                break;
        }

        long endTime = System.currentTimeMillis();
        double tiempoTotal = (endTime - startTime) / 1000.0;
        
        System.out.println("\n===== RESULTADO DE LA BUSQUEDA =====");
        if (nodoFinal != null) {
            mostrarPasos(reconstruirCamino(nodoFinal));
            System.out.println("\nResumen: Solucion encontrada en " + tiempoTotal + " segundos, expandiendo " + arbol.getNodosExpandidos() + " nodos.");
        } else {
            System.out.println("No se encontro una solucion. El calculo tomo " + tiempoTotal + " segundos y expandio " + arbol.getNodosExpandidos() + " nodos.");
        }
    }

    //Ejecuta todos los algoritmos para generar una tabla comparativa de rendimiento.
       private static void ejecutarTodasLasBusquedas(String estadoInicial, String estadoFinal) {
        String[] tiempos = new String[5];
        String[] espacios = new String[5];

        System.out.println("\nEjecutando todos los algoritmos, por favor espera...");
        Arbol arbol = new Arbol(new Nodo(estadoInicial, null, 0, 0));
        
        long startTime = System.currentTimeMillis();
        arbol.realizarBusquedaEnAnchura(estadoFinal);
        long endTime = System.currentTimeMillis();
        tiempos[0] = (endTime - startTime) / 1000.0 + " s";
        espacios[0] = arbol.getNodosExpandidos() + " nodos";

        startTime = System.currentTimeMillis();
        arbol.realizarBusquedaDeCostoUniforme(estadoFinal);
        endTime = System.currentTimeMillis();
        tiempos[1] = (endTime - startTime) / 1000.0 + " s";
        espacios[1] = arbol.getNodosExpandidos() + " nodos";

        startTime = System.currentTimeMillis();
        arbol.realizarBusquedaEnProfundidad(estadoFinal);
        endTime = System.currentTimeMillis();
        tiempos[2] = (endTime - startTime) / 1000.0 + " s";
        espacios[2] = arbol.getNodosExpandidos() + " nodos";
        
        startTime = System.currentTimeMillis();
        arbol.realizarBusquedaEnProfundidadLimitada(estadoFinal, 40);
        endTime = System.currentTimeMillis();
        tiempos[3] = (endTime - startTime) / 1000.0 + " s";
        espacios[3] = arbol.getNodosExpandidos() + " nodos";

        startTime = System.currentTimeMillis();
        arbol.realizarBusquedaAEstrella(estadoFinal);
        endTime = System.currentTimeMillis();
        tiempos[4] = (endTime - startTime) / 1000.0 + " s";
        espacios[4] = arbol.getNodosExpandidos() + " nodos";

        System.out.println("...Calculos completados.");
        
        System.out.println("\n\n===== TABLA COMPARATIVA DE RENDIMIENTO =====");
        String separador = "+-----------------------------------------+----------------+------------------+";
        String formatoFila = "| %-39s | %-14s | %-16s |%n";

        System.out.println(separador);
        System.out.format(formatoFila, "Criterio", "Tiempo (seg)", "Espacio");
        System.out.println(separador);
        
        // --- Impresión de la tabla con nombres escritos directamente ---
        System.out.format(formatoFila, "Busqueda primero en anchura", tiempos[0], espacios[0]);
        System.out.format(formatoFila, "Busqueda costo uniforme", tiempos[1], espacios[1]);
        System.out.format(formatoFila, "Busqueda primero en profundidad", tiempos[2], espacios[2]);
        System.out.format(formatoFila, "Busqueda profundidad limitada ", tiempos[3], espacios[3]);
        System.out.format(formatoFila, "Busqueda AStar(A*) (Heuristica)", tiempos[4], espacios[4]);

        System.out.println(separador);
    }

    //Reconstruye el camino desde el nodo final hasta el nodo raíz
    private static List<Nodo> reconstruirCamino(Nodo nodoFinal) {
        List<Nodo> camino = new ArrayList<>();
        Nodo actual = nodoFinal;
        while (actual != null) {
            camino.add(0, actual);
            actual = actual.padre;
        }
        return camino;
    }

     //Muestra en consola la solución paso a paso a partir de una lista de nodos.
    private static void mostrarPasos(List<Nodo> camino) {
        if (camino.isEmpty()) {
            System.out.println("No se encontro una solucion para este algoritmo.\n");
            return;
        }
        System.out.println("\n--- Solucion Detallada ---");
        System.out.println("Estado inicial:");
        imprimirTablero(camino.get(0).estado);
        for (int i = 1; i < camino.size(); i++) {
            String descripcion = describirMovimiento(camino.get(i - 1).estado, camino.get(i).estado);
            System.out.println("\nPaso " + i + ": " + descripcion);
            imprimirTablero(camino.get(i).estado);
        }
    }

    //Genera una descripción del movimiento realizado entre dos estados.
    private static String describirMovimiento(String anterior, String actual) {
        int vacioAntes = anterior.indexOf(' ');
        int vacioDespues = actual.indexOf(' ');
        char fichaMovida = anterior.charAt(vacioDespues);
        String direccion = "desconocida";
        if (vacioDespues == vacioAntes - 1) direccion = "la izquierda";
        else if (vacioDespues == vacioAntes + 1) direccion = "la derecha";
        else if (vacioDespues == vacioAntes - 3) direccion = "arriba";
        else if (vacioDespues == vacioAntes + 3) direccion = "abajo";
        return "Se movio el '" + fichaMovida + "' hacia " + direccion;
    }
    
    
    //Imprime el tablero del puzle en un formato gráfico y legible. 
    private static void imprimirTablero(String estado) {
        String bordeSuperior = "╔═════╦═════╦═════╗";
        String bordeMedio    = "╠═════╬═════╬═════╣";
        String bordeInferior = "╚═════╩═════╩═════╝";

        System.out.println(bordeSuperior);
        for (int fila = 0; fila < 3; fila++) {
            System.out.print("║");
            for (int col = 0; col < 3; col++) {
                char valor = estado.charAt(fila * 3 + col);
                String celda = (valor == ' ') ? "     " : "  " + valor + "  ";
                System.out.print(celda + "║");
            }
            System.out.println();
            if (fila < 2) {
                System.out.println(bordeMedio);
            }
        }
        System.out.println(bordeInferior);
    }
}
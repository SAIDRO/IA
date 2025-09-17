import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        // --- Configuración Inicial ---
        String estadoInicial = "7245 6831";
        String estadoFinal = " 12345678";
        
        System.out.println("Ejecutando todos los algoritmos de busqueda...");

        // --- Arreglos para guardar los resultados ---
        String[] nombresAlgoritmos = {
            "Busqueda Primero en Anchura",
            "Busqueda Costo Uniforme",
            "Busqueda Primero en Profundidad",
            "Busqueda Profundidad Limitada (l=25)"
        };
        String[] tiempos = new String[4];
        String[] espacios = new String[4];
        Nodo[] nodosFinales = new Nodo[4];

        // --- Ejecución de todos los algoritmos para recolectar datos ---

        // 1. Anchura
        Arbol arbolBFS = new Arbol(new Nodo(estadoInicial, null, 0, 0));
        long startTime = System.nanoTime();
        Arbol.ResultadoBusqueda resultadoBFS = arbolBFS.realizarBusquedaEnAnchura(estadoFinal);
        long endTime = System.nanoTime();
        tiempos[0] = String.format("%.6f s", (endTime - startTime) / 1_000_000_000.0);
        espacios[0] = resultadoBFS.nodosExpandidos + " nodos";
        nodosFinales[0] = resultadoBFS.nodoFinal;

        // 2. Costo Uniforme
        Arbol arbolUCS = new Arbol(new Nodo(estadoInicial, null, 0, 0));
        startTime = System.nanoTime();
        Arbol.ResultadoBusqueda resultadoUCS = arbolUCS.realizarBusquedaDeCostoUniforme(estadoFinal);
        endTime = System.nanoTime();
        tiempos[1] = String.format("%.6f s", (endTime - startTime) / 1_000_000_000.0);
        espacios[1] = resultadoUCS.nodosExpandidos + " nodos";
        nodosFinales[1] = resultadoUCS.nodoFinal;

        // 3. Profundidad
        Arbol arbolDFS = new Arbol(new Nodo(estadoInicial, null, 0, 0));
        startTime = System.nanoTime();
        Arbol.ResultadoBusqueda resultadoDFS = arbolDFS.realizarBusquedaEnProfundidad(estadoFinal);
        endTime = System.nanoTime();
        tiempos[2] = String.format("%.6f s", (endTime - startTime) / 1_000_000_000.0);
        espacios[2] = resultadoDFS.nodosExpandidos + " nodos";
        nodosFinales[2] = resultadoDFS.nodoFinal;

        // 4. Profundidad Limitada
        Arbol arbolDLS = new Arbol(new Nodo(estadoInicial, null, 0, 0));
        int limite = 25;
        startTime = System.nanoTime();
        Arbol.ResultadoBusqueda resultadoDLS = arbolDLS.realizarBusquedaEnProfundidadLimitada(estadoFinal, limite);
        endTime = System.nanoTime();
        tiempos[3] = String.format("%.6f s", (endTime - startTime) / 1_000_000_000.0);
        espacios[3] = resultadoDLS.nodosExpandidos + " nodos";
        nodosFinales[3] = resultadoDLS.nodoFinal;
        
        System.out.println("...Calculos completados.");

        // --- Menú de Opciones ---
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n¿Que solucion detallada deseas ver?");
        System.out.println("1. Busqueda Primero en Anchura");
        System.out.println("2. Busqueda Costo Uniforme");
        System.out.println("3. Busqueda Primero en Profundidad");
        System.out.println("4. Busqueda Profundidad Limitada");
        System.out.println("5. No mostrar pasos, solo la tabla final");
        System.out.print("Elige una opcion: ");
        int opcion = scanner.nextInt();
        
        if (opcion >= 1 && opcion <= 4) {
            System.out.println("\n===== Mostrando solucion para: " + nombresAlgoritmos[opcion - 1] + " =====");
            mostrarPasos(reconstruirCamino(nodosFinales[opcion - 1]));
        }

        // --- Imprimir la tabla comparativa final ---
        System.out.println("\n\n===== TABLA COMPARATIVA DE RENDIMIENTO =====");
        String separador = "+-----------------------------------------+----------------+------------------+";
        String formatoFila = "| %-39s | %-14s | %-16s |%n";

        System.out.println(separador);
        System.out.format(formatoFila, "Criterio", "Tiempo (seg)", "Espacio");
        System.out.println(separador);
        for (int i = 0; i < nombresAlgoritmos.length; i++) {
            System.out.format(formatoFila, nombresAlgoritmos[i], tiempos[i], espacios[i]);
        }
        System.out.println(separador);
        
        scanner.close();
    }

    // --- Métodos de ayuda (sin cambios) ---

    private static List<Nodo> reconstruirCamino(Nodo nodoFinal) {
        if (nodoFinal == null) return new ArrayList<>();
        List<Nodo> camino = new ArrayList<>();
        Nodo actual = nodoFinal;
        while (actual != null) {
            camino.add(0, actual);
            actual = actual.padre;
        }
        return camino;
    }

    private static void mostrarPasos(List<Nodo> camino) {
        if (camino.isEmpty()) {
            System.out.println("No se encontro una solucion para este algoritmo.\n");
            return;
        }
        System.out.println("\nEstado inicial:");
        imprimirTablero(camino.get(0).estado);
        for (int i = 1; i < camino.size(); i++) {
            Nodo anterior = camino.get(i - 1);
            Nodo actual = camino.get(i);
            String descripcion = describirMovimiento(anterior.estado, actual.estado);
            System.out.println("\nPaso " + i + ": " + descripcion);
            imprimirTablero(actual.estado);
        }
    }

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
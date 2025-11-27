// Clase Nodo
class Nodo {
    String nombre;
    Nodo izquierda;
    Nodo derecha;

    public Nodo(String nombre) {
        this.nombre = nombre;
        this.izquierda = null;
        this.derecha = null;
    }
}

// Clase Arbol
class Arbol {
    private Nodo raiz;

    public Arbol() {
        this.raiz = null;
    }
    public boolean vacio() {
        return raiz == null;
    }

    // Método para buscar un nodo
    public Nodo buscarNodo(String nombre) {
        return buscarRecursivo(raiz, nombre);
    }

    private Nodo buscarRecursivo(Nodo actual, String nombre) {
        if (actual == null || actual.nombre.equals(nombre)) {
            return actual;
        }
        if (nombre.compareTo(actual.nombre) < 0) {
            return buscarRecursivo(actual.izquierda, nombre);
        } else {
            return buscarRecursivo(actual.derecha, nombre);
        }
    }

    public void insertar(String nombre) {
        raiz = insertarRecursivo(raiz, nombre);
    }

    private Nodo insertarRecursivo(Nodo actual, String nombre) {
        if (actual == null) {
            return new Nodo(nombre);
        }
        if (nombre.compareTo(actual.nombre) < 0) {
            actual.izquierda = insertarRecursivo(actual.izquierda, nombre);
        } else if (nombre.compareTo(actual.nombre) > 0) {
            actual.derecha = insertarRecursivo(actual.derecha, nombre);
        }
        return actual;
    }

    public void imprimirArbol() {
    imprimirArbolRecursivo(raiz, 0);
    }

    private void imprimirArbolRecursivo(Nodo actual, int nivel) {
        if (actual == null) {
            return;
        }

        imprimirArbolRecursivo(actual.derecha, nivel + 1);

        for (int i = 0; i < nivel; i++) {
            System.out.print("    "); 
        }
        System.out.println(actual.nombre);

        imprimirArbolRecursivo(actual.izquierda, nivel + 1);
    }

}

// Clase Main
public class ArbolBinario {
    public static void main(String[] args) {
        Arbol arbol = new Arbol();

        arbol.insertar("Juan");
        arbol.insertar("Ana");
        arbol.insertar("Pedro");
        arbol.insertar("Jose");
        arbol.insertar("Maria");

        Nodo encontrado = arbol.buscarNodo("Pedro");
        if (encontrado != null) {
            System.out.println("Nodo encontrado: " + encontrado.nombre);
        } else {
            System.out.println("Nodo no encontrado.");
        }

        System.out.println("Árbol en forma visual:");
        arbol.imprimirArbol();
    }


}

import java.util.ArrayList;
import java.util.List;

public class Nodo {
    String estado;
    Nodo padre;
    int costo;
    int profundidad;

    public Nodo(String estado, Nodo padre, int costo, int profundidad) {
        this.estado = estado;
        this.padre = padre;
        this.costo = costo;
        this.profundidad = profundidad;
    }

    public List<String> obtenerSucesores() {
        List<String> successors = new ArrayList<String>();
        int posEspacio = estado.indexOf(" ");

        switch (posEspacio) {
            case 0:
                successors.add(intercambiar(posEspacio, 1));
                successors.add(intercambiar(posEspacio, 3));
                break;
            case 1:
                successors.add(intercambiar(posEspacio, 0));
                successors.add(intercambiar(posEspacio, 2));
                successors.add(intercambiar(posEspacio, 4));
                break;
            case 2:
                successors.add(intercambiar(posEspacio, 1));
                successors.add(intercambiar(posEspacio, 5));
                break;
            case 3:
                successors.add(intercambiar(posEspacio, 0));
                successors.add(intercambiar(posEspacio, 4));
                successors.add(intercambiar(posEspacio, 6));
                break;
            case 4:
                successors.add(intercambiar(posEspacio, 1));
                successors.add(intercambiar(posEspacio, 3));
                successors.add(intercambiar(posEspacio, 5));
                successors.add(intercambiar(posEspacio, 7));
                break;
            case 5:
                successors.add(intercambiar(posEspacio, 2));
                successors.add(intercambiar(posEspacio, 4));
                successors.add(intercambiar(posEspacio, 8));
                break;
            case 6:
                successors.add(intercambiar(posEspacio, 3));
                successors.add(intercambiar(posEspacio, 7));
                break;
            case 7:
                successors.add(intercambiar(posEspacio, 4));
                successors.add(intercambiar(posEspacio, 6));
                successors.add(intercambiar(posEspacio, 8));
                break;
            case 8:
                successors.add(intercambiar(posEspacio, 5));
                successors.add(intercambiar(posEspacio, 7));
                break;
        }
        return successors;
    }

    private String intercambiar(int pos1, int pos2) {
        char[] estadoArray = this.estado.toCharArray();
        char temp = estadoArray[pos1];
        estadoArray[pos1] = estadoArray[pos2];
        estadoArray[pos2] = temp;
        return new String(estadoArray);
    }


    //Heuristica
    //Calcular la heurística basada en si las diagonales (0, 4, 8) y (2, 4, 6) están correctas.
    
    public int calcularHeuristicaDiagonales(String objetivo) {
        int diagonalIncorrecta = 0;
        
        // Diagonal principal (posiciones 0, 4, 8)
        if (estado.charAt(0) != objetivo.charAt(0) || 
            estado.charAt(4) != objetivo.charAt(4) || 
            estado.charAt(8) != objetivo.charAt(8)) {
            diagonalIncorrecta++;
        }

        // Diagonal secundaria (posiciones 2, 4, 6)
        if (estado.charAt(2) != objetivo.charAt(2) || 
            estado.charAt(4) != objetivo.charAt(4) || 
            estado.charAt(6) != objetivo.charAt(6)) {
            diagonalIncorrecta++;
        }

        return diagonalIncorrecta;
    }
}


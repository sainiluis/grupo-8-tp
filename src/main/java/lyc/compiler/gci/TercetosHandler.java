package lyc.compiler.gci;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class TercetosHandler {
    private ArrayList<Terceto> tercetos;
    private Map<String, Integer> indiceTerminales = new HashMap<>();


    // Constructor
    public TercetosHandler() {
        this.tercetos = new ArrayList<>();
    }


    public void agregarTerceto(Terceto terceto) {
        this.tercetos.add(terceto);
    }


    public Terceto obtenerTerceto(int indice) {
        for (Terceto t : tercetos) {
            if (t.getIndice() == indice) {
                return t;
            }
        }
        return null; 
    }

    public void imprimirTercetos() {
        for (Terceto t : tercetos) {
            System.out.println(t.toString());
        }
    }

    public void agregarIndice(String noTerminal, int numeroTerceto) {
        indiceTerminales.put(noTerminal, numeroTerceto);
    }

    public int obtenerIndice(String noTerminal) {
        return indiceTerminales.getOrDefault(noTerminal, -1); // Devuelve -1 si no se encuentra el no terminal
    }

    public ArrayList<Terceto> getIntermediateCode() {
        return tercetos;
    }

    public void mostrarIndicesTercetos() {
        System.out.println("Índices de tercetos:");
        for (Map.Entry<String, Integer> entry : indiceTerminales.entrySet()) {
            String noTerminal = entry.getKey();
            int numeroTerceto = entry.getValue();
            System.out.println(noTerminal + " => Terceto " + numeroTerceto);
        }
    }

    public void actualizar_branch_terceto(int terceto_a_reemplazar, String valor) {
        // Busco el terceto a actualizar
        for (Terceto terceto : tercetos) {
            if (terceto.getIndice() == terceto_a_reemplazar) {
                terceto.setElemento1(valor);
                break; 
            }
        }
    }


    // esta función asigna el valor del indice de un no terminal al indice de otro no terminal.
    // puede ser util en reglas como termino := factor ya que Tind = Find 
    public void apuntarAOtroIndice(String noTerminal1, String noTerminal2) {

        Integer indice = indiceTerminales.get(noTerminal2);
        
        if (indice != null) {
            // Asignar el índice de noTerminal2 a noTerminal1
            indiceTerminales.put(noTerminal1, indice);
        }
    }

    public void quitarUltimoGoTo() {
        for (int i = tercetos.size() - 1; i >= 0; i--) {
            Terceto terceto = tercetos.get(i);
            if (terceto.getOperador().equals("go_to")) {
                tercetos.remove(i);
                for (int j = i; j < tercetos.size(); j++) {
                    Terceto t = tercetos.get(j);
                    t.setIndice(j + 1);
                }
                break;
            }
        }
    }

    public void modificarUltimoGoTo(String nuevo_elemento1) {
        for (int i = tercetos.size() - 1; i >= 0; i--) {
            Terceto terceto = tercetos.get(i);
            if (terceto.getOperador().equals("go_to")) {
                System.out.println("El ultimo terceto es: \n" + tercetos.get(i).toString());
                terceto.setElemento1(nuevo_elemento1);
                break;
            }
        }
    }

    public void agregarTipoDeVariableInit(String cadena) {
        for (Terceto terceto : tercetos) {
            if (terceto.getOperador().equals("INIT") && terceto.getElemento2().equals("_")) {
                terceto.setElemento2(cadena);
            }
        }
    }

    public void reemplazarOperadorUltimoIfTrue(String nuevoOperador) {
        int lastIndex = -1;
        for (int i = tercetos.size() - 1; i >= 0; i--) {
            Terceto terceto = tercetos.get(i);
            if (terceto.getOperador().equals("if_true")) {
                lastIndex = i;
                break;
            }
        }
        if (lastIndex != -1) {
            tercetos.get(lastIndex).setOperador(nuevoOperador);
        }
    }

    public void saltarAlTercetoDesapilado(int numeroDeTerceto, int valor) {
        
        for (Terceto terceto : tercetos) {
            if (terceto.getIndice() == numeroDeTerceto) {
                terceto.setElemento1("[" + Integer.toString(valor) + "]");
                return; 
            }
        }
    }

    public void crearNuevoTerceto(int index, String operador, String elemento1, String elemento2) {
        Terceto nuevoTerceto = new Terceto(index, operador, elemento1, elemento2);
        
        if (index >= tercetos.size()) {
            tercetos.add(nuevoTerceto);
        } else {
            tercetos.add(index, nuevoTerceto);
        }
        
        reorganizarIndices();
    }

    private void reorganizarIndices() {
        for (int i = 0; i < tercetos.size(); i++) {
            tercetos.get(i).setIndice(i + 1); 
        }
    }

    public String getOperadorPorIndice(int indiceTerceto) {
        for (Terceto terceto : tercetos) {
            if (terceto.getIndice() == indiceTerceto) {
                return terceto.getOperador();
            }
        }
        return null; 
    }

}

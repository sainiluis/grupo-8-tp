package lyc.compiler.gci;

public class Terceto {
    private int indice;
    private String operador;
    private String elemento1;
    private String elemento2;

    // Constructor
    public Terceto(int indice, String operador, String elemento1, String elemento2) {
        this.indice = indice;
        this.operador = operador;
        this.elemento1 = elemento1;
        this.elemento2 = elemento2;
    }

    // Getters y setters
    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getElemento1() {
        return elemento1;
    }

    public void setElemento1(String elemento1) {
        this.elemento1 = elemento1;
    }

    public String getElemento2() {
        return elemento2;
    }

    public void setElemento2(String elemento2) {
        this.elemento2 = elemento2;
    }

    @Override
    public String toString() {

        String element1Str=this.elemento1;
        String element2Str=this.elemento2;

        if (elemento1.startsWith("terceto_")) {
            element1Str = elemento1.substring(8);
            element1Str = "[" + element1Str + "]";
        }

        if (elemento2.startsWith("terceto_")) {
            element2Str = elemento2.substring(8);
            element2Str = "[" + element2Str + "]";
        }
        
        return "[" + indice + "] (" + this.operador + " , " + element1Str + " , " + element2Str + ")";
    }
}
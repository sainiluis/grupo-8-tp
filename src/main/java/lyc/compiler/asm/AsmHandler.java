package lyc.compiler.asm;

import lyc.compiler.gci.*;
import lyc.compiler.symbol_table.*;

//import lyc.compiler.symbol_table.Simbolo;
//import lyc.compiler.model.Terceto;

import java.util.ArrayList;
import java.util.List;

import java.util.Stack;

import java.util.HashMap;

public class AsmHandler {
    private ArrayList<String> asmInstructions;
    private ArrayList<Simbolo> symbolTable;
    private ArrayList<Terceto> tercetos;
    private HashMap<Integer, String> tercetoLabels;

    public AsmHandler(ArrayList<Simbolo> symbolTable, ArrayList<Terceto> tercetos) {
        this.symbolTable = symbolTable;
        this.tercetos = tercetos;
        this.asmInstructions = new ArrayList<>();
        this.tercetoLabels = new HashMap<>();
    }

    private void assignLabelsToTercetos() {
        for (int i = 0; i < tercetos.size(); i++) {
            tercetoLabels.put(i + 1, "LABEL_" + (i + 1));
        }
    }

    private void filterBranchLabels() {
        HashMap<Integer, String> branchLabels = new HashMap<>();
        for (int i = 0; i < tercetos.size(); i++) {
            Terceto terceto = tercetos.get(i);
            String operador = terceto.getOperador();
            if (operador.equals("BLE") || operador.equals("BGE") || operador.equals("BI") || operador.equals("BNE")) {

                try {
                    int destino = Integer.parseInt(terceto.getElemento1().replace("[", "").replace("]", "").replace("terceto", "").replace("_", ""));
                    branchLabels.put(destino, tercetoLabels.get(destino));
                } catch(Exception e) {
                    continue;
                }

                
            }
        }
        tercetoLabels = branchLabels;
    }

    // private void printTercetoLabels() {
    //     System.out.println("Terceto Labels:");
    //     for (Integer key : tercetoLabels.keySet()) {
    //         System.out.println("Terceto " + key + " -> " + tercetoLabels.get(key));
    //     }
    // }

    public void generateAssembler() {
        assignLabelsToTercetos();
        filterBranchLabels();
        //printTercetoLabels();
        insertHeaders();
        insertSymbolTable();
        generateTercetosCode();
    }

    private void insertHeaders() {
        asmInstructions.add(".MODEL  LARGE");
        asmInstructions.add(".386");
        asmInstructions.add(".STACK 200h");
    }

    private void insertSymbolTable() {

        asmInstructions.add("");
        asmInstructions.add("");
        asmInstructions.add(".DATA");

        for (Simbolo symbol : symbolTable) {

            try {
                // reemplazamos el "." por "x" en el nombre del simbolo ya que daba error en el pasaje a asm
                if(symbol.getTipoDato().equals("Float") && symbol.getNombre().contains(".")) {

                    String valor = symbol.getValor();

                    // Formatear el valor
                    if (valor.startsWith(".")) {
                        valor = "0" + valor;  // Cambia ".9999" a "0.9999"
                    } else if (valor.endsWith(".")) {
                        valor = valor + "00";  // Cambia "99." a "99.00"
                    }

                    String nuevoNombre = valor.replace('.','x');
                    symbol.setNombre("_" + nuevoNombre);
                    symbol.setValor(valor);
                }

                if(symbol.getValor().isEmpty()) { // es una variable
                    asmInstructions.add("\t" + symbol.getNombre() + "\t\tdd ?" );
                } else { // es una cte

                    if(symbol.getTipoDato().equals("String")) { // el nombre del string no puede tener " "
                        String nombre_string = symbol.getNombre().replace("\"", "");
                        nombre_string = nombre_string.replace(' ', '_');
                        asmInstructions.add("\t" + nombre_string + "\t\tdb\t\t" + symbol.getValor() +", '$'");
                    } else {
                        asmInstructions.add("\t" + symbol.getNombre() + "\t\tdd\t\t" + symbol.getValor());
                    }
                }

                if(symbol.getValor().contains("@")){
                    asmInstructions.add("\t" + symbol.getNombre() + "\t\tdd\t\t" + symbol.getValor());
                }
            } catch(Exception e){
                continue;
            }
        }

        asmInstructions.add("");
        asmInstructions.add("; Definicion de variables auxiliares");
        for (int i = 1; i <= tercetos.size(); i++) {
            asmInstructions.add("\t@aux" + i + "\t\tdd ?");
        }
    }

    private void generateTercetosCode() {

        Stack<String> operandos  = new Stack<String>();

        asmInstructions.add(".code");
        asmInstructions.add("START:");

        asmInstructions.add("\tMOV AX,@DATA");
        asmInstructions.add("\tMOV DS,AX");
        asmInstructions.add("\tMOV ES,AX");
        asmInstructions.add("");

        int tercetoIndex = 1;



        for (Terceto terceto : tercetos) {

            try {
                String destino = tercetoLabels.get(terceto.getIndice());
                if (destino == null) {
                    destino = "FINAL_LABEL";
                }

                String operador = terceto.getOperador();
                String operando1 = terceto.getElemento1();

                

                if(operador.contains(".")) {

                    String valor = operador;

                    // Formatear el valor
                    if (valor.startsWith(".")) {
                        valor = "0" + valor;  // Cambia ".9999" a "0.9999"
                    } else if (valor.endsWith(".")) {
                        valor = valor + "00";  // Cambia "99." a "99.00"
                    }

                    valor = valor.replace(".", "x");
                    valor = "_" + valor;

                    operador = valor;
                }

                if(operador.contains("\"")) { // el nombre del string no puede tener " "
                        String nombre_string = operador.replace("\"", "");
                        nombre_string = nombre_string.replace(' ', '_');

                        operador = "_" + nombre_string;
                }


                String operando2 = terceto.getElemento2();

                String ladoDerecho = "";
                String ladoIzquierdo = "";

                // Si el terceto destino tiene una etiqueta, la agregamos en el asm
                if (tercetoLabels.containsKey(tercetoIndex)) {
                    asmInstructions.add(tercetoLabels.get(tercetoIndex) + ":");
                }

                if (operando1.startsWith("terceto_")) {
                    operando1 = "@aux" + operando1.substring(8);
                }
                if (operando2.startsWith("terceto_")) {
                    operando2 = "@aux" + operando2.substring(8);
                }

                switch (operador) {
                    case ":=":

                        if (operando1.startsWith("terceto_")) {
                            operando1 = "@aux" + operando1.substring(8);
                        }

                        if (operando2.startsWith("terceto_")) {
                            operando2 = "@aux" + operando2.substring(8);
                        }

                        if (getSymbolType(operando2).equals("Constante")) {
                            asmInstructions.add("\tFLD _" + operando2);
                        } else {
                            asmInstructions.add("\tFLD " + operando2);
                        }

                        asmInstructions.add("\tFSTP " + operando1);
                        break;
                    case "CMP":

                        asmInstructions.add("");

                        if (operandos.size() >= 2) {
                            ladoIzquierdo = operandos.pop();
                            ladoDerecho = operandos.pop();
                        }
                            
                        // verifico si el lado derecho de la asignación es una variable o cte
                        if(getSymbolType(ladoDerecho).equals("Constante")) {
                            asmInstructions.add("\tFLD _" + ladoDerecho);
                        } else{
                            asmInstructions.add("\tFLD " + ladoDerecho);
                        }

                        // verifico si el lado izquierdo de la asignación es una variable o cte
                        if(getSymbolType(ladoIzquierdo).equals("Constante")) {
                            asmInstructions.add("\tFLD _" + ladoIzquierdo);
                        } else{
                            asmInstructions.add("\tFLD " + ladoIzquierdo);
                        }

                        asmInstructions.add("\tFXCH");

                        asmInstructions.add("\tFCOMP");
                        asmInstructions.add("\tFSTSW ax");
                        asmInstructions.add("\tFFREE st(0)");
                        asmInstructions.add("\tSAHF");

                        break;
                    case "BLE":
                        int destinoBLE = Integer.parseInt(operando1.replace("[", "").replace("]", ""));

                        if (tercetoLabels.get(destinoBLE) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJBE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }

                        asmInstructions.add("\tJBE " + tercetoLabels.get(destinoBLE));
                        asmInstructions.add("");
                        break;
                    case "BI":
                        int destinoBI = Integer.parseInt(operando1.replace("[", "").replace("]", ""));

                        if (tercetoLabels.get(destinoBI) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJMP FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }

                        asmInstructions.add("\tJMP " + tercetoLabels.get(destinoBI));
                        asmInstructions.add("");
                        break;
                    case "BGE":
                        int destinoBGE = Integer.parseInt(operando1.replace("[", "").replace("]", ""));

                        if (tercetoLabels.get(destinoBGE) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJAE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }

                        asmInstructions.add("\tJAE " + tercetoLabels.get(destinoBGE));
                        asmInstructions.add("");
                        break;
                    case "BLT":
                        int destinoBLT = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                    
                        if (tercetoLabels.get(destinoBLT) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJB FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }

                        asmInstructions.add("\tJB " + tercetoLabels.get(destinoBLT));
                        asmInstructions.add("");
                        break;
                    case "BEQ":
                        int destinoBEQ = Integer.parseInt(operando1.replace("[", "").replace("]", ""));

                        if (tercetoLabels.get(destinoBEQ) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }

                        asmInstructions.add("\tJE " + tercetoLabels.get(destinoBEQ));
                        asmInstructions.add("");
                        break;
                    case "BNE":
                        int destinoBNE = Integer.parseInt(operando1.replace("[", "").replace("]", ""));
                        
                        if (tercetoLabels.get(destinoBNE) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJNE FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }

                        asmInstructions.add("\tJNE " + tercetoLabels.get(destinoBNE));
                        asmInstructions.add("");
                        break;
                    case "BGT":
                        int destinoBGT = Integer.parseInt(operando1.replace("[", "").replace("]", ""));

                        if (tercetoLabels.get(destinoBGT) == null) { // si no hay más instrucciones salta a la etiqueta final
                            asmInstructions.add("\tJG FINAL_LABEL" );
                            asmInstructions.add("");
                            break;
                        }

                        asmInstructions.add("\tJG " + tercetoLabels.get(destinoBGT));
                        asmInstructions.add("");
                        break;
                    case "*":
                        asmInstructions.add("\tFLD " + operando1);
                        asmInstructions.add("\tFMUL " + operando2);
                        asmInstructions.add("\tFSTP @aux" + terceto.getIndice());
                        asmInstructions.add("");
                        break;
                    case "+":
                        asmInstructions.add("\tFLD " + operando1);
                        asmInstructions.add("\tFADD " + operando2);
                        asmInstructions.add("\tFSTP @aux" + terceto.getIndice());
                        asmInstructions.add("");
                        break;
                    case "-":
                        asmInstructions.add("\tFLD " + operando1);
                        asmInstructions.add("\tFSUB " + operando2);
                        asmInstructions.add("\tFSTP @aux" + terceto.getIndice());
                        asmInstructions.add("");
                        break;
                    case "/":
                        asmInstructions.add("\tFLD " + operando1);
                        asmInstructions.add("\tFDIV " + operando2);
                        asmInstructions.add("\tFSTP @aux" + terceto.getIndice());
                        asmInstructions.add("");
                        break;
                    case "READ":
                        // manejar caso READ

                    case "WRITE":

                        String mensaje = operando1.replace("\"", "").replace(" ", "_");
                        mensaje = "_" + mensaje;
                        
                        // Instrucciones asm para mostrar por pantalla un msj
                        asmInstructions.add("\tMOV dx,OFFSET " + mensaje);
                        asmInstructions.add("\tMOV ah,9");
                        asmInstructions.add("\tint 21h");
                        asmInstructions.add("");
                        break;

                    // Otros operadores
                    default:

                        if (terceto.getOperador().equals("terceto")) {
                            asmInstructions.add("\tFLD " + terceto.getElemento1());
                            asmInstructions.add("\tFSTP @aux" + terceto.getIndice());
                            asmInstructions.add("");
                        } else if (terceto.getOperador().matches("[0-9]+")) {
                            asmInstructions.add("\tMOV eax, " + terceto.getOperador());
                            asmInstructions.add("\tMOV @aux" + terceto.getIndice() + ", eax");
                            asmInstructions.add("");
                        } else if(!terceto.getOperador().contains("\"") && !terceto.getOperador().contains(".")){
                            // Asumimos que es una variable
                            asmInstructions.add("\tMOV eax, " + terceto.getOperador());
                            asmInstructions.add("\tMOV @aux" + terceto.getIndice() + ", eax");
                            asmInstructions.add("");
                        }
                        operandos.add(operador);

                    }
                    tercetoIndex++;
                } catch(Exception e) {
                    continue;
                }


            
        }

        asmInstructions.add("\tmov ax, 4C00h");
        asmInstructions.add("\tint 21h");
        asmInstructions.add("");
        asmInstructions.add("FINAL_LABEL:");
        asmInstructions.add("");
        asmInstructions.add("END START");
    }

    // public void printAsmInstructions() {
    //     for (String instruction : asmInstructions) {
    //         System.out.println(instruction);
    //     }
    // }

    public String getSymbolType(String symbolName) {


        for (Simbolo symbol : symbolTable) {
            if (symbol.getNombre().equals(symbolName)) {
                return "Variable";
            }
        }

        for (Simbolo symbol : symbolTable) {
            if (symbol.getNombre().equals("_" + symbolName)) {
                return "Constante";
            }
        }

 
        return "No encontrado";
    }


    public String tipoDeSimboloPorNombre(String nombre) {
        for (Simbolo symbol : symbolTable) {
            if (symbol.getNombre().equals(nombre)) {
                return symbol.getTipoDato();
            } else if (symbol.getNombre().equals("_" + nombre)) {
                return symbol.getTipoDato();
            }
        }
        return null; // Si no se encuentra el símbolo
    }

    // Busca el tipo de un símbolo reconstruyendo su nombre original
    public boolean buscarTipoString(String ladoDerecho) {

        String nombreOriginal = "\"" + ladoDerecho.substring(1).replace('_', ' ') + "\"";

        for (Simbolo symbol : symbolTable) {
            if (symbol.getNombre().equals(nombreOriginal) && symbol.getTipoDato().equals("String")) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getAsmInstructions() {
        return asmInstructions;
    }
}

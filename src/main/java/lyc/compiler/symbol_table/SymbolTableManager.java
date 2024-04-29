package lyc.compiler.symbol_table;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SymbolTableManager {

    private ArrayList<Simbolo> symbolTable = new ArrayList<>();

    public void addSymbol(Simbolo symbol) {
        symbolTable.add(symbol);
    }

    public void addVariablesFromArrayList(ArrayList<Simbolo> simbolosAAñadir, String tipoDato) {


        for (Simbolo elemento : simbolosAAñadir) {
            try {
                if (!symbolTable.contains(elemento)){
                    elemento.setTipoDato(tipoDato);
                    symbolTable.add(elemento);
                }
                    
            } catch (Exception e) {
                System.out.println("Error al procesar el símbolo: " + e.getMessage());
            }
        }
        
    }

    public void writeToFile(FileWriter fileWriter) throws IOException {
        for (Simbolo symbol : symbolTable) {
            fileWriter.write(symbol.getNombre() + " | " + symbol.getTipoDato() + " | " + symbol.getValor() + " | " + symbol.getLongitud() + "\n");
        }
    }

     public ArrayList<Simbolo> getSymbolTable() {
        return symbolTable;
    }

    public boolean containsSymbol(String nombreSimbolo) {
        for (Simbolo symbol : symbolTable) {
            if (symbol.getNombre().equals(nombreSimbolo)) {
                return true;
            }
        }
        return false;
    }

    
}
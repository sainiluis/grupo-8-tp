package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import lyc.compiler.symbol_table.*;
import java.util.ArrayList;

public class SymbolTableGenerator implements FileGenerator {

    // private SymbolTableManager symbolTableManager = new SymbolTableManager();

    // public void addSymbol(Simbolo symbol) {
    //     symbolTableManager.addSymbol(symbol);
    // }

    private final ArrayList<Simbolo> symbolTable;

    public SymbolTableGenerator(ArrayList<Simbolo> symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Simbolo symbol : symbolTable) {
            fileWriter.write(symbol.getNombre() + " | " + symbol.getTipoDato() + " | " + symbol.getValor() + " | " + symbol.getLongitud() + "\n");
        }
    }
}

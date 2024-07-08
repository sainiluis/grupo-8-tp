package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AsmCodeGenerator implements FileGenerator {

    private ArrayList<String> asmInstructions;

    public AsmCodeGenerator(ArrayList<String> asmInstructions) {
        this.asmInstructions = asmInstructions;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (String instruction : asmInstructions) {
            fileWriter.write(instruction + System.lineSeparator());
        }
    }
}

package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import lyc.compiler.gci.*;

public class IntermediateCodeGenerator implements FileGenerator {

    private final ArrayList<Terceto> intermediateCode;

    public IntermediateCodeGenerator(ArrayList<Terceto> intermediateCode) {
        this.intermediateCode = intermediateCode;
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        for (Terceto terceto : intermediateCode) {
            fileWriter.write(terceto.toString() + "\n");
        }
    }
}
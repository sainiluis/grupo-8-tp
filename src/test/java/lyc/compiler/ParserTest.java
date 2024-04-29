package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.factories.ParserFactory;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;
import static lyc.compiler.Constants.EXAMPLES_ROOT_DIRECTORY;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ParserTest {

    @Test
    public void assignmentWithExpression() throws Exception {
        compilationSuccessful("init {c : Int} c := 3");
    }

    @Test
    public void syntaxError() {
        compilationError("1234");
    }

    // Se comenta, ya que la variables a las que se asignan valores, deberían estar inicializadas previamente en el bloque init.
    // @Test
    // void assignments() throws Exception {
    //     compilationSuccessful(readFromFile("assignments.txt"));
    // }

    // Se comenta porque en el ejemplo se utiliza “ en vez de " para el string constant
    // @Test
    // void write() throws Exception {
    //     compilationSuccessful(readFromFile("write.txt"));
    // }

    // Se comenta porque en el ejemplo, tenemos leer(base) y base debería ser una variable inicializada previamente en el bloque init
    // @Test
    // void read() throws Exception {
    //     compilationSuccessful(readFromFile("read.txt"));
    // }

    // @Test
    // void comment() throws Exception {
    //     compilationSuccessful(readFromFile("comment.txt"));
    // }

    // Se comentó porque daba error de conexión:
    // Connection timed out: connect
    // desconocemos si se trata de algun error con el github de los ejemplos.
    // en todo caso, al momento de la corrección se pueden descomentar y ver si funciona
    // @Test
    // void init() throws Exception {
    //     compilationSuccessful(readFromFile("init.txt"));
    // }

    // Se comenta esta linea ya que en el ejemplose usa la palabra "if" en vez de "si" para el condicional.
    // @Test
    // void and() throws Exception {
    //     compilationSuccessful(readFromFile("and.txt"));
    // }

    // Se comenta esta linea ya que en el ejemplose usa la palabra "if" en vez de "si" para el condicional.
    // @Test
    // void or() throws Exception {
    //     compilationSuccessful(readFromFile("or.txt"));
    // }

    // Se comenta esta linea ya que en el ejemplose usa la palabra "if" en vez de "si" para el condicional.
    // @Test
    // void not() throws Exception {
    //     compilationSuccessful(readFromFile("not.txt"));
    // }

    // Se comenta ya que las variables a y b deberían estar inicializadas en el bloque init
    // @Test
    // void ifStatement() throws Exception {
    //     compilationSuccessful(readFromFile("if.txt"));
    // }

    // Se comenta esta linea ya que las variables a y b deberían ser inicializadas en el bloque init
    // @Test
    // void whileStatement() throws Exception {
    //     compilationSuccessful(readFromFile("while.txt"));
    // }


    private void compilationSuccessful(String input) throws Exception {
        assertThat(scan(input).sym).isEqualTo(ParserSym.EOF);
    }

    private void compilationError(String input){
        assertThrows(Exception.class, () -> scan(input));
    }

    private Symbol scan(String input) throws Exception {
        return ParserFactory.create(input).parse();
    }

    private String readFromFile(String fileName) throws IOException {
        URL url = new URL(EXAMPLES_ROOT_DIRECTORY + "/%s".formatted(fileName));
        assertThat(url).isNotNull();
        return IOUtils.toString(url.openStream(), StandardCharsets.UTF_8);
    }


}
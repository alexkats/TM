/**
 * @author Alexey Katsman
 * @since 25.05.16
 */

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;

public class Obfuscator {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = new FileInputStream(new File("Main.java"));
        ANTLRInputStream antlrInputStream = new ANTLRInputStream(inputStream);
        JavaLexer lexer = new JavaLexer(antlrInputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.start();
        ParseTreeWalker walker = new ParseTreeWalker();
        ObfuscatorListener listener = new ObfuscatorListener();
        PrintStream consoleOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(outputStream);
        System.setOut(stream);
        walker.walk(listener, tree);
        String result = outputStream.toString();
        System.setOut(consoleOut);
        PrintWriter out = new PrintWriter(new FileWriter("Obfuscated.java"));
        out.print(result);
        out.close();
    }
}

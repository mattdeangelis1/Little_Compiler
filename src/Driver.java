import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;

public class Driver {
    public static void main(String[] args) throws Exception {

        System.out.println(";IR code");

        CharStream input = CharStreams.fromStream(System.in);

        LittleLexer littleLexer = new LittleLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(littleLexer);
//
//        class ErrorListener extends BaseErrorListener {
//            @Override
//            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
//
//                String error = "Not Accepted";
//
//                System.err.println(error);
//
//
//		System.exit(1);
//            }
//        }

        LittleParser parser = new LittleParser(tokens);

//        parser.removeErrorListeners();
//        parser.addErrorListener(new ErrorListener());
	
//	    parser.addErrorListener(ConsoleErrorListener.INSTANCE);

        ParseTree tree = parser.program();
	
//	    System.out.println("Accepted");

        ParseTreeWalker walker = new ParseTreeWalker();
        SimpleTableBuilder stb = new SimpleTableBuilder();

        walker.walk(stb, tree);
        stb.prettyPrint();

    }
}

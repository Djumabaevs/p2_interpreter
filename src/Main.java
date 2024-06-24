import java.util.List;

public class Main {
    public static void main(String[] args) {
        String sourceCode = 
            "x = 10 + 5; \n" +
            "print(x); \n" +
            "if (x > 15) { \n" +
            "  print(\"Greater than 15\"); \n" +
            "} else { \n" +
            "  print(\"Less than or equal to 15\"); \n" +
            "} \n" +
            "while (x > 0) { \n" +
            "  print(x); \n" +
            "  x = x - 1; \n" +
            "}";

        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.tokenize();
        System.out.println("Tokens:");
        for (Token token : tokens) {
            System.out.println(token.type + " (" + token.value + ")");
        }

        Parser parser = new Parser(tokens);
        Parser.ASTNode ast = parser.parse();
        System.out.println("\nAbstract Syntax Tree (AST):");
        ast.print(0);

      
    }
}

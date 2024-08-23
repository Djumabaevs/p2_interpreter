import java.util.ArrayList;
import java.util.List;
//commit

public class Parser {
    private List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ASTNode parse() {
        return program();
    }

    private ASTNode program() {
        ASTNode program = new ASTNode("Program");
        while (pos < tokens.size() - 1) { 
            program.addChild(statement());
        }
        return program;
    }

    private ASTNode statement() {
        Token token = peek();
        System.out.println("Processing token: " + token.type + " (" + token.value + ")");
        switch (token.type) {
            case WHILE:
                return whileStatement();
            case IF:
                return ifStatement();
            case PRINT:
                return printStatement();
            case IDENTIFIER:
                if (lookahead(1).type == TokenType.ASSIGN) {
                    return assignment();
                } else {
                    return expression();  
                }
            case LPAREN:
                return expression();  
            case STRING:
                return new ASTNode("String", consume().value);  
            case SEMICOLON:
                consume(TokenType.SEMICOLON); 
                return new ASTNode("EmptyStatement");
            case LBRACE:
                return block(); 
            default:
                throw new RuntimeException("Invalid statement: " + token.value);
        }
    }

    private Token peek() {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return new Token(TokenType.EOF, ""); 
    }

    private Token lookahead(int distance) {
        if (pos + distance < tokens.size()) {
            return tokens.get(pos + distance);
        }
        return new Token(TokenType.EOF, "");
    }

    private ASTNode printStatement() {
        ASTNode printStatement = new ASTNode("PrintStatement");
        consume(TokenType.PRINT);
        consume(TokenType.LPAREN);
        printStatement.addChild(expression());
        consume(TokenType.RPAREN);
        consume(TokenType.SEMICOLON);
        return printStatement;
    }

    private ASTNode block() {
        ASTNode block = new ASTNode("Block");
        consume(TokenType.LBRACE); 
        while (peek().type != TokenType.RBRACE && peek().type != TokenType.EOF) {
            block.addChild(statement());
        }
        consume(TokenType.RBRACE);  
        return block;
    }

    private ASTNode assignment() {
        ASTNode assignment = new ASTNode("Assignment");
        assignment.addChild(new ASTNode("Identifier", consume().value));
        consume(TokenType.ASSIGN);
        assignment.addChild(expression());
        consume(TokenType.SEMICOLON);
        return assignment;
    }

    private ASTNode ifStatement() {
        ASTNode ifStatement = new ASTNode("IfStatement");
        consume(TokenType.IF);
        consume(TokenType.LPAREN);
        ifStatement.addChild(expression());
        consume(TokenType.RPAREN);
        ifStatement.addChild(block());

        if (peek().type == TokenType.ELSE) {
            consume(TokenType.ELSE);
            ifStatement.addChild(block());
        }

        return ifStatement;
    }

    private ASTNode whileStatement() {
        ASTNode whileStatement = new ASTNode("WhileStatement");
        consume(TokenType.WHILE);
        consume(TokenType.LPAREN);
        whileStatement.addChild(new ASTNode("Identifier", "x")); 
        consume(TokenType.GREATER_THAN); 
        whileStatement.addChild(new ASTNode("Number", "0")); 
        consume(TokenType.RPAREN);
        whileStatement.addChild(block());
        return whileStatement;
    }


    private ASTNode expression() {
        ASTNode expression = new ASTNode("Expression");
        expression.addChild(term()); 

        while (peek().type == TokenType.PLUS || peek().type == TokenType.MINUS || peek().type == TokenType.GREATER_THAN) {
            Token operator = consume();
            expression.addChild(new ASTNode("Operator", operator.value)); 
            expression.addChild(term()); 
        }
        return expression;
    }

    private ASTNode term() {
        ASTNode term = new ASTNode("Term");
        term.addChild(factor()); 

        while (peek().type == TokenType.STAR || peek().type == TokenType.SLASH) {
            Token operator = consume();
            term.addChild(new ASTNode("Operator", operator.value)); 
            term.addChild(factor());
        }
        return term;
    }

    private ASTNode factor() {
        Token token = peek();
        if (token.type == TokenType.NUMBER) {
            return new ASTNode("Number", consume().value);
        } else if (token.type == TokenType.IDENTIFIER) {
            return new ASTNode("Identifier", consume().value);
        } else if (token.type == TokenType.STRING) {
            return new ASTNode("String", consume().value);
        } else if (token.type == TokenType.LPAREN) { 
            consume(TokenType.LPAREN);
            ASTNode expression = expression(); 
            consume(TokenType.RPAREN);
            return expression;
        }
        throw new RuntimeException("Invalid factor: " + token.value);
    }

    private Token consume() {
        if (pos < tokens.size()) {
            return tokens.get(pos++);
        }
        throw new RuntimeException("Unexpected end of input");
    }

    private Token consume(TokenType expectedType) {
        Token token = consume();
        if (token.type != expectedType) {
            throw new RuntimeException("Expected: " + expectedType + ", Found: " + token.type);
        }
        return token;
    }

    public static class ASTNode {
        public String name;
        public String value;
        public List<ASTNode> children = new ArrayList<>();

        public ASTNode(String name) {
            this.name = name;
        }

        public ASTNode(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public void addChild(ASTNode child) {
            children.add(child);
        }

        public void print(int level) {
            for (int i = 0; i < level; i++) {
                System.out.print("  ");
            }
            System.out.println(name + (value != null ? " (" + value + ")" : ""));
            for (ASTNode child : children) {
                child.print(level + 1);
            }
        }
    }
}

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char ch = input.charAt(pos);
            if (Character.isWhitespace(ch)) {
                pos++;
                continue;
            }
            if (Character.isLetter(ch)) {
                tokens.add(readIdentifier());
            } else if (Character.isDigit(ch)) {
                tokens.add(readNumber());
            } else {
                switch (ch) {
                    case '=':
                        tokens.add(new Token(TokenType.ASSIGN, "="));
                        pos++;
                        break;
                    case '+':
                        tokens.add(new Token(TokenType.PLUS, "+"));
                        pos++;
                        break;
                    case '-':
                        tokens.add(new Token(TokenType.MINUS, "-"));
                        pos++;
                        break;
                    case '*':
                        tokens.add(new Token(TokenType.MULTIPLY, "*"));
                        pos++;
                        break;
                    case '/':
                        tokens.add(new Token(TokenType.DIVIDE, "/"));
                        pos++;
                        break;
                    case '(':
                        tokens.add(new Token(TokenType.LPAREN, "("));
                        pos++;
                        break;
                    case ')':
                        tokens.add(new Token(TokenType.RPAREN, ")"));
                        pos++;
                        break;
                    case '{':
                        tokens.add(new Token(TokenType.LBRACE, "{"));
                        pos++;
                        break;
                    case '}':
                        tokens.add(new Token(TokenType.RBRACE, "}"));
                        pos++;
                        break;
                    case ';':
                        tokens.add(new Token(TokenType.SEMICOLON, ";"));
                        pos++;
                        break;
                    case '>':
                        tokens.add(new Token(TokenType.GREATER_THAN, ">"));
                        pos++;
                        break;
                    case '"':
                        tokens.add(readStringLiteral());
                        break;
                    case 'i':
                        if (input.startsWith("if", pos)) {
                            tokens.add(new Token(TokenType.IF, "if"));
                            pos += 2;
                        } else {
                            tokens.add(readIdentifier());
                        }
                        break;
                    case 'w':
                        if (input.startsWith("while", pos)) {
                            tokens.add(new Token(TokenType.WHILE, "while"));
                            pos += 5;
                        } else {
                            tokens.add(readIdentifier());
                        }
                        break;
                    case 'p':
                        if (input.startsWith("print", pos)) {
                            tokens.add(new Token(TokenType.PRINT, "print"));
                            pos += 5;
                        } else {
                            tokens.add(readIdentifier());
                        }
                        break;
                    case 'e':
                        if (input.startsWith("else", pos)) {
                            tokens.add(new Token(TokenType.ELSE, "else"));
                            pos += 4;
                        } else {
                            tokens.add(readIdentifier());
                        }
                        break;
                    default:
                        throw new RuntimeException("Invalid character: " + ch);
                }
            }
        }
        return tokens;
    }

    private Token readIdentifier() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && (Character.isLetterOrDigit(input.charAt(pos)))) {
            sb.append(input.charAt(pos));
            pos++;
        }
        return new Token(TokenType.IDENTIFIER, sb.toString());
    }

    private Token readNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }
        return new Token(TokenType.NUMBER, sb.toString());
    }

    private Token readStringLiteral() {
        StringBuilder sb = new StringBuilder();
        pos++; 
        while (pos < input.length() && input.charAt(pos) != '"') {
            sb.append(input.charAt(pos));
            pos++;
        }
        pos++;
        return new Token(TokenType.STRING, sb.toString());
    }
}
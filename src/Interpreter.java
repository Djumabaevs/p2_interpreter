import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    private Parser.ASTNode ast;
    private Map<String, Integer> variables = new HashMap<>();

    public Interpreter(Parser.ASTNode ast) {
        this.ast = ast;
    }

    public void interpret() {
        interpretNode(ast);
    }

    private void interpretNode(Parser.ASTNode node) {
        System.out.println("Interpreting node: " + node.name + (node.value != null ? " (" + node.value + ")" : ""));
        switch (node.name) {
            case "Program":
                for (Parser.ASTNode child : node.children) {
                    interpretNode(child);
                }
                break;
            case "Assignment":
                String identifier = node.children.get(0).value;
                int value = evaluateExpression(node.children.get(1));
                variables.put(identifier, value);
                System.out.println("Assigned " + identifier + " = " + value);
                break;
            case "IfStatement":
                if (evaluateExpression(node.children.get(0)) != 0) {
                    interpretNode(node.children.get(1));
                } else if (node.children.size() > 2) {
                    interpretNode(node.children.get(2));
                }
                break;
            case "WhileStatement":
                while (evaluateExpression(node.children.get(0)) != 0) { 
                    interpretNode(node.children.get(1)); 
                    variables.put("x", variables.get("x") - 1); 
                }
                break;
            case "PrintStatement":
                Parser.ASTNode child = node.children.get(0);
                if (child.name.equals("String")) {
                    System.out.println(child.value);
                } else {
                    System.out.println(evaluateExpression(child));
                }
                break;
            case "Block":
                for (Parser.ASTNode childNode : node.children) {
                    interpretNode(childNode);
                }
                break;
            case "EmptyStatement":
                break;
            case "Expression":
            case "Term":
            case "Factor":
                evaluateExpression(node);
                break;
            default:
                throw new RuntimeException("Invalid node: " + node.name);
        }
    }

    private int evaluateExpression(Parser.ASTNode node) {
        System.out.println("Evaluating expression: " + node.name);

        if (node.children.size() == 1) {
            return evaluateTerm(node.children.get(0));
        } else {
            int result = evaluateTerm(node.children.get(0));

            for (int i = 1; i < node.children.size(); i += 2) {
                if (i + 1 >= node.children.size()) {
                    throw new RuntimeException("Incomplete expression at node: " + node.name);
                }

                Parser.ASTNode operator = node.children.get(i);
                int operand = evaluateTerm(node.children.get(i + 1)); 

                switch (operator.value) {
                    case "+":
                        result += operand;
                        break;
                    case "-":
                        result -= operand;
                        break;
                    case ">":
                        result = result > operand ? 1 : 0;
                        break;
                    default:
                        throw new RuntimeException("Invalid operator: " + operator.value);
                }
            }
            return result;
        }
    }

    private int evaluateTerm(Parser.ASTNode node) {
        System.out.println("Evaluating term: " + node.name);
        if (node.children.size() == 1) {
            return evaluateFactor(node.children.get(0));
        } else {
            int result = evaluateFactor(node.children.get(0));
            for (int i = 1; i < node.children.size(); i += 2) {
                if (i + 1 >= node.children.size()) {
                    throw new RuntimeException("Incomplete term at node: " + node.name);
                }
                Parser.ASTNode operator = node.children.get(i);
                int operand = evaluateFactor(node.children.get(i + 1));
                switch (operator.value) {
                    case "*":
                        result *= operand;
                        break;
                    case "/":
                        result /= operand;
                        break;
                    default:
                        throw new RuntimeException("Invalid operator: " + operator.value);
                }
            }
            return result;
        }
    }

    private int evaluateFactor(Parser.ASTNode node) {
        System.out.println("Evaluating factor: " + node.name + (node.value != null ? " (" + node.value + ")" : ""));
        switch (node.name) {
            case "Number":
                return Integer.parseInt(node.value);
            case "Identifier":
                return variables.getOrDefault(node.value, 0);
            case "Expression":
                return evaluateExpression(node);
            case "String":
                System.out.println(node.value);
                return 0;
            default:
                throw new RuntimeException("Unknown factor: " + node.name);
        }
    }
}

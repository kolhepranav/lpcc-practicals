import java.util.*;

public class ThreeAddressCodeGenerator {
    // Temporary variable counter (t1, t2, t3, ...)
    private static int tempVarCount = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for the input expression
        System.out.println("Enter the expression (e.g., a = b*b + c*c - d):");
        String input = scanner.nextLine().replaceAll("\\s+", ""); // Remove spaces

        // Basic validation: check for '=' in the input
        if (!input.contains("=")) {
            System.out.println("Invalid expression! Must be in the form: variable = expression");
            scanner.close();
            return;
        }

        // Split the input into LHS and RHS
        String[] parts = input.split("=");
        String lhs = parts[0]; // Left-hand side variable (e.g., a)
        String rhs = parts[1]; // Right-hand side expression (e.g., b*b + c*c - d)

        // Generate the TAC for the right-hand expression
        List<String> tac = generateTAC(rhs);

        // Print all intermediate code lines
        for (String line : tac) {
            System.out.println(line);
        }

        // Final assignment: a = tX
        System.out.println(lhs + " = " + tempVar("peek"));
        scanner.close(); // Close the scanner
    }

    // Function to generate Three Address Code using Shunting Yard logic
    private static List<String> generateTAC(String expr) {
        Stack<String> operands = new Stack<>();     // Stack to store operands (variables and temp)
        Stack<Character> operators = new Stack<>(); // Stack to store operators (+, -, *, /, etc.)
        List<String> tac = new ArrayList<>();       // Output list for TAC lines

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            // If the character is a variable or number
            if (Character.isLetterOrDigit(c)) {
                StringBuilder operand = new StringBuilder();
                while (i < expr.length() && Character.isLetterOrDigit(expr.charAt(i))) {
                    operand.append(expr.charAt(i));
                    i++;
                }
                i--; // Go one step back since loop moves ahead
                operands.push(operand.toString());
            }
            // If opening bracket, push to operators stack
            else if (c == '(') {
                operators.push(c);
            }
            // If closing bracket, pop until opening bracket is found
            else if (c == ')') {
                while (operators.peek() != '(') {
                    processOperator(operators.pop(), operands, tac);
                }
                operators.pop(); // Remove the '('
            }
            // If the character is an operator
            else if (isOperator(c)) {
                // Handle operator precedence
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    processOperator(operators.pop(), operands, tac);
                }
                operators.push(c);
            }
        }

        // Process remaining operators
        while (!operators.isEmpty()) {
            processOperator(operators.pop(), operands, tac);
        }

        return tac;
    }

    // Generate a TAC line and push result back to operand stack
    private static void processOperator(char op, Stack<String> operands, List<String> tac) {
        String right = operands.pop(); // Right operand
        String left = operands.pop();  // Left operand
        String temp = tempVar(null);   // Generate a new temp variable
        tac.add(temp + " = " + left + " " + op + " " + right); // Add TAC line
        operands.push(temp); // Push result back for future computation
    }

    // Function to get next temporary variable (t1, t2, t3, ...)
    private static String tempVar(String peek) {
        if ("peek".equals(peek)) {
            return "t" + (tempVarCount - 1); // Return the latest temp var
        }
        return "t" + (tempVarCount++); // Return and increment temp var count
    }

    // Check if a character is an operator
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    // Define precedence of operators (higher value means higher precedence)
    private static int precedence(char op) {
        switch (op) {
            case '^': return 3;
            case '*': case '/': return 2;
            case '+': case '-': return 1;
            default: return 0;
        }
    }
}
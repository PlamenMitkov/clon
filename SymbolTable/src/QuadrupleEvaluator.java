import java.util.*;

public class QuadrupleEvaluator {
    private final Map<String, Integer> variableValues = new HashMap<>();
    private final Set<String> allVariables = new TreeSet<>(); // Sorted set for all variables

    /**
     * Evaluates the list of quadruples and calculates variable values.
     *
     * @param quadruples The list of quadruples to evaluate.
     */
    public void evaluateQuadruples(List<Quadruple> quadruples) {
        int programCounter = 0; // Track the current quadruple index
        int maxIterations = 30; // Safety limit to prevent infinite loops
        int iterationCount = 0;

        System.out.println("\n[DEBUG] Starting quadruple evaluation...");
        while (programCounter < quadruples.size()) {
            if (iterationCount++ > maxIterations) {
                throw new IllegalStateException("Infinite loop detected during quadruple evaluation.");
            }

            Quadruple quadruple = quadruples.get(programCounter);
            System.out.printf("[DEBUG] Executing Quadruple %d: %s\n", programCounter, quadruple);


            switch (quadruple.operator) {
                case "=": // Assignment
                    variableValues.put(quadruple.result, getValue(quadruple.arg1));
                    addVariable(quadruple.result);
                    programCounter++;
                    break;

                case "+": // Addition
                    variableValues.put(quadruple.result,
                            getValue(quadruple.arg1) + getValue(quadruple.arg2));
                    addVariable(quadruple.result);
                    programCounter++;
                    break;

                case "-": // Subtraction
                    variableValues.put(quadruple.result,
                            getValue(quadruple.arg1) - getValue(quadruple.arg2));
                    addVariable(quadruple.result);
                    programCounter++;
                    break;

                case "*": // Multiplication
                    variableValues.put(quadruple.result,
                            getValue(quadruple.arg1) * getValue(quadruple.arg2));
                    addVariable(quadruple.result);
                    programCounter++;
                    break;

                case "/": // Division
                    variableValues.put(quadruple.result,
                            getValue(quadruple.arg1) / getValue(quadruple.arg2));
                    addVariable(quadruple.result);
                    programCounter++;
                    break;

                case ">": // Greater than
                    variableValues.put(quadruple.result,
                            getValue(quadruple.arg1) > getValue(quadruple.arg2) ? 1 : 0);
                    addVariable(quadruple.result);
                    programCounter++;
                    break;

                case "<": // Less than
                    variableValues.put(quadruple.result,
                            getValue(quadruple.arg1) < getValue(quadruple.arg2) ? 1 : 0);
                    addVariable(quadruple.result);
                    programCounter++;
                    break;

                case "JMP_FALSE": // Conditional jump
                    if (getValue(quadruple.arg1) == 0) {
                        System.out.printf("[DEBUG] JMP_FALSE triggered. Jumping to %s\n", quadruple.result);
                        programCounter = Integer.parseInt(quadruple.result);
                    } else {
                        programCounter++;
                    }
                    break;

                case "JMP":
                    int jumpTarget = Integer.parseInt(quadruple.result);
                    if (jumpTarget < 0 || jumpTarget >= quadruples.size()) {
                        throw new IllegalStateException("Invalid jump target: " + jumpTarget);
                    }
                    System.out.printf("[DEBUG] JMP triggered. Jumping to %d\n", jumpTarget);
                    programCounter = jumpTarget;
                    break;

                case "WRITE": // Output
                    System.out.println("[OUTPUT] " + getValue(quadruple.arg1));
                    programCounter++;
                    break;

                case "READ": // Input
                    System.out.print("Enter value for " + quadruple.result + ": ");
                    Scanner scanner = new Scanner(System.in);
                    int inputValue = scanner.nextInt();
                    variableValues.put(quadruple.result, inputValue);
                    addVariable(quadruple.result);
                    programCounter++;
                    break;

                case "STOP": // Program termination
                    System.out.println("[DEBUG] STOP encountered. Ending execution.");
                    programCounter = quadruples.size(); // Exit evaluation
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported operator: " + quadruple.operator);
            }

            // Log current variable state after each step
            System.out.printf("[DEBUG] Step %d - Current Variable Values: %s\n", programCounter, variableValues);
        }

        System.out.println("[DEBUG] Evaluation complete.");
        printAllVariables(); // Show all variables at the end
    }

    /**
     * Adds a variable to the set of all variables.
     *
     * @param variable The variable name to track.
     */
    private void addVariable(String variable) {
        if (variable != null && !variable.isEmpty()) {
            allVariables.add(variable);
        }
    }

    /**
     * Gets the integer value of an argument (variable or constant).
     *
     * @param arg The argument to evaluate.
     * @return The value of the argument.
     */
    private int getValue(String arg) {
        if (arg.matches("-?\\d+")) { // Check if it's a number
            return Integer.parseInt(arg);
        } else if (variableValues.containsKey(arg)) { // Check if it's a variable
            return variableValues.get(arg);
        } else {
            throw new IllegalStateException("Undefined variable: " + arg);
        }
    }
    
    /**
     * Prints the quadruples with all variable values, including temporary variables.
     *
     * @param quadruples The list of quadruples to display.
     */
    public void printQuadruplesWithValues(List<Quadruple> quadruples) {
        System.out.printf("%-5s %-10s %-10s %-10s %-10s \n",
                "Index", "Operator", "Arg1", "Arg2", "Result");
        System.out.println("-------------------------------------------------------------");

        for (int i = 0; i < quadruples.size(); i++) {
            Quadruple q = quadruples.get(i);
            System.out.printf("%-5d %-10s %-10s %-10s %-10s \n",
                    i, q.operator, q.arg1, q.arg2, q.result);
        }
    }

    /**
     * Prints all variables (both user-defined and temporaries) with their values.
     */
    public void printAllVariables() {
        System.out.println("\nFinal Variable Values:");
        System.out.println("-----------------------");
        for (String variable : allVariables) {
            System.out.printf("%-10s : %d\n", variable, variableValues.getOrDefault(variable, 0));
        }
    }
}

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Step 1: Get input source (manual input or file)
            String input = getInputSource();

            // Step 2: Initialize components
            SymbolTable symbolTable = new SymbolTable();
            IntermediateCodeGenerator intermediateCodeGenerator = new IntermediateCodeGenerator();
            QuadrupleEvaluator evaluator = new QuadrupleEvaluator();
            CodeGenerator codeGenerator = new CodeGenerator();

            // Step 3: Perform lexical analysis
            Lexer lexer = new Lexer(input);
            lexer.tokenize(); // Generate tokens
            List<Token> tokens = lexer.getTokens();

            // Step 4: Display tokens
            System.out.println("Tokens:");
            tokens.forEach(System.out::println);

            // Step 5: Parse the tokens and generate quadruples
            Parser parser = new Parser(tokens, symbolTable, intermediateCodeGenerator);
            parser.parseZ();
            System.out.println("\nParsing completed successfully!");

            // Step 6: Display quadruples
            List<Quadruple> quadruples = intermediateCodeGenerator.getQuadruples();
            System.out.println("\nQuadruples:");
            evaluator.printQuadruplesWithValues(quadruples);

            // Step 7: Evaluate quadruples
            System.out.println("\nEvaluating quadruples...");
            evaluator.evaluateQuadruples(quadruples);

            // Step 8: Display quadruples with final values
            System.out.println("\nFinal Quadruples with Variable Values:");
        //    evaluator.printQuadruplesWithValues_1(quadruples);
            
            // Step 9: Generate assembly code
            String outputFileName = "output.asm";
            codeGenerator.generateAssembly(intermediateCodeGenerator.getQuadruples(), outputFileName);
            System.out.println("\nAssembly code has been generated in: " + outputFileName);
            
            // Step 10 Print All value
            evaluator.printAllVariables();

        } catch (Exception e) {
            // Handle exceptions gracefully
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves the input source for the program.
     *
     * @return The program as a string.
     * @throws Exception If input retrieval fails.
     */
    private static String getInputSource() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select input method: ");
        System.out.println("1. Enter code manually");
        System.out.println("2. Load code from a file");
        System.out.print("Your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (choice == 1) {
            System.out.println("Enter your program below (type 'END' on a new line to finish):");
            StringBuilder input = new StringBuilder();
            while (true) {
                String line = scanner.nextLine();
                if ("END".equalsIgnoreCase(line)) {
                    break;
                }
                input.append(line).append("\n");
            }
            return input.toString();

        } else if (choice == 2) {
            System.out.print("Enter the file path: ");
            String filePath = scanner.nextLine();
            return new String(Files.readAllBytes(Paths.get(filePath)));

        } else {
            throw new IllegalArgumentException("Invalid choice. Please select 1 or 2.");
        }
    }
}

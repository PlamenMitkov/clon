import java.io.PrintWriter;
import java.util.List;

public class CodeGenerator {

    /**
     * Generates assembly code from a list of quadruples.
     * @param quadruples The list of quadruples representing intermediate code.
     * @param outputFileName The file to which assembly code will be written.
     */
    public void generateAssembly(List<Quadruple> quadruples, String outputFileName) {
        try (PrintWriter writer = new PrintWriter(outputFileName)) {
            writer.println("; Assembly Code Generated from Quadruples");
            writer.println("; ----------------------------------------");

            for (Quadruple quadruple : quadruples) {
                switch (quadruple.operator) {
                    case "+": // Addition
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("ADD " + quadruple.arg2);
                        writer.println("STORE " + quadruple.result);
                        break;

                    case "-": // Subtraction
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("SUB " + quadruple.arg2);
                        writer.println("STORE " + quadruple.result);
                        break;

                    case "*": // Multiplication
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("MUL " + quadruple.arg2);
                        writer.println("STORE " + quadruple.result);
                        break;

                    case "/": // Division
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("DIV " + quadruple.arg2);
                        writer.println("STORE " + quadruple.result);
                        break;

                    case "<": // Less Than
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("CMP " + quadruple.arg2);
                        writer.println("BRL " + quadruple.result);
                        break;

                    case "<=": // Less Than or Equal
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("CMP " + quadruple.arg2);
                        writer.println("BRLE " + quadruple.result);
                        break;

                    case ">": // Greater Than
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("CMP " + quadruple.arg2);
                        writer.println("BRG " + quadruple.result);
                        break;

                    case ">=": // Greater Than or Equal
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("CMP " + quadruple.arg2);
                        writer.println("BRGE " + quadruple.result);
                        break;

                    case "==": // Equality
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("CMP " + quadruple.arg2);
                        writer.println("BREQ " + quadruple.result);
                        break;

                    case "!=": // Not Equal
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("CMP " + quadruple.arg2);
                        writer.println("BRNE " + quadruple.result);
                        break;

                    case "=": // Assignment
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("STORE " + quadruple.result);
                        break;

                    case "JMP": // Unconditional Jump
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("JUMP " + "LABEL" +quadruple.number);
                        break;

                    case "JMP_FALSE": // Conditional Jump (false)
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("LOAD " + quadruple.arg1);
                        writer.println("BRZ " + "LABEL" + quadruple.number);
                        break;

                    case "WRITE": // Write Output
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("OUT " + quadruple.arg1);
                        break;

                    case "READ": // Read Input
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("IN " + quadruple.result);
                        break;
                        
                    case "STOP":
                    	writer.println("LABEL" + quadruple.number);
                        writer.println("HALT");
                        break;

                    default: // Unsupported operator
                        throw new IllegalArgumentException("Unsupported operator: " + quadruple.operator);
                }
            }

            writer.println("; End of Assembly Code");

        } catch (Exception e) {
            System.err.println("Error writing assembly code: " + e.getMessage());
        }
    }
}

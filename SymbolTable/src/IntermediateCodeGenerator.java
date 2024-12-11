import java.util.ArrayList;
import java.util.List;

public class IntermediateCodeGenerator {
    private final List<Quadruple> quadruples = new ArrayList<>();
    private int tempCounter = 0;

    /**
     * Generates a new temporary variable.
     */
    public String getNextTemp() {
        return "&" + (++tempCounter);
    }

    /**
     * Adds a quadruple to the end of the list.
     */
    public int addQuadruple(String operator, String arg1, String arg2, String result) {
        Quadruple quadruple = new Quadruple(quadruples.size(),operator, arg1, arg2, result);
        quadruples.add(quadruple);
        return quadruples.size() - 1;
    }

    /**
     * Inserts a quadruple at the beginning of the list.
     * All jump indices referring to other quadruples must be adjusted.
     */
    public void addQuadrupleAtStart(String operator, String arg1, String arg2, String result) {
        Quadruple newQuadruple = new Quadruple(quadruples.size(),operator, arg1, arg2, result);

        // Insert at the beginning of the list
        quadruples.add(0, newQuadruple);

        // Adjust all jump references in the existing quadruples
        for (Quadruple q : quadruples) {
            if (q.result.matches("\\d+")) { // Only adjust if result is numeric (jump indices)
                int index = Integer.parseInt(q.result);
                q.result = String.valueOf(index + 1); // Shift by 1 because of the new quadruple
            }
        }
    }
    
    public void addStopQuadruple() {
        addQuadruple("STOP", null, null, null); // Add STOP as the final quadruple
    }


    /**
     * Updates the result of an existing quadruple.
     */
    public void updateQuadruple(int index, String result) {
        Quadruple quadruple = quadruples.get(index);
        quadruple.result = result;
    }

    /**
     * Retrieves the index of the next quadruple to be added.
     */
    public int getNextQuadrupleIndex() {
        return quadruples.size();
    }

    /**
     * Returns the list of all quadruples.
     */
    public List<Quadruple> getQuadruples() {
        return quadruples;
    }

    /**
     * Prints all quadruples.
     */
    public void printQuadruples() {
        System.out.println("Index  Operator     Arg1       Arg2       Result");
        System.out.println("------------------------------------------------");
        for (Quadruple quadruple : quadruples) {
            System.out.println(quadruple);
        }
    }
}

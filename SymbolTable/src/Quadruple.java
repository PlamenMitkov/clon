public class Quadruple {
     int number; // Line number or index
     String operator; // Operator in the quadruple
     String arg1; // First argument
     String arg2; // Second argument
     String result; // Result or destination

    /**
     * Constructor to create a quadruple.
     *
     * @param number The line number or index in the intermediate code.
     * @param operator The operation being performed.
     * @param arg1 The first argument involved in the operation.
     * @param arg2 The second argument involved in the operation.
     * @param result The destination or result of the operation.
     */
     public Quadruple(int number, String operator, String arg1, String arg2, String result) {
         this.number = number;
         this.operator = operator;
         this.arg1 = (arg1 == null) ? "" : arg1;
         this.arg2 = (arg2 == null) ? "" : arg2;
         this.result = (result == null) ? "" : result;
     }

    // Getter for `number`
    public int getNumber() {
        return number;
    }

    // Setter for `number`
    public void setNumber(int number) {
        this.number = number;
    }

    // Getters for other fields
    public String getOperator() {
        return operator;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public String getResult() {
        return result;
    }

    // Setters for other fields
    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("%-5d %-10s %-10s %-10s %-10s", number, operator, arg1, arg2, result);
    }
}
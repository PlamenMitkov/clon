public class Token {
    private final TokenType type; // Type of the token (e.g., IDENTIFIER, DIGITS, etc.)
    private final String value;  // String representation of the token
    private final int code;      // Unique code for the token type
    private final int line;      // Line number where the token is found
    private final int column;    // Column number where the token is found

    /**
     * Constructor for the Token class.
     *
     * @param type   The type of the token
     * @param value  The string value of the token
     * @param code   The unique code associated with the token type
     * @param line   The line number where the token appears
     * @param column The column number where the token appears
     */
    public Token(TokenType type, String value, int code, int line, int column) {
        this.type = type;
        this.value = value;
        this.code = code;
        this.line = line;
        this.column = column;
    }

    // Getters
    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", code=" + code +
                ", line=" + line +
                ", column=" + column +
                '}';
    }
}

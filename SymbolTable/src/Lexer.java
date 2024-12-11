import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String input;               // Input program as a string
    private int index = 0;                    // Current position in the input
    private char currentChar;                 // Current character being analyzed
    private int line = 1;                     // Tracks the current line number
    private int column = 0;                   // Tracks the current column number
    private final SymbolTable symbolTable;    // Shared symbol table
    private final List<Token> tokens;         // List of generated tokens

    // Character categories
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String OPERATORS = "+-*/=<>!";

    // Constructor
    public Lexer(String input) {
        this.input = input;
        this.symbolTable = new SymbolTable();
        this.tokens = new ArrayList<>();
        this.currentChar = input.isEmpty() ? '\0' : input.charAt(0); // Initialize currentChar
    }

    /**
     * Main method to analyze the input and generate tokens.
     */
    public void analyze() throws Exception {
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                handleWhitespace(); // Skip whitespace and handle newlines
            } else if (LETTERS.indexOf(currentChar) >= 0) {
                recognizeIdentifierOrKeyword(); // Process identifiers or keywords
            } else if (DIGITS.indexOf(currentChar) >= 0) {
                recognizeNumber(); // Process numeric constants
            } else if (OPERATORS.indexOf(currentChar) >= 0) {
                recognizeOperator(); // Process operators
            } else if (currentChar == ';') {
                addToken(TokenType.SEMICOLON, ";");
                nextChar();
            } else if (currentChar == '.') {
                addToken(TokenType.DOT, ".");
                nextChar();
            } else if (currentChar == '(') {
                addToken(TokenType.LPARENT, "(");
                nextChar();
            } else if (currentChar == ')') {
                addToken(TokenType.RPARENT, ")");
                nextChar();
            } else if (currentChar == '{') {
                addToken(TokenType.LCURLY, "{");
                nextChar();
            } else if (currentChar == '}') {
                addToken(TokenType.RCURLY, "}");
                nextChar();
            }
            	else {
                reportError("Unexpected character: " + currentChar);
                nextChar();
            }
        }
    }

    /**
     * Wrapper method for analyze to provide the tokenize() method.
     */
    public void tokenize() throws Exception {
        analyze();
    }

    /**
     * Handles whitespace and tracks newlines for line/column tracking.
     */
    private void handleWhitespace() {
        while (Character.isWhitespace(currentChar)) {
            if (currentChar == '\n') {
                line++;
                column = 0;
            }
            nextChar();
        }
    }

    /**
     * Processes identifiers and keywords.
     * Ensures identifiers start with a letter.
     */
    private void recognizeIdentifierOrKeyword() throws Exception {
        StringBuilder buffer = new StringBuilder();

        // First character must be a letter
        if (LETTERS.indexOf(currentChar) >= 0) {
            buffer.append(currentChar);
            nextChar();

            // Subsequent characters can be letters or digits
            while (LETTERS.indexOf(currentChar) >= 0 || DIGITS.indexOf(currentChar) >= 0) {
                buffer.append(currentChar);
                nextChar();
            }
        } else {
            throw new ParseException("Invalid identifier: Identifiers must start with a letter, but found: " + currentChar);
        }

        String value = buffer.toString();

        // Determine if it's a keyword or an identifier
        TokenType type = getKeywordType(value);
        if (type != null) {
            addToken(type, value); // It's a keyword
        } else {
            addToken(TokenType.IDENTIFIER, value); // It's an identifier
        }
    }


    /**
     * Processes numeric constants.
     */
    private void recognizeNumber() throws Exception {
        StringBuilder buffer = new StringBuilder();
        boolean hasDecimalPoint = false;

        // Collect digits and optionally one decimal point
        while (DIGITS.indexOf(currentChar) >= 0 || (currentChar == '.' && !hasDecimalPoint)) {
            if (currentChar == '.') {
                hasDecimalPoint = true;
            }
            buffer.append(currentChar);
            nextChar();
        }

        String value = buffer.toString();

        if (hasDecimalPoint) {
            addToken(TokenType.CONST, value); // Treat as floating-point constant
        } else {
            addToken(TokenType.DIGITS, value); // Treat as integer constant
        }
    }

    /**
     * Processes operators and assigns them to the OPERATOR token type.
     */
    private void recognizeOperator() throws Exception {
        StringBuilder buffer = new StringBuilder();
        buffer.append(currentChar); // Start with the current character
        nextChar();

        // Check for compound operators (e.g., <=, >=, !=, ==)
        if (OPERATORS.indexOf(currentChar) >= 0) {
            buffer.append(currentChar); // Add the next character
            String compound = buffer.toString();
            if (isCompoundOperator(compound)) {
                nextChar(); // Advance for compound operators
            } else {
                buffer.setLength(1); // Keep only the single character if not compound
            }
        }

        String value = buffer.toString();
        // Recognize '=' as a valid operator
        if (value.equals("=")) {
            addToken(TokenType.OPERATOR, "="); // Explicitly mark '=' as an operator
        } else {
            addToken(TokenType.OPERATOR, value); // Add other operators
        }
    }



    /**
     * Moves to the next character in the input.
     */
    private void nextChar() {
        if (++index < input.length()) {
            currentChar = input.charAt(index);
            column++;
        } else {
            currentChar = '\0'; // End of input
        }
    }

    /**
     * Adds a token to the token list and updates the symbol table as needed.
     *
     * @param type  The type of the token (e.g., IDENTIFIER, DIGITS, OPERATOR)
     * @param value The string value of the token
     */
    private void addToken(TokenType type, String value) {
        // Create a new token with line and column information
        Token token = new Token(type, value, type.getCode(), line, column);
        tokens.add(token);

        // Handle specific token types for symbol table integration
        if (type == TokenType.IDENTIFIER || type == TokenType.DIGITS || type == TokenType.CONST) {
            symbolTable.addSymbol(value, token);
        }
    }

    /**
     * Reports a lexical error.
     *
     * @param message The error message
     * @throws Exception
     */
    private void reportError(String message) throws Exception {
        System.err.println("Lexical error at line " + line + ", column " + column + ": " + message);
        throw new Exception(message);
    }

    /**
     * Retrieves the token type for a given keyword.
     *
     * @param word The keyword to check
     * @return The TokenType, or null if not a keyword
     */
    private TokenType getKeywordType(String word) {
        Map<String, TokenType> keywords = new HashMap<>();
        keywords.put("PROGRAM", TokenType.PROGRAM);
        keywords.put("begin", TokenType.BEGIN);
        keywords.put("end", TokenType.END);
        keywords.put("IF", TokenType.IF);
        keywords.put("THEN", TokenType.THEN);
        keywords.put("ELSE", TokenType.ELSE);
        keywords.put("WHILE", TokenType.WHILE);
        keywords.put("WRITE", TokenType.WRITE);
        keywords.put("READ", TokenType.READ);
        keywords.put("DO", TokenType.DO);

        return keywords.get(word); // Return the token type if it’s a keyword
    }

    /**
     * Checks if a string is a valid compound operator.
     */
    private boolean isCompoundOperator(String operator) {
        return operator.equals("<=") || operator.equals(">=") || operator.equals("!=") || operator.equals("==");
    }


    /**
     * Retrieves the list of generated tokens.
     *
     * @return The list of tokens
     */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * Retrieves the symbol table.
     *
     * @return The symbol table
     */
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}

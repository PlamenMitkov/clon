import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int current = 0;
    private SymbolTable symbolTable;
    private IntermediateCodeGenerator intermediateCodeGenerator;
    private int tempCounter = 0; // For generating temporary variables

    public Parser(List<Token> tokens, SymbolTable symbolTable, IntermediateCodeGenerator intermediateCodeGenerator) {
        this.tokens = tokens;
        this.symbolTable = symbolTable;
        this.intermediateCodeGenerator = intermediateCodeGenerator;
    }

    public void parseZ() {
        parseHead(); // Parse the PROGRAM declaration

        // Expect LCURLY at the start of the program body
        if (!match(TokenType.LCURLY)) {
            throw new ParseException("Expected '{' at the start of the program body but found: " +
                    (current < tokens.size() ? tokens.get(current).getType() : "EOF"));
        }

        // Parse the block enclosed in {}
        while (!check(TokenType.RCURLY) && !isAtEnd()) {
            stm(); // Parse each statement within the block
            
            if (match(TokenType.SEMICOLON)) {
                log("Semicolon after statement."); // Optional semicolon logging
            }
        }

        // Expect RCURLY at the end of the block
        if (!match(TokenType.RCURLY)) {
            throw new ParseException("Expected '}' at the end of the program body but found: " +
                    (current < tokens.size() ? tokens.get(current).getType() : "EOF"));
        }

        intermediateCodeGenerator.addQuadruple("STOP", null, null, null);

        // Expect DOT at the end of the program
        if (!match(TokenType.DOT)) {
            throw new ParseException("Expected '.' at the end of the program but found: " +
                    (current < tokens.size() ? tokens.get(current).getType() : "EOF"));
        }

        log("Parsing completed successfully.");
    }


    private void parseHead() {
        consume(TokenType.PROGRAM);
        consume(TokenType.IDENTIFIER);
        consume(TokenType.SEMICOLON);
        
        // Optional: Log the program header
        log("Parsed program header successfully.");
    }

    /**
     * Parses a block enclosed by { and }.
     */
    private void parseBlock() {
        if (!match(TokenType.LCURLY)) {
            throw new ParseException("Expected '{' to start a block");
        }

        System.out.println("[DEBUG] Entering block. Current token: " + tokens.get(current));

        // Parse statements inside the block
        while (!check(TokenType.RCURLY) && !isAtEnd()) {
            stm(); // Parse each statement inside the block
        }

        if (!match(TokenType.RCURLY)) {
            throw new ParseException("Expected '}' to close a block");
        }

        System.out.println("[DEBUG] Exiting block. Current token: " + tokens.get(current));
    }



    private void stm() {
        if (match(TokenType.IDENTIFIER)) {
            // Parse Assignment
            Token identifierToken = previous();
            String variableName = identifierToken.getValue();

            if (!symbolTable.contains(variableName)) {
                // Implicit declaration if variable is not found in the symbol table
                symbolTable.addSymbol(variableName, identifierToken);
            }

            consume(TokenType.OPERATOR); // Expect "="
            String expressionResult = parseExpr(); // Parse the RHS

            // Add a quadruple for the assignment
            intermediateCodeGenerator.addQuadruple("=", expressionResult, "", variableName);
            return;
        }

        if (match(TokenType.WHILE)) {
        	
        	 int loopStartIndex = intermediateCodeGenerator.getNextQuadrupleIndex(); 
         
        	int startQuadrupleIndex = intermediateCodeGenerator.getNextQuadrupleIndex();


            if (!match(TokenType.LPARENT)) {
                throw new ParseException("Expected '(' after 'WHILE'");
            }

            String conditionResult = parseBool(); // Parse the Boolean condition

            if (!match(TokenType.RPARENT)) {
                throw new ParseException("Expected ')' after condition in 'WHILE'");
            }

            if (!match(TokenType.DO)) {
                throw new ParseException("Expected 'DO' after condition in 'WHILE'");
            }

          //  int loopStartIndex = intermediateCodeGenerator.getNextQuadrupleIndex(); // Save loop start

            // Add JMP_FALSE for condition
            int falseJumpIndex = intermediateCodeGenerator.addQuadruple("JMP_FALSE", conditionResult, "", "");

            // Parse loop body
            parseBlock(); // Use parseBlock to handle { }

            // Add JMP back to the start of the loop
            intermediateCodeGenerator.addQuadruple("JMP", "", "", String.valueOf(loopStartIndex));

            // Back-patch the false jump
            intermediateCodeGenerator.updateQuadruple(falseJumpIndex, String.valueOf(intermediateCodeGenerator.getNextQuadrupleIndex()));


            System.out.println("[DEBUG] Completed WHILE loop parsing.");
            return;
        }



        if (match(TokenType.IF)) {
        	

            if (!match(TokenType.LPARENT)) {
                throw new ParseException("Expected '(' after 'IF'");
            }

            String conditionResult = parseBool(); // Parse the Boolean condition

            if (!match(TokenType.RPARENT)) {
                throw new ParseException("Expected ')' after condition in 'IF'");
            }

            if (!match(TokenType.THEN)) {
                throw new ParseException("Expected 'THEN' after condition in 'IF'");
            }

            // Add JMP_FALSE for condition
            int falseJumpIndex = intermediateCodeGenerator.addQuadruple("JMP_FALSE", conditionResult, "", "");

            // Parse THEN block
            parseBlock();

            // Handle ELSE block if present
            if (match(TokenType.ELSE)) {
                int endJumpIndex = intermediateCodeGenerator.addQuadruple("JMP", "", "", ""); // Placeholder
                intermediateCodeGenerator.updateQuadruple(falseJumpIndex, String.valueOf(intermediateCodeGenerator.getNextQuadrupleIndex()));

                // Parse ELSE block
                parseBlock();

                // Back-patch the end jump
                intermediateCodeGenerator.updateQuadruple(endJumpIndex, String.valueOf(intermediateCodeGenerator.getNextQuadrupleIndex()));
            } else {
                // Back-patch the false jump if no ELSE block
                intermediateCodeGenerator.updateQuadruple(falseJumpIndex, String.valueOf(intermediateCodeGenerator.getNextQuadrupleIndex()));
            }

            System.out.println("[DEBUG] Completed IF statement parsing.");

            return;
        }


        if (match(TokenType.WRITE)) {
            // Parse Write
            consume(TokenType.LPARENT); // (
            String expressionResult = parseExpr(); // Argument expression
            consume(TokenType.RPARENT); // )
            consume(TokenType.SEMICOLON);
            intermediateCodeGenerator.addQuadruple("WRITE", expressionResult, "", "");
            return;
        }

        if (match(TokenType.READ)) {
            // Parse Read
            consume(TokenType.LPARENT); // (
            Token identifierToken = consume(TokenType.IDENTIFIER); // Identifier
            consume(TokenType.RPARENT); // )
            consume(TokenType.SEMICOLON);
            intermediateCodeGenerator.addQuadruple("READ", "", "", identifierToken.getValue());
            return;
        }

        if (match(TokenType.LCURLY)) {
            // Parse Block
            symbolTable.enterScope();
            parseBlock(); // Parse the block enclosed by {}
            symbolTable.exitScope();
            return;
        }

        throw new ParseException("Unexpected token or invalid statement: " +
                (current < tokens.size() ? tokens.get(current).getValue() : "EOF"));
    }


    private String parseExpr() {
        String leftResult = parseTerm();

        while (match("+") || match("-")) {
            Token operatorToken = previous();
            String operator = operatorToken.getValue();
            String rightResult = parseTerm();

            String tempResult = getNextTemp();
            intermediateCodeGenerator.addQuadruple(operator, leftResult, rightResult, tempResult);
            leftResult = tempResult;
        }

        return leftResult;
    }

    private String parseTerm() {
        String leftResult = parseFactor();

        while (match("*") || match("/")) {
            Token operatorToken = previous();
            String operator = operatorToken.getValue();
            String rightResult = parseFactor();

            String tempResult = getNextTemp();
            intermediateCodeGenerator.addQuadruple(operator, leftResult, rightResult, tempResult);
            leftResult = tempResult;
        }

        return leftResult;
    }

    private String parseFactor() {
        if (match(TokenType.IDENTIFIER)) {
            String variableName = previous().getValue();
            // Implicitly declare the variable if not already in the symbol table
            if (!symbolTable.contains(variableName)) {
                log("Implicitly declaring variable: " + variableName);
                Token token = previous(); // Get the token for the identifier
                symbolTable.addSymbol(variableName, token);
            }

            return variableName;
        } else if (match(TokenType.DIGITS)) {
            return previous().getValue();
        } else if (match(TokenType.LPARENT)) {
            String exprResult = parseExpr();
            consume(TokenType.RPARENT);
            return exprResult;
        } else {
            throw new ParseException("Expected a factor but found: " +
                                     (current < tokens.size() ? tokens.get(current).getValue() : "EOF"));
        }
    }

    private String parseBool() {
        String leftExpr = parseExpr();

        if (match(TokenType.OPERATOR)) {
            String operator = previous().getValue();
            String rightExpr = parseExpr();

            String tempResult = getNextTemp();
            intermediateCodeGenerator.addQuadruple(operator, leftExpr, rightExpr, tempResult);
            return tempResult;
        } else {
            throw new SemanticException("Invalid Boolean expression: Expected a relational operator but found: " +
                                        (current < tokens.size() ? tokens.get(current).getValue() : "EOF"));
        }
    }

    private String getNextTemp() {
        return "&" + (++tempCounter);
    }

    private Token consume(TokenType expectedType) {
        if (check(expectedType)) {
            return advance();
        } else {
            throw new ParseException("Expected token of type " + expectedType + " but found " +
                                      (isAtEnd() ? "EOF" : tokens.get(current).getType()));
        }
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && tokens.get(current).getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(String... values) {
        for (String value : values) {
            if (check(value)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    
    private boolean match(TokenType expectedType) {
        if (check(expectedType)) {
        //    System.out.println("[DEBUG] Matched token: " + tokens.get(current));
            advance();
            return true;
        }
        return false;
    }


    private boolean check(String value) {
        return !isAtEnd() && tokens.get(current).getValue().equals(value);
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private void log(String message) {
        System.out.println(message + " Current token: " +
                           (current < tokens.size() ? tokens.get(current) : "EOF"));
    }
}

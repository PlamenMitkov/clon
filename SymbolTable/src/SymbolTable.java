import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {
    private final Stack<Map<String, Token>> scopes; // Stack of maps to manage nested scopes

    public SymbolTable() {
        this.scopes = new Stack<>();
        enterScope(); // Start with a global scope
    }
    
   
    /**
     * Enters a new scope.
     */
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Exits the current scope by popping the map off the stack.
     */
    public void exitScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        } else {
            throw new IllegalStateException("Cannot exit scope: No active scope.");
        }
    }

   
    /**
     * Adds a symbol to the current scope.
     *
     * @param key   The name of the symbol.
     * @param token The token associated with the symbol.
     */
    public void addSymbol(String key, Token token) {
        if (!scopes.isEmpty()) {
            scopes.peek().put(key, token);
        } else {
            throw new IllegalStateException("Cannot add symbol: No active scope.");
        }
    }
    
    /**
     * Checks if a symbol exists in any active scope.
     *
     * @param key The name of the symbol to check.
     * @return True if the symbol exists in any active scope, otherwise false.
     */
    public boolean contains(String key) {
        for (Map<String, Token> scope : scopes) {
            if (scope.containsKey(key)) {
                return true;
            }
        }
        return false;
    }


       /**
     * Checks if a symbol is declared in any active scope.
     *
     * @param name the name of the symbol
     * @return true if the symbol is found, false otherwise
     */
    public boolean isDeclared(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a token associated with the given symbol name.
     * Searches in all active scopes, starting from the innermost.
     *
     * @param name the name of the symbol
     * @return the token associated with the symbol, or null if not found
     */
    public Token getToken(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        return null; // Not found
    }

    /**
     * Prints all symbols from all active scopes for debugging.
     */
    /**
     * Prints the symbol table in a structured format.
     */
    public void printSymbols() {
        System.out.println("Symbol Table:");
        System.out.printf("%-10s %-15s %-10s %-5s %-5s%n", "Name", "Type", "Value", "Line", "Column");
        System.out.println("------------------------------------------------------");

        int level = 0;
        for (Map<String, Token> scope : scopes) {
            System.out.println("Scope Level: " + level++);
            for (Map.Entry<String, Token> entry : scope.entrySet()) {
                Token token = entry.getValue();
                System.out.printf("%-10s %-15s %-10s %-5d %-5d%n",
                        entry.getKey(),                 // Name
                        token.getType(),                // Type
                        token.getValue(),               // Value
                        token.getLine(),                // Line
                        token.getColumn());             // Column
            }
            System.out.println();
        }
    }

    public void printAllSymbols() {
        System.out.println("All Symbols:");
        System.out.printf("%-10s %-15s %-10s%n", "Code", "Type", "Value");
        System.out.println("--------------------------------------");

        for (Map<String, Token> scope : scopes) {
            for (Map.Entry<String, Token> entry : scope.entrySet()) {
                Token token = entry.getValue();
                System.out.printf("%-10s %-15s %-10s%n",
                        entry.getKey(),      // Code (symbol name or representation)
                        token.getType(),     // Type (Identifier, Operator, Digit, etc.)
                        token.getValue());   // Value (actual value or description)
            }
        }
    }
}


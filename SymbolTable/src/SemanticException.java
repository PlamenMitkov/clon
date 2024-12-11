/**
 * Custom exception for semantic errors during parsing.
 */
public class SemanticException extends RuntimeException {
    /**
     * Constructs a new SemanticException with the specified detail message.
     *
     * @param message the detail message
     */
    public SemanticException(String message) {
        super(message); // Pass the message to the parent RuntimeException class
    }
}

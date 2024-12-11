public enum TokenType {
    // Keywords
	    PROGRAM(100),
	    BEGIN(101),
	    END(102), DO(109), IF(103), THEN(104), ELSE(105), WHILE(106), WRITE(107), READ(108),
	    IDENTIFIER(300),
	    DIGITS(301),
	    CONST(302),
	    OPERATOR(200),  // Single token type for all operators
	    SEMICOLON(211),
	    DOT(212),
	    LPARENT(213),
	    RPARENT(214),
	    LCURLY(500),
	    RCURLY(501);
	
    private final int code;

    TokenType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name() + "(" + code + ")";
    }

	
}

package ml2js;

public class js_Token {
    private static final int KEYWORDS = js_TokenType.Eof.ordinal();

    private static final String[] reserved = new String[KEYWORDS];
    private static js_Token[] js_Token = new js_Token[KEYWORDS];
    public static final js_Token eofTok = new js_Token(js_TokenType.Eof, "<<EOF>>");
    public static final js_Token variableTok = new js_Token(js_TokenType.Variable, "Variable");
    public static final js_Token BinaryTok = new js_Token(js_TokenType.Binary, "Binary");
    public static final js_Token intTok = new js_Token(js_TokenType.Int, "Int");
    public static final js_Token assignmentTok = new js_Token(js_TokenType.Assignment, "Assignment");
    public static final js_Token blockTok = new js_Token(js_TokenType.Block, "Block");

    public static final js_Token eqeqTok = new js_Token(js_TokenType.Equals, "==");
    public static final js_Token ltTok = new js_Token(js_TokenType.Less, "<");
    public static final js_Token lteqTok = new js_Token(js_TokenType.LessEqual, "<=");
    public static final js_Token gtTok = new js_Token(js_TokenType.Greater, ">");
    public static final js_Token gteqTok = new js_Token(js_TokenType.GreaterEqual, ">=");
    public static final js_Token notTok = new js_Token(js_TokenType.Not, "!");
    public static final js_Token noteqTok = new js_Token(js_TokenType.NotEqual, "!=");
    public static final js_Token plusTok = new js_Token(js_TokenType.Plus, "+");
    public static final js_Token minusTok = new js_Token(js_TokenType.Minus, "-");
    public static final js_Token multiplyTok = new js_Token(js_TokenType.Multiply, "*");
    public static final js_Token divideTok = new js_Token(js_TokenType.Divide, "/");
    public static final js_Token andTok = new js_Token(js_TokenType.And, "&&");
    public static final js_Token orTok = new js_Token(js_TokenType.Or, "||");
    private js_Token (js_TokenType t, String v) {
        type = t;
        value = v;
        if (t.compareTo(js_TokenType.Eof) < 0) {
            int ti = t.ordinal();
            reserved[ti] = v;
            js_Token[ti] = this;
        }
    }
    public static Boolean keyword( String name ) {
        char ch = name.charAt(0);
        for (int i = 0; i < KEYWORDS; i++)
            if (name.equals(reserved[i])) { 
            		return true;
            	}

		return false;
    } // keyword
    
    private js_TokenType type;
    private String value = "";
}

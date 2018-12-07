package ml2js;

import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
    }
  
    private String match (TokenType t) { // * return the string of a token if it matches with t *
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.LeftBrace, TokenType.Ml2jsText,
                          TokenType.RightBrace};
        for (int i=0; i<header.length; i++)   // bypass <ml2js>
            match(header[i]);
        // student exercise
        
        Declarations decs = declarations();
        Block stmts = statements();
        //  -> /ml2js}
        TokenType[ ] footer = {TokenType.Divide, TokenType.Ml2jsText,
                TokenType.RightBrace};
        for (int i=0; i<footer.length; i++)   // bypass </ml2js>
            match(footer[i]);
        return new Program(decs,stmts);  // student exercise
    }
  
    private Declarations declarations () {
    	Declarations ds = new Declarations();
		//first_left_brace
    	match(TokenType.LeftBrace);
    	while (isVarText()){
    		declaration(ds);
    	}
//    	System.out.println(" arrary declared");
        return ds;  // student exercise
    }
  
    private void declaration (Declarations ds) {
    	// grab the Type
    			
    				// grab variable name from current token
    				// skip identifier
    				match(TokenType.VarText);
        			match(TokenType.TypeText);
    				match(TokenType.Assign);

    		    	Type type = type();
    				match(TokenType.NameText);
    				match(TokenType.Assign);
    				Variable v  = new Variable(token.value());
    				match(TokenType.Identifier);
    				Declaration dec =new Declaration(v, type);
    				ds.add(dec);
    				match(TokenType.RightBrace);
    				match(TokenType.LeftBrace);
    				match(TokenType.Divide);
    				match(TokenType.VarText);
    				match(TokenType.RightBrace);
    	        	match(TokenType.LeftBrace);
    }
  
    private Type type () {
    	Type t = null;
		// ���� ��ū�� Ÿ���� üũ�� ��, ���� t �� �ش� Type�� ����
		if(token.type() == TokenType.Int)
			t = Type.INT;
		else if(token.type() == TokenType.Bool)
			t = Type.BOOL;
		else if(token.type() == TokenType.Float)
			t = Type.FLOAT;
		else if(token.type() == TokenType.Char)
			t = Type.CHAR;
		// ��ū Ÿ���� int, bool, float, char �߿� ������ ���� ��� ������ ���
		else
			error("Current token not type (current token: " + token + ")");
		// match�� ȣ�������ν� lexer�� ���� ���� ��ū�� ��������, �� ��ū�� Ÿ���� return 
		match(token.type());
		return t;       
    }
  
    private Statement statement() {
    	// �����ݷ��� ��� AST�� ��带 ������ �ʰ� ������
    			if(token.type() == TokenType.Semicolon)
    				return new Skip();
    			
    			//���Թ� �ؽ�Ʈ�� assign�� �� ���
    			else if(token.type() == TokenType.AssignText)
    				return assignment();
    			
    			// ��ū�� if�� ��� ifStatement()�� ȣ�������ν� ���ο� ifStatement subtree�� ����
    			else if(token.type() == TokenType.If)
    				return ifStatement();
    			
    			// ��ū�� while�� ��� whileStatement()�� ȣ�������ν� ���ο� whileStatement subtree�� ����
    			else if(token.type() == TokenType.While)
    				return whileStatement();
    			
    			// identifier�� ������ �켱 match()�Լ��� ȣ���Ͽ� 'identifier' ��ū�� ������ ��, �� ���� ��ū�� '='��� assignment�� ȣ�� 
    			/*else if(token.type() == TokenType.Identifier){
    				Variable name = new Variable(token.value());
    				match(TokenType.Identifier);
    				if(token.type() == TokenType.Assign)
    					return assignment(name);   		  
    			}*/
    			 
    			 else
    				error("Unknown statement type: " + token.value());
    			
    			return null;
    }
  
    private Block statements () {
    	Block b = new Block();
		// ���ο� block ��ü b�� ����� statement�� ���� ��ȯ�� statement ��ü�� �߰��ϸ鼭 '}' ������ �߰�ȣ�� ������ ���� �ݺ�.
		while(token.type() != TokenType.Divide )
			b.members.add(statement());
		// statement���� ������ block��ü b�� ��ȯ 
		
		return b;
    }
  
    // assignment statement is working very well
    public Assignment assignment() {
    	// target
    			match(TokenType.AssignText);
    			match(TokenType.TargetText);
    			match(TokenType.Assign);
				Variable v  = new Variable(token.value());
    			match(TokenType.Identifier);
				match(TokenType.RightBrace);
    			Expression source = expression();
    			match(TokenType.LeftBrace);
				match(TokenType.Divide);
				match(TokenType.AssignText);
				match(TokenType.RightBrace);
    			match(TokenType.LeftBrace);
    			return new Assignment(v, source);
    }
  
    private Conditional ifStatement () {
    	// gobble up if
    			match(TokenType.If);
    			
    			match(TokenType.ConditionText);
    			match(TokenType.Assign);
    			Expression expression = expression();
    			match(TokenType.RightBrace);
    			
    			// grab if statement
    			match(TokenType.LeftBrace);
    			Block ifblock = statements();
    			// grab else statement if it's there, skip otherwise
    			match(TokenType.Divide);
    			match(TokenType.If);
    			match(TokenType.RightBrace);
    			match(TokenType.LeftBrace);

    			Statement elsestatement =new Skip();
    			
    			return new Conditional(expression, ifblock, elsestatement); 
    	}

    private Loop whileStatement () {
    	// gobble up while
    		match(TokenType.While);
		
    		match(TokenType.ConditionText);
    		match(TokenType.Assign);
			Expression expression = expression();
			match(TokenType.RightBrace);
		
			// grab if statement
			match(TokenType.LeftBrace);

			Block whileblock = statements();
			// grab else statement if it's there, skip otherwise
			match(TokenType.Divide);
			match(TokenType.While);
			match(TokenType.RightBrace);
			match(TokenType.LeftBrace);
    			return new Loop(expression, whileblock); 
    }

    private Expression expression () {
    	Expression e = conjunction();
		// expression goes while there's no more  ||s
		while(token.type() == TokenType.Or){
			Operator op = new Operator(token.value());
			match(TokenType.Or);
			Expression term2 = conjunction();
			e = new Binary(op, e, term2);
		}
		
		return e;
    }
  
    private Expression conjunction () {
    	Expression e = equality();
		// conjunction goes until there's no more &&s
		while(token.type() == TokenType.And){
			Operator op = new Operator(token.value());
			match(TokenType.And);
			Expression term2 = equality();
			e = new Binary(op, e, term2);
		}
		
		return e;
    }
  
    private Expression equality () {
Expression e = relation();
		
		// equality goes while there's an equality operator
		while(isEqualityOp()){
			Operator op = new Operator(token.value());
			match(token.type());
			Expression term2 = relation();
			e = new Binary(op, e, term2);
		}
		
		return e;
    }

    private Expression relation (){
    	Expression e = addition();
		
		// relation goes until there's no more relational ops
		while(isRelationalOp()){
			Operator op = new Operator(token.value());
			match(token.type());
			Expression term2 = addition();
			e = new Binary(op, e, term2);
		}
		
		return e;
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
    	try{
			// int literal
			if (token.type() == TokenType.IntLiteral){
				Value v = new IntValue(Integer.parseInt(token.value()));
				match(TokenType.IntLiteral);
				return v;
				
			// float literal
			}else if (token.type() == TokenType.FloatLiteral){
				Value v = new FloatValue(Float.parseFloat(token.value()));
				match(TokenType.FloatLiteral);
				return v;
			}
			
			// char literal
			else if (token.type() == TokenType.CharLiteral){
				Value v = new CharValue(token.value().charAt(0));
				match(TokenType.CharLiteral);
				return v;
			}
			else
				error("unknown token type for literal! Token value: " + token.value());
		} catch(NumberFormatException e){
			error("Inavlid number format " + e.getLocalizedMessage());
		}
		return null;
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    private boolean isVarText() {
    	return token.type().equals(TokenType.VarText);
    	
    }
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
   

/*   public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        
        prog.display(0);
        // display abstract syntax tree
        prog.tree.print();
    } //main
*/
} // Parser

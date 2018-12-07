package ml2js;

import java.util.*;


public class TypeChecker {
	
	private Program program;
	//선언된 변수를 map에 담아서 반환해주는 함수
	public TypeChecker(Parser p) {
		program = p.program();
		System.out.println("\nBegin type checking...");
		typing(program.decpart);
		V(program);
		System.out.println("\ntype checking is Finished.");
		program.display(0);
		program.tree.print();
	}
	public Program getProgram() {
		return program;
	}
	
	 public  TypeMap typing (Declarations d) {
	        TypeMap map = new TypeMap();
	        for (Declaration di : d) 
	            map.put (di.v, di.t);
	        return map;
	    }
	 
	    public  void check(boolean test, String msg) {
	        if (test)  return;
	        System.err.println(msg);
	        System.exit(1);
	    }
	    // 선언부에 해당하는 부분의 유효성 검사
	    public  void V (Declarations d) {
	        for (int i=0; i<d.size() - 1; i++)
	            for (int j=i+1; j<d.size(); j++) {
	                Declaration di = d.get(i);
	                Declaration dj = d.get(j);
	                check( ! (di.v.equals(dj.v)),
	                       "duplicate declaration: " + dj.v);
	            }
	    } 

	    public  void V (Program p) {
	        V (p.decpart);
	        V (p.body, typing (p.decpart));
	    } 
	    
	    public  Type typeOf (Expression e, TypeMap tm) {
	        if (e instanceof Value) return ((Value)e).type;
	        if (e instanceof Variable) {
	            Variable v = (Variable)e;
	            check (tm.containsKey(v), "undefined variable: " + v);
	            return (Type) tm.get(v);
	        }
	        if (e instanceof Binary) {
	            Binary b = (Binary)e;
	            if (b.op.ArithmeticOp( ))
	                if (typeOf(b.term1,tm)== Type.FLOAT)
	                    return (Type.FLOAT);
	                else return (Type.INT);
	            if (b.op.RelationalOp( ) || b.op.BooleanOp( )) 
	                return (Type.BOOL);
	        }
	        if (e instanceof Unary) {
	            Unary u = (Unary)e;
	            if (u.op.NotOp( ))        return (Type.BOOL);
	            else if (u.op.NegateOp( )) return typeOf(u.term,tm);
	            else if (u.op.intOp( ))    return (Type.INT);
	            else if (u.op.floatOp( )) return (Type.FLOAT);
	            else if (u.op.charOp( ))  return (Type.CHAR);
	        }
	        throw new IllegalArgumentException("should never reach here");
	    } 

	    public  void V (Expression e, TypeMap tm) {
	        if (e instanceof Value) 
	            return;
	        if (e instanceof Variable) { 
	            Variable v = (Variable)e;
	            check( tm.containsKey(v)
	                   , "undeclared variable: " + v);
	            return;
	        }
	        if (e instanceof Binary) {
	            Binary b = (Binary) e;
	            Type typ1 = typeOf(b.term1, tm);
	            Type typ2 = typeOf(b.term2, tm);
	            V (b.term1, tm);
	            V (b.term2, tm);
	            if (b.op.ArithmeticOp( ))  
	                check( typ1 == typ2 &&
	                       (typ1 == Type.INT || typ1 == Type.FLOAT)
	                       , "type error for " + b.op);
	            else if (b.op.RelationalOp( )) 
	                check( typ1 == typ2 , "type error for " + b.op);
	            else if (b.op.BooleanOp( )) 
	                check( typ1 == Type.BOOL && typ2 == Type.BOOL,
	                       b.op + ": non-bool operand");
	            else
	                throw new IllegalArgumentException("should never reach here");
	            return;
	        }
	        if (e instanceof Unary) {
	            Unary u = (Unary) e;
	            Type type = typeOf(u.term, tm); //start here
	            V(u.term, tm);
	            if (u.op.NotOp()) {
	                check((type == Type.BOOL), "type error for NotOp " + u.op);
	            } 
	            else if (u.op.NegateOp()) {
	                check((type == (Type.INT) || type == (Type.FLOAT)), "type error for NegateOp " + u.op);
	            }
	            else {
	                throw new IllegalArgumentException("should never reach here UnaryOp error");
	            }
	            return;
	        }
	        // student exercise
	        throw new IllegalArgumentException("should never reach here");
	    }

	    public  void V (Statement s, TypeMap tm) {
	        if ( s == null )
	            throw new IllegalArgumentException( "AST error: null statement");
	        if (s instanceof Skip) return;
	        if (s instanceof Assignment) {
	            Assignment a = (Assignment)s;
	            check( tm.containsKey(a.target)
	                   , " undefined target in assignment: " + a.target);
	            V(a.source, tm);
	            Type ttype = (Type)tm.get(a.target);
	            Type srctype = typeOf(a.source, tm);
	            if (ttype != srctype) {
	                if (ttype == Type.FLOAT)
	                    check( srctype == Type.INT
	                           , "mixed mode assignment to " + a.target);
	                else if (ttype == Type.INT)
	                    check( srctype == Type.CHAR
	                           , "mixed mode assignment to " + a.target);
	                else
	                    check( false
	                           , "mixed mode assignment to " + a.target);
	            }
	            return;
	        } 
	        // student exercise---------------------------------------------------------
	        
	        // conditional 선언문이 유효 하기 위해서는  첫째, expression이 bool 타입이어야 하고,
	        // 둘째, then-branch와 else-branch가 유효 해야한다.
	        else if (s instanceof Conditional) {
	            Conditional c = (Conditional)s;
	            // statement의 test 부분의 유효성 검사
	            V(c.test, tm);
	            // test 부분의 타입 checking과 symbol-table에 선언된 변수인지 검사 
	            Type testtype = typeOf(c.test, tm);
	            // 만약 bool 타입이라면, then-branch와 else-branch의 유효성 검사
	            if (testtype == Type.BOOL) {
	                V(c.thenbranch, tm); 
	                V(c.elsebranch, tm); 
	                return;
	            }else {
	                check( false, "poorly typed if in Conditional: " + c.test);
	            }
	        }
	        //Clite 문법에서 Loop statement가 유효하기 위해서는, 우선 expression이 유효해야 하고
	        //반복문의 body 부분이 valid 해야한다.
	        else if (s instanceof Loop) {
	            Loop l = (Loop)s;
	            // test 부분의 유효성 검사
	            V(l.test, tm);
	            Type testtype = typeOf(l.test, tm);
	            if (testtype == Type.BOOL) {
	            	// body 부분 유효성 검사
	                V(l.body, tm);
	            }else {
	                check ( false, "poorly typed test in while Loop in Conditional: " + l.test);
	            }
	        }
	     
	        //Clite에서 Block-statement의 유효성 검사는 block 내부에 있는 모든 statement가
	        //모두 유효한지 검사함으로써 확인이 가능하다.
	        else if (s instanceof Block) {
	            Block b = (Block)s;
	            // Block 내부에 있는 statement를 모두 순회하면서 유효성 검사를 진행한다.
	            for(Statement i : b.members) {
	                V(i, tm);
	            } 
	        } else {
	        throw new IllegalArgumentException("should never reach here");
	        }
	        //throw new IllegalArgumentException("should never reach here");
	    }

	   /* public static void main(String args[]) {
	        Parser parser  = new Parser(new Lexer(args[0]));
	        Program prog = parser.program();
	        prog.display(0);          
	        //System.out.println("\nBegin type checking...");
	        //System.out.println("Type map:");
	        TypeMap map = typing(prog.decpart);
	        //map.display();   // student exercise
	        V(prog);
	        prog.tree.print();

	    } //main
*/	    
}

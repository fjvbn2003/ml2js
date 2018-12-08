package ml2js;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList; 
import java.util.Stack; 

public class CodeGenerator {
	private Program program;
	private TreeNode tree;
	private String token;
	private ArrayList<String> token_list;
	private boolean block_open;
	
	public CodeGenerator(TypeChecker t) {
			program = t.getProgram();
			tree = t.getProgram().tree;
			token_list = new ArrayList<String>();
			block_open = false;
	}
	public boolean generate() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("C:\\Users\\youngju\\eclipse-workspace\\ml2js\\src\\ml2js\\ouput.txt");
		//fill tokenlist member variable
		tree_traversal_preorder();
		// printWriter 예제
		String s = generate_string();
		pw.write(s);
        pw.close();
		return true;
	}
	
	private String generate_string() {
		StringBuffer sb = new StringBuffer();
		int list_len = token_list.size();
		int i=0;
		token = token_list.get(i);
		while(token !=  null) {
			if(i>= list_len)
				break;
			else {
				token = token_list.get(i);
			}
			
			if(token.equals("root")) {
				i++;
				token = token_list.get(i);

			}
			else if(token.equals("Declarations")) {
				i++;
				// 변수 여러개 선언
				while(!js_Token.keyword(token_list.get(i))){
					sb.append("var "+token_list.get(i)+";\r\n");
					i++;
					token = token_list.get(i);

				}
			}
			else if(token.equals("Assignment")) {
			
				i++;//Variable id
				token = token_list.get(i);
				
				String id = token_list.get(i).split(" ")[1];
				sb.append(id + " = ");
				
				i++;//value
				token = token_list.get(i);
				
				//  single value
				if(isType()) {
					String value = token_list.get(i).split(" ")[1];
					sb.append(value+";\r\n");
				}
				// not a single value. case of Bynary expression!
				else {
					while(isBinary()) {
						i++;//operator
						token = token_list.get(i);
						String operator = String.valueOf(token);
						i++;//first operand
						token = token_list.get(i);
						String first_operand = String.valueOf(token.split(" ")[1]);
						i++;//second operand
						token = token_list.get(i);
						String second_operand =String.valueOf(token.split(" ")[1]);
						sb.append(first_operand+operator+second_operand);
					}
					sb.append(";\r\n");
				}
				i++;		
			}
			
			//conditional statement
			else if(token.equals("Conditional") ||token.equals("Loop")) {
				if(token.equals("Conditional")) {
					sb.append("if(");	
				}else {
					sb.append("while(");
				}
				
				i++;//Binary or single value
				token = token_list.get(i);
				if(isType()) {
					i++;//value
					token = token_list.get(i);
					String value = token_list.get(i).split(" ")[1];
					sb.append(value+";\r\n");
				}
				// not a single value. case of Bynary expression!
				else {
					while(isBinary()) {
						i++;//operator
						token = token_list.get(i);
						String operator = String.valueOf(token);
						i++;//first operand
						token = token_list.get(i);
						String first_operand = String.valueOf(token.split(" ")[1]);
						i++;//second operand
						token = token_list.get(i);
						String second_operand =String.valueOf(token.split(" ")[1]);
						sb.append(first_operand+operator+second_operand);
					}
				}
				sb.append(")");
				i++;	
			}
			

			
			//block_keyword
			else if(isBlock()) {
				sb.append("{ \r\n");
				block_open = true;
				i++;

			}
			//block_out_keyword
			else if(isBlock_out()) {
				sb.append("} \r\n");
				block_open = false;
				i++;

			}
			else {
				i++;
			}

		}
		
		return sb.toString();
	}
	public void tree_traversal_preorder() {
		
		// javascript 결과를 담을 스트링 버퍼 선언 (String에 비해 append 연산이 효율적임)
		// 트리 전위순회를 위한 스텍 선언
		Stack<TreeNode> node_stack = new Stack<TreeNode>();
		node_stack.add(tree);
		while(node_stack.isEmpty() == false) {
			TreeNode current_node = node_stack.pop();
			
			token_list.add(current_node.data);
			int length = current_node.Children.size();
			for(int i=length-1; i>=0; i--) {
				if(current_node.Children.get(i).data.equals("Block")) {
					node_stack.add(new TreeNode("Block_out"));
				}
				node_stack.add(current_node.Children.get(i));
				
			}
		}
		return ;
	}/*
    private boolean isAddOp( ) {
        return token.equals(js_Token.plusTok.value) ||
               token.equals(js_Token.minusTok.value);
    }
    
    private boolean isMultiplyOp( ) {
        return token.equals(js_Token.multiplyTok.value) ||
               token.equals(js_Token.divideTok.value);
    }
    
    private boolean isUnaryOp( ) {
        return token.equals(js_Token.notTok.value) ||
               token.equals(js_Token.minusTok.value);
    }
    
    private boolean isEqualityOp( ) {
        return token.equals(js_Token.eqeqTok.value) ||
            token.equals(js_Token.noteqTok.value);
    }
    
    private boolean isRelationalOp( ) {
        return token.equals(js_Token.ltTok.value) ||
               token.equals(js_Token.lteqTok.value) || 
               token.equals(js_Token.gtTok.value) ||
               token.equals(js_Token.gteqTok.value);
    }*/
    
    private boolean isType( ) {
        return token.split(" ")[0].equals(js_Token.intTok.value)
            || token.split(" ")[0].equals(js_Token.variableTok.value);
    }
    
    private boolean isBinary() {
    	return token.equals(js_Token.BinaryTok.value);
    }
	
    private boolean isBlock() {
    	return token.equals(js_Token.blockTok.value);
    }
    private boolean isBlock_out() {
    	return token.equals(js_Token.blockOutTok.value);
    }
	
	 public static void main(String args[]) {
		 
		 	// 코드 제너레이터 생성
	        CodeGenerator code_generator  = new CodeGenerator(new TypeChecker(new Parser(new Lexer(args[0]))));
	        // file not found exception handling
	        try {
	        	// code generate start!!
	        	// output string is
				code_generator.generate();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	  } //main
}

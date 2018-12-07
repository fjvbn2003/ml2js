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
	
	public CodeGenerator(TypeChecker t) {
			program = t.getProgram();
			tree = t.getProgram().tree;
			token_list = new ArrayList<String>();
	}
	public boolean generate() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("C:\\Users\\youngju\\eclipse-workspace\\ml2js\\src\\ml2js\\ouput.txt");
		//fill tokenlist member variable
		tree_traversal_preorder();
		// printWriter ����
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
			//sb.append(token_list.get(i)+"\r\n");
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
				// ���� ������ ����
				while(!js_Token.keyword(token_list.get(i))){
					sb.append("var "+token_list.get(i)+";\r\n");
					i++;
					token = token_list.get(i);

				}
			}
			else if(token.equals("Block")) {
				i++;
			}
			else if(token.equals("Assignment")) {
			
				i++;//Variable id
				token = token_list.get(i);
				//!! ���⼭���� �ٽ� �ڵ� ����. assignment �캯�� expression�� �޵��� �ؾ���..!! expression
				//!! expression ����� ������ conditional ������ ��밡��. �� ����� ���� ���ȭ ��Ű��!!
				if(isType()) {
					String id = token_list.get(i).split(" ")[1];
					sb.append(id + " = ");
					
					i++;//value
					token = token_list.get(i);

				}
				//!! ���⼭���� �ٽ� �ڵ� ����. assignment �캯�� expression�� �޵��� �ؾ���..!! expression
				//!! expression ����� ������ conditional ������ ��밡��. �� ����� ���� ���ȭ ��Ű��!!
				
				String value = token_list.get(i).split(" ")[1];
				sb.append(value + ";\r\n");
				
				i++;
				token = token_list.get(i);
			}
			else if(token.equals("Conditional")) {
				i++;
		/*		while(token.equals("Binary")) {
					i++;//operation token
					String op = token_list.get(i);
					
				}*/
			}
			else {
				i++;
			}

		}
		
		return sb.toString();
	}
	public void tree_traversal_preorder() {
		
		// javascript ����� ���� ��Ʈ�� ���� ���� (String�� ���� append ������ ȿ������)
		// Ʈ�� ������ȸ�� ���� ���� ����
		Stack<TreeNode> node_stack = new Stack<TreeNode>();
		node_stack.add(tree);
		while(node_stack.isEmpty() == false) {
			TreeNode current_node = node_stack.pop();
			token_list.add(current_node.data);
			
			int length = current_node.Children.size();
			for(int i=length-1; i>=0; i--) {
				node_stack.add(current_node.Children.get(i));
			}
		}
		return ;
	}
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
    }
    
    private boolean isType( ) {
        return token.equals(js_Token.intTok.value)
            || token.equals(js_Token.variableTok.value);
    }
        
	
	
	
	
	
	 public static void main(String args[]) {
		 
		 	// �ڵ� ���ʷ����� ����
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

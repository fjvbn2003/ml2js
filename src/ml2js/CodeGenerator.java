package ml2js;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList; 
import java.util.Stack; 

public class CodeGenerator {
	private Program program;
	private TreeNode tree;
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
		String token = token_list.get(i);
		while(token !=  null) {
			//sb.append(token_list.get(i)+"\r\n");
			if(i>= list_len)
				break;
			else {
				token = token_list.get(i);
			}
			
			
			if(token.equals("root")) {
				i++;
			}
			else if(token.equals("Declarations")) {
				i++;
				// ���� ������ ����
				while(!js_Token.keyword(token_list.get(i))){
					sb.append("var "+token_list.get(i)+";\r\n");
					i++;
				}
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

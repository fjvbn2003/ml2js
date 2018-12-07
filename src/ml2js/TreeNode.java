package ml2js;


import java.util.ArrayList;

public class TreeNode{
	public TreeNode(String name) {
		this.data = name;
		Children = new ArrayList<TreeNode>();

	}
	public TreeNode(String name, TreeNode parent) {
		this.data = name;
		this.parent = parent;
		parent.Children.add(this);
		Children = new ArrayList<TreeNode>();
	}
    public void print() {
        print("", true);
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "戌式式 " : "戍式式 ") + data);
        for (int i = 0; i < Children.size() - 1; i++) {
            Children.get(i).print(prefix + (isTail ? "    " : "弛   "), false);
        }
        if (Children.size() > 0) {
            Children.get(Children.size() - 1).print(prefix + (isTail ?"    " : "弛   "), true);
        }
    }
    TreeNode parent;
    String data;
    ArrayList <TreeNode> Children;
}

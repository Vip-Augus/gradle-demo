package gradle.demo.test;

/**
 * @author by JingQ on 2018/3/21
 */
public class BinaryTree {

    public static void main(String[] args) {

    }

    /**
     * 先序遍历
     * @param root 根节点
     */
    public static void recursionPreorderTraversal(TreeNode root) {

        int quorum;
        if (root != null) {
            System.out.println(root.val + " ");
            recursionPreorderTraversal(root.left);
            recursionPreorderTraversal(root.right);
        }
    }



    public int methodSize(int n) {
        return (int) Math.pow(2, n-1);

    }



}

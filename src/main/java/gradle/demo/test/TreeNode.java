package gradle.demo.test;

import lombok.Data;

/**
 * @author by JingQ on 2018/3/21
 */
@Data
public class TreeNode {

    int val;

    TreeNode left;

    TreeNode right;

    /**
     * 构造方法
     *
     * @param val 值
     */
    public TreeNode(int val) {
        this.val = val;
    }

}

package gradle.demo.test;

/**
 * @author by JingQ on 2018/4/15
 */
public class ShellSortTest {


    public static void main(String[] args) {
        int[] arr = {10, 20, 39, 1, 5, 9, 3, 4};
        sort(arr);
        System.out.println(arr);
    }

    private static void sort(int[] arr) {
        //增量gap，并不断减少gap
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            //从第gap个元素开始，逐个对其所在组进行直接插入排序
            for (int i = gap; i < arr.length; i++){
                int j = i;
                while (j - gap >= 0 && arr[j] < arr[j-gap]) {
                    swap(arr, j, j-gap);
                    j-=gap;
                }
            }
        }
    }


    private static void swap(int[] arr, int a, int b) {
        arr[b] = arr[a] + arr[b];
        arr[a] = arr[b] - arr[a];
        arr[b] = arr[b] - arr[a];
    }
}

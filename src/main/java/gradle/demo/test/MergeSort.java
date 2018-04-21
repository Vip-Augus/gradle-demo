package gradle.demo.test;

/**
 * @author by JingQ on 2018/4/15
 */
public class MergeSort {

    public static void main(String[] args) {
        int[] arr = {3, 10, 100, 5, 8, 2, 1, 1};
        sort(arr);
        Class c1 = int.class;
        System.out.println(c1.getName());
    }

    public static void sort(int[] arr) {
        //先开辟一个暂存数组，避免频繁创建
        int[] temp = new int[arr.length];
        sort(arr, 0, arr.length -1 , temp);
    }

    private static void sort(int[] arr, int left, int right, int[] temp) {
        if (left < right) {
            int mid = (left + right) / 2;
            //左边归并排序，使得左子序列有序
            sort(arr, left, mid, temp);
            //右边归并排序，使得右子序列有序
            sort(arr, mid + 1, right, temp);
            //将两个有序数组进行合并
            merge(arr, left, mid, right, temp);
        }
    }

    private static void merge(int[] arr, int left, int mid, int right, int[] temp) {
        //左序指针
        int i = left;
        //右序指针
        int j = mid + 1;
        //临时数组指针
        int t = 0;

        while (i <= mid && j <= right) {
            if (arr[i] < arr[j]) {
                //每次排序后都需要将下标加一
                temp[t++] = arr[i++];
            } else {
                temp[t++] = arr[j++];
            }
        }

        while (i <= mid) {
            temp[t++] = arr[i++];
        }

        while (j <= right) {
            temp[t++] = arr[j++];
        }

        t = 0;
        //将暂存数组的数据拷贝到原来的数组
        while (left <= right) {
            arr[left++] = temp[t++];
        }
    }
}

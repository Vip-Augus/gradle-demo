package gradle.demo.test;

/**
 * @author by JingQ on 2018/4/17
 */
public class QuickSortTest {


    public static void main(String[] args) {
        int[] arr = {100, 29, 13, 90, 79, 86};
        sort(arr, 0, arr.length - 1);
    }

    public static void sort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    public static void sort(int[] arr, int low, int high) {
        int l = low;
        int h = high;
        int pivot = arr[l];

        while (l < h) {
            //从后面一直找第一个比枢纽值小的数组下标
            while (l < h && arr[h] > pivot) {
                h--;
            }
            if (l < h) {
                swap(arr, l, h);
                l++;
            }
            //从前面一直找第一个比枢纽值大的数组下标
            while (l < h && arr[l] < pivot) {
                l++;
            }

            if (l < h) {
                swap(arr, l, h);
                h--;
            }
            if (l > low) {
                sort(arr, low, l - 1);
            }

            if (h < high) {
                sort(arr, l + 1, high);
            }
        }
    }

    private static void quickSort(int[] arr, int left, int right) {
        if (left < right) {
            dealPivot(arr, left, right);
            //枢纽值在序列末端
            int pivot = right - 1;
            int i = left;
            int j = right - 1;

            while (true) {
                while (arr[++i] < arr[pivot]) {
                }
                while (j > left && arr[--j] > arr[pivot]) {
                }
                if (i < j) {
                    swap(arr, i, j);
                } else {
                    break;
                }
            }
            if (i < right) {
                swap(arr, i, right - 1);
            }
            quickSort(arr, left, i - 1);
            quickSort(arr, i + 1, right);

        }
    }

    /**
     * 处理枢纽值，并将其放置数组末端
     *
     * @param arr
     * @param left
     * @param right
     */
    private static void dealPivot(int[] arr, int left, int right) {
        int mid = (left + right) / 2;
        if (arr[left] > arr[mid]) {
            swap(arr, left, mid);
        }

        if (arr[left] > arr[right]) {
            swap(arr, left, right);
        }

        if (arr[right] < arr[mid]) {
            swap(arr, right, mid);
        }

        swap(arr, right - 1, mid);

    }

    private static void swap(int[] arr, int a, int b) {
        arr[b] = arr[a] + arr[b];
        arr[a] = arr[b] - arr[a];
        arr[b] = arr[b] - arr[a];
    }
}

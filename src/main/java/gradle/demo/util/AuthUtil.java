package gradle.demo.util;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.dto.UserDTO;
import gradle.demo.test.TestService;
import gradle.demo.util.result.SingleResult;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author guojiawei
 * @date 2018/1/8
 */
public class AuthUtil {
    public static boolean checkAuthority(int auth, int type) {
        return ((auth >> type) & 1) == 1;
    }

    public static void NoAuthorityResponseOut(HttpServletResponse httpServletResponse, String describe) throws IOException {
        SingleResult<UserDTO> result = new SingleResult<>();
        result.returnError(String.format("没有%s权限", describe));
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().append(JSON.toJSON(result).toString());
        httpServletResponse.getWriter().close();
    }



    public static void main(String[] args) throws IOException, InterruptedException {
        String filePath = "/1/2/3/4/5/file.name";
        int index = filePath.lastIndexOf(".");
        String dir = filePath.substring(0, index);
        String filrName = filePath.substring(index + 1, filePath.length());
    }



    private void heapSort(int[] arr) {
        //1.构建大顶堆
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            //从第一个非叶子结点从下至上，从右至左调整结构
            adjustHeap(arr, i, arr.length);
        }
        //2.调整堆结构+交换堆顶元素与末尾元素
        for (int j = arr.length - 1; j > 0; j--) {
            //将堆顶元素与末尾元素进行交换
            swap(arr, 0, j);
            //重新对堆进行调整
            adjustHeap(arr, 0, j);
        }
    }

    private void adjustHeap(int[] arr, int i, int length) {
        int temp = arr[i];
        //从i结点的左子结点开始，也就是2i+1处开始
        for (int k = i * 2 + 1; k < length; k = k * 2 + 1) {
            //如果左子结点小于右子结点，k指向右子结点
            if (k + 1 < length && arr[k] < arr[k + 1]) {
                k++;
            }
            //如果子节点大于父节点，将子节点值赋给父节点（不用进行交换）
            if (arr[k] > temp) {
                arr[i] = arr[k];
                i = k;
            } else {
                break;
            }
        }
        //将temp值放到最终的位置
        arr[i] = temp;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }


    private void mergeSort(int[] arr, int left, int right, int[] temp) {
        if(left<right){
            int mid = (left+right)/2;
            //左边归并排序，使得左子序列有序
            mergeSort(arr,left,mid,temp);
            //右边归并排序，使得右子序列有序
            mergeSort(arr,mid+1,right,temp);
            //将两个有序子数组合并操作
            merge(arr,left,mid,right,temp);
        }
    }

    private void merge(int[] arr, int left, int mid, int right, int[] temp) {
        //左序指针
        int i = left;
        //右序指针
        int j = mid + 1;
        //临时指针
        int t = 0;
        while (i <= mid && j <= right) {
            if (arr[i] < arr[j]) {
                temp[t++] = arr[i++];
            } else {
                temp[t++] = arr[j++];
            }
        }
        //将左边剩余元素填充进temp中
        while (i <= mid) {
            temp[t++] = arr[i++];
        }
        //将右序列剩余元素填充进temp中
        while (j <= right) {
            temp[t++] = arr[j++];
        }
        t = 0;
        //将temp中的元素全部拷贝到原数组中
        while (left <= right) {
            arr[left++] = temp[t++];
        }
    }


    static class MyTask implements Runnable {
        private int taskNum;

        public MyTask(int num) {
            this.taskNum = num;
        }

        @Override
        public void run() {
            System.out.println("正在执行task " + taskNum);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task " + taskNum + "执行完毕");
        }
    }
}

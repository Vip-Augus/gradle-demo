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

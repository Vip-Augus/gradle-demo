package gradle.demo.interceptor;

import com.alibaba.fastjson.JSON;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.SingleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常拦截器
 *
 * @author by JingQ on 2018/2/7
 */
@ControllerAdvice
@Slf4j
public class ExceptionInterceptor {

    /**
     * 异常类型对应的处理逻辑
     * @param e
     *          异常
     * @return  JSON格式的异常
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public JSON handle(Exception e) {
        //自定义的运行异常
        if (e instanceof BusinessException) {
            BusinessException ex = (BusinessException) e;
            SingleResult result = new SingleResult();
            result.returnError(ex);
            log.error(ex.getMessage(), ex);
            return (JSON) JSON.toJSON(result);
        } else {
            log.error("未捕获错误", e);
            SingleResult result = new SingleResult();
            result.setMsg("未捕获错误");
            result.setCode("00000000");
            return (JSON) JSON.toJSON(result);
        }
    }
}

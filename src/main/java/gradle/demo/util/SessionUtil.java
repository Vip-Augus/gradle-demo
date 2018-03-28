package gradle.demo.util;

import gradle.demo.model.User;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ExceptionDefinitions;

import javax.servlet.http.HttpSession;

/**
 * 会话工具类
 *
 * @author by JingQ on 2018/1/1
 */
public class SessionUtil {

    /**
     * 从session中获取用户数据
     *
     * @param session 用户请求
     * @return 用户数据
     */
    public static User getUser(HttpSession session) {
        User user = (User) session.getAttribute(CodeConstants.USER_INFO_CONSTANT);
        if (user == null) {
            throw new BusinessException(ExceptionDefinitions.LOGIN_AGAIN);
        }
        return user;
    }

}

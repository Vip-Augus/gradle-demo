package gradle.demo.util.result;

import gradle.demo.util.CodeConstants;

/**
 * 常用异常定义
 *
 * @author by JingQ on 2018/1/13
 */
public class ExceptionDefinitions {

    /**
     * 重新登录
     */
    public static final BusinessException LOGIN_AGAIN = new BusinessException("11111111", CodeConstants.LOGIN_AGAIN);

    /**
     * 实验室已存在
     */
    public static final BusinessException CLASSROOM_CONFLICT = new BusinessException("CL000001", "实验室已存在");

    /**
     * 不在签到时间内
     */
    public static final BusinessException NOT_IN_SIGN_TIME = new BusinessException("CL0000002", "不在签到时间内");

    /**
     * 签到识别码错误
     */
    public static final BusinessException CHECK_CODE_INCORRECT = new BusinessException("CL000003", "签到识别码错误");

    /**
     * 已经签到了
     */
    public static final BusinessException HAVE_CHECK_IN = new BusinessException("CL000004", "已经签到了");
}

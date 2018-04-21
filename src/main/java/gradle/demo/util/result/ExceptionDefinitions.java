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
    public static final ExceptionDefinition LOGIN_AGAIN = new ExceptionDefinition("11111111", CodeConstants.LOGIN_AGAIN);

    /**
     * 实验室已存在
     */
    public static final ExceptionDefinition CLASSROOM_CONFLICT = new ExceptionDefinition("CL000001", "教室已存在");

    /**
     * 不在签到时间内
     */
    public static final ExceptionDefinition NOT_IN_SIGN_TIME = new ExceptionDefinition("CL0000002", "不在签到时间内");

    /**
     * 签到识别码错误
     */
    public static final ExceptionDefinition CHECK_CODE_INCORRECT = new ExceptionDefinition("CL000003", "签到识别码错误");

    /**
     * 已经签到了
     */
    public static final ExceptionDefinition HAVE_CHECK_IN = new ExceptionDefinition("CL000004", "已经签到了");

    /**
     * 填写的日期信息不正确
     */
    public static final ExceptionDefinition INCORRECT_CLASS_TIME = new ExceptionDefinition("CL000005", "填写的日期信息不正确");

    /**
     * 分页查询条件错误
     */
    public static final ExceptionDefinition INCORRECT_PAGE_PARAM = new ExceptionDefinition("GY000001", "分页参数不正确");

    /**
     * 文件后缀名不正确
     */
    public static final ExceptionDefinition INCORRECT_FILE_SUFFIX = new ExceptionDefinition("WJ000001", "文件后缀名不正确");

    /**
     * 作业记录不存在
     */
    public static final ExceptionDefinition HOMEWORK_RECORD_NOT_EXIST = new ExceptionDefinition("ZY0000001", "作业记录不存在");

}

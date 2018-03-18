package gradle.demo.util.result;

import gradle.demo.util.CodeConstants;

/**
 * @author by JingQ on 2018/3/13
 */
public class ApiResponse<T> {

    private final static String SUCCESS_STATUS = "Y";
    private final static String ERROR_STATUS   = "N";
    private Head                head;
    private T                   body;

    public ApiResponse() {
    }

    public ApiResponse(Head head, T body) {
        this.head = head;
        this.body = body;
    }

    public ApiResponse(Head head) {
        this.head = head;
    }

    public Head getHead() {
        return head;
    }

    public T getBody() {
        return body;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public ApiResponse<T> head(Head head) {
        setHead(head);
        return this;
    }

    public ApiResponse<T> body(T body) {
        setBody(body);
        return this;
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T model) {
        return new ApiResponse<T>().head(new Head(CodeConstants.SUCCESS_CODE,
                CodeConstants.SUCCESS_DESCRIPTION, SUCCESS_STATUS, CodeConstants.SUCCESS_MSG))
                .body(model);
    }

    /**
     * 业务出现异常时候，调用该方法把数据传递给UI
     *
     * @param message
     * @return
     */
    public static <T> ApiResponse<T> error(Message message) {
        return new ApiResponse<T>().head(
                new Head(message.getCode(), message.getDescription(), ERROR_STATUS, message.getText()))
                .body(null);
    }

    /**
     * 业务出现异常时候，调用该方法把数据传递给UI
     *
     * @param message
     * @return
     */
    public static <T> ApiResponse<T> error(Message message, T model) {
        return new ApiResponse<T>().head(
                new Head(message.getCode(), message.getDescription(), ERROR_STATUS, message.getText()))
                .body(model);
    }

}

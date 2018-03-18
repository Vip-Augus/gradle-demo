package gradle.demo.util.result;

import gradle.demo.model.enums.ResultType;
import org.apache.ibatis.executor.ErrorContext;

import java.io.Serializable;

/**
 * @author by JingQ on 2018/1/1
 */
public abstract class Result implements Serializable {

    private static final long serialVersionUID = -3295991523122453977L;


    private boolean success = false;

    private ErrorContext errorContext;

    private String msg;

    private String code;

    private String description;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
        this.code = ResultType.SUCCESS.getCode();
    }

    public ErrorContext getErrorContext() {
        return errorContext;
    }

    public void setErrorContext(ErrorContext errorContext) {
        this.errorContext = errorContext;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

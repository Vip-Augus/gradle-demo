package gradle.demo.util.result;

import org.apache.ibatis.executor.ErrorContext;

import java.util.List;

/**
 * 批量结果返回类
 * Author by JingQ on 2018/1/1
 */
public class ListResult<T> extends Result {

    private List<T> list;

    private Integer totalCount;

    public ListResult() {

    }

    public void returnSuccess(List<T> list) {
        setSuccess(true);
        this.list = list;
    }

    public void returnError(String errorMessage) {
        setMsg(errorMessage);
    }

    public void returnError(BusinessException ex) {
        setCode(ex.getCode());
        setMsg(ex.getMessage());
    }

    public void returnError(String errorMessage, ErrorContext errorContext) {
        setMsg(errorMessage);
        setErrorContext(errorContext);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}

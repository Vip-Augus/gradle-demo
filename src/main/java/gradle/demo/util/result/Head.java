package gradle.demo.util.result;

/**
 * @author by JingQ on 2018/3/13
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class Head {

    private String code;

    private String description;

    private String msg;

    private String time;

    private String status;

    public Head() {

    }

    public Head(String status, String msg) {
        this.status = status;
        this.msg = msg;
        this.time = currentTime();
    }

    public Head(String code, String description, String status, String msg) {
        this.code = code;
        this.description = description;
        this.status = status;
        this.msg = msg;
        this.time = currentTime();

    }

    public String getMsg() {
        return msg;
    }

    public String getTime() {
        return time;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Head withTime(Date time) {
        this.time = format(time);
        return this;
    }

    public Head withMsg(String message, Object... args) {
        this.msg = String.format(message, args);
        return this;
    }

    private static String currentTime() {
        return format(new Date());
    }

    private static String format(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
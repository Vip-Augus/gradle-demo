package gradle.demo.util.result;

import java.io.Serializable;

/**
 * @author by JingQ on 2018/3/13
 */
public class Message implements Serializable {


    private static final long serialVersionUID = 6421511690190992896L;
    private String code;
    private String description;
    private String text;


    public Message() {
    }

    public Message(String code,String text) {
        super();
        this.code = code;
        this.description = "";
        this.text = text;
    }


    public Message(String code, String description,String text) {
        super();
        this.code = code;
        this.description = description;
        this.text = text;
    }

    public Message(String text){
        this.code = "";
        this.description = "";
        this.text = text;
    }


    public void setCode(String code) {
        this.code = code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return this.code;
    }

    public String getText() {
        return this.text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Message code(String code) {
        this.code = code;
        return this;
    }

    public Message text(String text) {
        this.text = text;
        return this;
    }
}
package gradle.demo.model.dto;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author GJW
 * @date 2018/1/7
 */

@Data
public class NoticeDTO {

    private Integer id;

    private String title;

    private String content;

    private Integer createId;

    private String createUserName;

    private Date createTime;
}

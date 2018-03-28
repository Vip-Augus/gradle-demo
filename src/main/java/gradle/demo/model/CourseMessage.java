package gradle.demo.model;

import lombok.Data;

/**
 * 课程留言消息
 *
 * @author by JingQ on 2018/3/27
 */
@Data
public class CourseMessage {

    private Integer id;

    private String content;

    private String idNumber;

    private Integer userId;

    private String userName;

    private String publishTime;

    private Integer courseId;

    private String createDate;

    private String modifyDate;


}

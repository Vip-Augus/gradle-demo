package gradle.demo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程和用户的关系--谁选修了这门课
 * @author JingQ on 2017/12/24.
 */
@Data
public class CourseUser implements Serializable{

    private static final long serialVersionUID = -7173595158935962596L;

    private Integer id;

    private Integer courseId;

    private Integer userId;

    private Integer userType;

    private String createDate;

    private String modifyDate;

}
package gradle.demo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程记录--每一节课信息
 *
 * @author JingQ on 2017/12/24.
 */
@Data
public class CourseRecord implements Serializable {

    private static final long serialVersionUID = 5917177207935651806L;

    private Integer id;

    private String courseName;

    private String courseFileUrl;

    private Integer courseId;

    private String date;

    private String uploadEndTime;

    private Integer classBegin;

    private Integer classEnd;

    private Integer classroomId;

    /**
     * 签到识别码
     */
    private String checkCode;

    private Integer allStudentCount;

    private String createDate;

    private String modifyDate;

    /**
     * 上课时间
     */
    private String classTime;

}
package gradle.demo.model.dto;

import lombok.Data;

/**
 * 课时记录
 *
 * @author by JingQ on 2018/3/27
 */
@Data
public class CourseRecordDTO {

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

    /**
     * 是否签到
     */
    private Boolean isCheckIn;

    /**
     * 全部学生人数
     */
    private Integer allStudentCount;

    /**
     * 已签到人数
     */
    private Integer checkCount;

    /**
     * 学生提交的作业
     */
    private String homeworkUrl;

    /**
     * 老师批改的作业
     */
    private String markHomeworkUrl;

}

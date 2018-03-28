package gradle.demo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程作业详情--提交记录
 * @author JingQ on 2017/12/24.
 */
@Data
public class Homework implements Serializable{

    private static final long serialVersionUID = 6825913423924664739L;

    private Integer id;

    /**
     * 学生提交作业
     */
    private String homeworkUrl;

    /**
     * 老师批改完后的结果
     */
    private String markHomeworkUrl;

    private String homeworkName;

    private String uploadDate;

    private String createDate;

    private String modifyDate;

    private Integer courseRecordId;

    private String score;

    private String comment;

    private String uploadName;

    private Integer courseId;

    private Integer userId;

    private String idNumber;
}
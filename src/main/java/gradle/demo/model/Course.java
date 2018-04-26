package gradle.demo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 实验课信息
 *
 * @author JingQ on 2017/12/24.
 */
@Data
public class Course implements Serializable {

    private static final long serialVersionUID = -4725272480564053483L;

    private Integer id;

    /**
     * 课程名字
     */
    private String name;

    /**
     * 简介
     */
    private String briefIntroduction;

    /**
     * 冗余字段
     */
    private Integer classroomId;

    /**
     * 开始学期---yyyy
     */
    private String beginPeriod;

    /**
     * 结束学期---yyyy
     */
    private String endPeriod;

    /**
     * 学期：1 上学期， 2 下学期
     */
    private Integer term;

    /**
     * 周几
     */
    private Integer day;

    /**
     * 课时
     */
    private Integer lesson;

    /**
     * 第几节课开始
     */
    private Integer classBegin;

    /**
     * 第几节课下课
     */
    private Integer classEnd;

    /**
     * 教学楼号码
     */
    private Integer buildingNumber;

    /**
     * 教室门牌
     */
    private String classroom;

    /**
     * 加入课程的识别码
     */
    private String code;

    /**
     * 教师ID列表
     */
    private String tIds;

    private String teacherName;

    private String joinEndTime;

    private String createDate;

    private String modifyDate;

}
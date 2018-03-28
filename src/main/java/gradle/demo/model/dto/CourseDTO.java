package gradle.demo.model.dto;

import gradle.demo.base.BaseDTO;
import gradle.demo.model.User;
import lombok.Data;

import java.util.List;

/**
 * 课程信息
 *
 * @author by JingQ on 2018/1/2
 */
@Data
public class CourseDTO extends BaseDTO {

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
     * 加入课程的识别码
     */
    private String code;

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
     * 老师
     */
    private List<User> teachers;

}

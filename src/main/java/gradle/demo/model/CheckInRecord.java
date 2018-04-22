package gradle.demo.model;

import lombok.Data;

/**
 * 签到记录
 *
 * @author by JingQ on 2018/3/14
 */
@Data
public class CheckInRecord {

    private Integer id;

    private String idNumber;

    private Integer userId;

    private String time;

    private Integer courseId;

    private Integer courseRecordId;

    /**
     * 冗余字段，是否签到
     */
    private Boolean isCheck;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    private String createDate;

    private String modifyDate;

}

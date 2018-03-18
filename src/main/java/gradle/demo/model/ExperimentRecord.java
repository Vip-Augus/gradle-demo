package gradle.demo.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程记录--每一节课信息
 *
 * @author JingQ on 2017/12/24.
 */
@Data
public class ExperimentRecord implements Serializable {

    private static final long serialVersionUID = 5917177207935651806L;

    private Integer id;

    private String epName;

    private String epFileUrl;

    private Integer epId;

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

    private String createDate;

    private String modifyDate;

    @Override
    public String toString() {
        return "ExperimentRecord{" +
                "id=" + id +
                ", epName='" + epName + '\'' +
                ", epFileUrl='" + epFileUrl + '\'' +
                ", epId=" + epId +
                ", date='" + date + '\'' +
                ", uploadEndTime='" + uploadEndTime + '\'' +
                ", classBegin=" + classBegin +
                ", classEnd=" + classEnd +
                ", classroomId='" + classroomId + '\'' +
                ", checkCode='" + checkCode + '\'' +
                ", createDate='" + createDate + '\'' +
                ", modifyDate='" + modifyDate + '\'' +
                '}';
    }
}
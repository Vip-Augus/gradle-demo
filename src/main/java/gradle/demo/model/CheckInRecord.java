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

    private Integer epId;

    private Integer epRecordId;

    private String createDate;

    private String modifyDate;

}

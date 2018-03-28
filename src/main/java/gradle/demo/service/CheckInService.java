package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.CheckInRecord;
import gradle.demo.model.User;

import java.util.List;

/**
 * @author by JingQ on 2018/3/14
 */
public interface CheckInService extends BaseServiceTemplate<CheckInRecord> {

    /**
     * 签到
     *
     * @param epId       课程ID
     * @param epRecordId 课程记录ID
     * @param checkCode  签到认证码
     * @param user       用户
     * @return 签到结果
     */
    Boolean signIn(Integer epId, Integer epRecordId, String checkCode, User user);

    /**
     * 通过学号，倒序获取签到记录
     *
     * @param idNumber 学号
     * @return 签到记录列表
     */
    List<CheckInRecord> getByIdNumber(String idNumber);

    /**
     * 通过课时ID获取签到列表
     *
     * @param courseRecordId 课时
     * @return 签到列表
     */
    List<CheckInRecord> getByCourseRecordId(Integer courseRecordId);

    /**
     * 根据课时ID和用户ID
     *
     * @param courseRecordId 课时ID
     * @param idNumber       学号
     * @return 签到记录
     */
    CheckInRecord getByCourserRecordIdAndUserId(Integer courseRecordId, String idNumber);
}

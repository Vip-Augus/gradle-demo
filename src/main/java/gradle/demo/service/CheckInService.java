package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.CheckInRecord;
import gradle.demo.model.User;

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
}

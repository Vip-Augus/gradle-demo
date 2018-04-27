package gradle.demo.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import gradle.demo.dao.CheckInMapper;
import gradle.demo.dao.CourseUserMapper;
import gradle.demo.model.CheckInRecord;
import gradle.demo.model.Course;
import gradle.demo.model.CourseRecord;
import gradle.demo.model.CourseUser;
import gradle.demo.model.User;
import gradle.demo.model.enums.SignTime;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.CheckInService;
import gradle.demo.service.CourseRecordService;
import gradle.demo.service.CourseService;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.StringUtil;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ExceptionDefinitions;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author by JingQ on 2018/3/14
 */
@Slf4j
@Service
public class CheckInServiceImpl implements CheckInService {

    private static final String HOUR_PATTERN = "HH:mm";

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private CourseUserMapper courseUserMapper;

    @Autowired
    private CourseService courserServiceImpl;

    @Autowired
    private CourseRecordService courseRecordServiceImpl;

    @Override
    public CheckInRecord getById(Integer id) {
        return checkInMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(CheckInRecord record) {
        return checkInMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return checkInMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CheckInRecord add(CheckInRecord record) {
        checkInMapper.insert(record);
        return record;
    }

    @Override
    public Boolean signIn(Integer courseId, Integer courseRecordId, String checkCode, User user) {
        CheckInRecord oldRecord = checkInMapper.selectByIdNumberAndCourseRecord(user.getIdNumber(), courseRecordId);
        if (oldRecord != null) {
            throw new BusinessException(ExceptionDefinitions.HAVE_CHECK_IN);
        }
        long currentTime = System.currentTimeMillis();
        Course course = courserServiceImpl.getById(courseId);
        SignTime signTime = SignTime.fromTime(PeriodUtil.format(currentTime, HOUR_PATTERN));
        if (SignTime.OTHER.equals(signTime) || !signTime.getCode().equals(course.getClassBegin())) {
            throw new BusinessException(ExceptionDefinitions.NOT_IN_SIGN_TIME);
        }
        CourseRecord courserRecord = courseRecordServiceImpl.getById(courseRecordId);
        //判断签到识别码
        if (StringUtil.isNullOrEmpty(checkCode) || !StringUtil.isEquals(checkCode, courserRecord.getCheckCode())) {
            throw new BusinessException(ExceptionDefinitions.CHECK_CODE_INCORRECT);
        }
        //插入记录
        CheckInRecord record = new CheckInRecord();
        record.setCourseId(courseId);
        record.setCourseRecordId(courseRecordId);
        record.setIdNumber(user.getIdNumber());
        record.setUserId(user.getId());
        checkInMapper.insert(record);
        return true;
    }

    @Override
    public List<CheckInRecord> getByIdNumber(String idNumber) {
        return checkInMapper.selectByIdNumber(idNumber);
    }

    @Override
    public List<CheckInRecord> getByCourseRecordId(Integer courseRecordId) {
        CourseRecord courseRecord = courseRecordServiceImpl.getById(courseRecordId);
        if (courseRecord == null) {
            throw new BusinessException("QD0000002", "没有这门课时");
        }
        List<CourseUser> courseUsers = courseUserMapper.selectByCourseId(courseRecord.getCourseId());
        List<CheckInRecord> selectCheckInRecords = checkInMapper.selectByCourseRecordId(courseRecordId);
        Map<Integer, CheckInRecord> checkUserMap = Maps.uniqueIndex(selectCheckInRecords, new Function<CheckInRecord, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable CheckInRecord input) {
                return input.getUserId();
            }
        });
        List<CheckInRecord> result = Lists.newArrayList();
        for (CourseUser courseUser : courseUsers) {
            if (UserType.STUDENT.equals(UserType.fromCode(courseUser.getUserType()))) {
                CheckInRecord record = checkUserMap.get(courseUser.getUserId());
                //根据isCheck判断是否签到
                if (record == null) {
                    record = new CheckInRecord();
                    record.setUserId(courseUser.getUserId());
                    record.setIdNumber(courseUser.getIdNumber());
                    record.setCourseRecordId(courseRecordId);
                    record.setIsCheck(false);
                } else {
                    record.setIsCheck(true);
                }
                result.add(record);
            }
        }
        return result;
    }

    @Override
    public CheckInRecord getByCourserRecordIdAndUserId(Integer courseRecordId, String idNumber) {
        return checkInMapper.selectByIdNumberAndCourseRecord(idNumber, courseRecordId);
    }
}
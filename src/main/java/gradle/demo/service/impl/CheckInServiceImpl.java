package gradle.demo.service.impl;

import gradle.demo.dao.CheckInMapper;
import gradle.demo.model.CheckInRecord;
import gradle.demo.model.Experiment;
import gradle.demo.model.ExperimentRecord;
import gradle.demo.model.User;
import gradle.demo.model.enums.SignTime;
import gradle.demo.service.CheckInService;
import gradle.demo.service.ExperimentRecordService;
import gradle.demo.service.ExperimentService;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.StringUtil;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ExceptionDefinitions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ExperimentService epServiceImpl;

    @Autowired
    private ExperimentRecordService epRecordServiceImpl;

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
    public Boolean signIn(Integer epId, Integer epRecordId, String checkCode, User user) {
        CheckInRecord oldRecord = checkInMapper.selectByIdNumberAndEpRecord(user.getIdNumber(), epRecordId);
        if (oldRecord != null) {
            throw new BusinessException(ExceptionDefinitions.HAVE_CHECK_IN);
        }
        long currentTime = System.currentTimeMillis();
        Experiment experiment = epServiceImpl.getById(epId);
        SignTime signTime = SignTime.fromTime(PeriodUtil.format(currentTime, HOUR_PATTERN));
        if (SignTime.OTHER.equals(signTime) || !signTime.getCode().equals(experiment.getClassBegin())) {
            throw new BusinessException(ExceptionDefinitions.NOT_IN_SIGN_TIME);
        }
        ExperimentRecord epRecord = epRecordServiceImpl.getById(epRecordId);
        //判断签到识别码
        if (StringUtil.isNullOrEmpty(checkCode) || !StringUtil.isEquals(checkCode, epRecord.getCheckCode())) {
            throw new BusinessException(ExceptionDefinitions.CHECK_CODE_INCORRECT);
        }
        //插入记录
        CheckInRecord record = new CheckInRecord();
        record.setEpId(epId);
        record.setEpRecordId(epRecordId);
        record.setIdNumber(user.getIdNumber());
        record.setUserId(user.getId());
        checkInMapper.insert(record);
        return true;
    }
}
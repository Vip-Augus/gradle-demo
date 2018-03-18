package gradle.demo.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import gradle.demo.dao.CheckInMapper;
import gradle.demo.dao.ExperimentRecordMapper;
import gradle.demo.model.CheckInRecord;
import gradle.demo.model.ExperimentRecord;
import gradle.demo.service.ExperimentRecordService;
import gradle.demo.util.MD5Util;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author JingQ on 2017/12/24.
 */
@Service
public class ExperimentRecordServiceImpl implements ExperimentRecordService {

    @Autowired
    private ExperimentRecordMapper experimentRecordMapper;

    @Autowired
    private CheckInMapper checkInMapper;

    @Override
    public ExperimentRecord getById(Integer id) {
        return experimentRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(ExperimentRecord record) {
        return experimentRecordMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return experimentRecordMapper.deleteByPrimaryKey(id);
    }

    @Override
    public ExperimentRecord add(ExperimentRecord record) {
        //生成四位的签到识别码
        record.setCheckCode(MD5Util.getCheckCode());
        experimentRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<ExperimentRecord> getListByEPId(Integer epId) {
        return experimentRecordMapper.selectByEPId(epId);
    }

    @Override
    public List<ExperimentRecord> getListByClassroomId(Integer classroomId) {
        return experimentRecordMapper.selectByClassroomId(classroomId);
    }

    @Override
    public List<ExperimentRecord> getByEPIdAndUser(Integer epId, String idNumber) {
        List<ExperimentRecord> result = experimentRecordMapper.selectByEPId(epId);
        List<CheckInRecord> checkInRecords = checkInMapper.selectByIdNumberAndEpRecordIds(idNumber, result.stream().map(ExperimentRecord::getId).collect(Collectors.toList()));
        Set<Integer> epRecordSet = Sets.newHashSet();
        if (!CollectionUtils.isEmpty(checkInRecords)) {
            epRecordSet.addAll(checkInRecords.stream().map(CheckInRecord::getEpRecordId).collect(Collectors.toSet()));
        }
        for (int i = 0, length = result.size(); i < length; i++){
            ExperimentRecord record = result.get(i);
            record.setCheckCode(null);
            if (epRecordSet.contains(record.getId())) {
                record.setIsCheckIn(true);
            }
        }
        return result;
    }
}

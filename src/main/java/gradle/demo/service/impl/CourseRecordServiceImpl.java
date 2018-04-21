package gradle.demo.service.impl;

import com.google.common.collect.Maps;
import gradle.demo.dao.CheckInMapper;
import gradle.demo.dao.CourseRecordMapper;
import gradle.demo.dao.HomeworkMapper;
import gradle.demo.model.CheckInRecord;
import gradle.demo.model.CourseRecord;
import gradle.demo.model.User;
import gradle.demo.model.dto.CourseRecordDTO;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.CourseRecordService;
import gradle.demo.service.CourseUserService;
import gradle.demo.util.MD5Util;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.convert.CourseRecordConvert;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author JingQ on 2017/12/24.
 */
@Service
public class CourseRecordServiceImpl implements CourseRecordService {

    private static final int SECURE_KEY_LENGTH = 4;

    @Autowired
    private CourseRecordMapper courseRecordMapper;

    @Autowired
    private CourseUserService courseUserServiceImpl;

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private CourseRecordConvert courseRecordConvert;

    @Override
    public CourseRecord getById(Integer id) {
        return courseRecordMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(CourseRecord record) {
        return courseRecordMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return courseRecordMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CourseRecord add(CourseRecord record) {
        //生成四位的签到识别码
        record.setCheckCode(MD5Util.getCheckCode(SECURE_KEY_LENGTH));
        courseRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<CourseRecord> getListByCourseId(Integer courseId) {
        return courseRecordMapper.selectByCId(courseId);
    }

    @Override
    public List<CourseRecord> getListByClassroomId(Integer classroomId) {
        return courseRecordMapper.selectByClassroomId(classroomId);
    }

    @Override
    public List<CourseRecordDTO> getByCourseIdAndUser(Integer courseId, User user) {
        // 当前日期
        String currentTime = PeriodUtil.format(System.currentTimeMillis(), "yyyy-MM-dd");
        List<CourseRecord> tmpRecords = courseRecordMapper.selectByCId(courseId);

        if (CollectionUtils.isEmpty(tmpRecords)) {
            return Lists.newArrayList();
        }
        //过滤在当前日期之前的课时
        List<CourseRecord> records = Lists.newArrayList();
        for (CourseRecord courseRecord : tmpRecords) {
            if (courseRecord.getClassTime().compareTo(currentTime) <= 0) {
                records.add(courseRecord);
            }
        }

        List<CourseRecordDTO> result = courseRecordConvert.records2DTOS(records);
        if (!CollectionUtils.isEmpty(records)) {
            //学生处理逻辑
            if (UserType.STUDENT.equals(UserType.fromCode(user.getType()))) {
                getStudentRecords(result, user, courseId);
            } else {
                //老师处理逻辑
                //学生数量 = 总数量 - 老师
                int studentCount = courseUserServiceImpl.getCourseCountByCId(courseId) - 1;
                getTeacherRecords(result, courseId, studentCount);
            }
         }
        return result;
    }

    @Override
    public int batchAdd(List<CourseRecord> recordList) {
        if (CollectionUtils.isEmpty(recordList)) {
            return 0;
        }
        return courseRecordMapper.batchInsert(recordList);
    }

    @Override
    public CourseRecord getByClassTimeAndCID(String classTime, Integer courseId) {
        return courseRecordMapper.selectByClassTimeAndCID(classTime, courseId);
    }

    private void getStudentRecords(List<CourseRecordDTO> result, User user, Integer courseId) {
        //学生签到记录
        List<CheckInRecord> checkInRecords = checkInMapper.selectByIdNumberAndCourseRecordIds(user.getIdNumber(), result.stream().map(CourseRecordDTO::getId).collect(Collectors.toList()));
        Set<Integer> courseRecordSet = Sets.newHashSet();
        if (!CollectionUtils.isEmpty(checkInRecords)) {
            courseRecordSet.addAll(checkInRecords.stream().map(CheckInRecord::getCourseRecordId).collect(Collectors.toSet()));
        }
        for (CourseRecordDTO record : result) {
            if (courseRecordSet.contains(record.getId())) {
                record.setIsCheckIn(true);
            } else {
                record.setIsCheckIn(false);
            }
        }
    }

    private void getTeacherRecords(List<CourseRecordDTO> result, int courseId, int studentCount) {
        List<CheckInRecord> checkInRecords = checkInMapper.selectByCourseId(courseId);
        Map<Integer, List<Integer>> courseRecordMap = Maps.newHashMap();
        //将课时和签到记录匹配起来
        for (CheckInRecord record : checkInRecords) {
            int courseRecordId = record.getCourseRecordId();
            courseRecordMap.computeIfAbsent(courseRecordId, k -> Lists.newArrayList());
            courseRecordMap.get(courseRecordId).add(1);
        }
        for (CourseRecordDTO courseRecord : result) {
            courseRecord.setAllStudentCount(studentCount);
            courseRecord.setCheckCount(courseRecordMap.get(courseRecord.getId()) == null ? 0 : courseRecordMap.get(courseRecord.getId()).size());
        }
    }
}

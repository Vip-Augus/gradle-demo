package gradle.demo.service.impl;

import gradle.demo.dao.CourseMapper;
import gradle.demo.model.Classroom;
import gradle.demo.model.Course;
import gradle.demo.model.CourseUser;
import gradle.demo.model.enums.ClassTime;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.ClassroomService;
import gradle.demo.service.CourseService;
import gradle.demo.service.CourseUserService;
import gradle.demo.util.MD5Util;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ExceptionDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    private static final int SECURE_KEY_LENGTH = 6;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseUserService courseUserServiceImpl;

    @Autowired
    private ClassroomService classroomServiceImpl;


    @Override
    public Course getById(Integer id) {
        return courseMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Course record) {
//        if (!isValid(record)) {
//            throw new BusinessException(new ExceptionDefinition("EP000101", "实验室时间冲突"));
//        }
        return courseMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return courseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Course add(Course record) {
//        if (!isValid(record)) {
//            BusinessException exception = new BusinessException(new ExceptionDefinition("0001111", "实验室地点被占用了,无法添加该课程"));
//            log.error("实验室地点被占用", exception);
//            throw exception;
//        }
        //生成六位识别码
        String secureKey = MD5Util.getCheckCode(6);
        while (courseMapper.selectByCode(secureKey) != null) {
            secureKey = MD5Util.getCheckCode(6);
        }
        record.setCode(secureKey);
        courseMapper.insert(record);
        CourseUser courseUser = new CourseUser();
        courseUser.setCourseId(record.getId());
        courseUser.setUserId(Integer.valueOf(record.getTIds()));
        courseUser.setUserType(UserType.TEACHER.getCode());
        courseUserServiceImpl.add(courseUser);
        return record;
    }

    @Override
    public List<Course> getByIds(List<Integer> ids) {
        return courseMapper.selectByIds(ids);
    }

    @Override
    public List<Course> getUsingStatementByCID(Integer cid, Integer day, String currentPeriod) {
        return courseMapper.selectInUseByClassroomId(cid, day, currentPeriod);
    }

    @Override
    public Course getByCode(String code) {
        return courseMapper.selectByCode(code);
    }

    /**
     * 校验更新和插入时该实验室是否可用
     * @param record            实验课信息
     * @return                  实验室是否可用
     */
    private boolean isValid(Course record) {
        Course oldCourse = courseMapper.selectByPrimaryKey(record.getId());
        ClassTime classTime = ClassTime.fromCode(record.getDay());
        Classroom classroom = classroomServiceImpl.getById(record.getClassroomId());
        List<Integer> periodsBegin = classroomServiceImpl.getPeriods(classTime, record.getBeginPeriod(), classroom);
        List<Integer> periodsEnd = classroomServiceImpl.getPeriods(classTime, record.getEndPeriod(), classroom);
        if (oldCourse != null) {
            List<Integer> periodsOld = PeriodUtil.getSectionClass(oldCourse.getClassBegin(), oldCourse.getClassEnd());
            if (periodsBegin.containsAll(periodsOld)) {
                periodsBegin.removeAll(periodsOld);
            }
            if (periodsEnd.containsAll(periodsOld)) {
                periodsEnd.removeAll(periodsOld);
            }
        }
        //实验室地点被占用了,无法添加该课程
        if (periodsBegin.contains(record.getClassBegin()) || periodsBegin.contains(record.getClassEnd())
                || periodsEnd.contains(record.getClassBegin()) || periodsEnd.contains(record.getClassEnd())) {
            return false;
        }
        return true;
    }
}
package gradle.demo.service.impl;

import gradle.demo.dao.CourseMapper;
import gradle.demo.model.Classroom;
import gradle.demo.model.Course;
import gradle.demo.model.CourseRecord;
import gradle.demo.model.CourseUser;
import gradle.demo.model.User;
import gradle.demo.model.enums.ClassTime;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.ClassroomService;
import gradle.demo.service.CourseRecordService;
import gradle.demo.service.CourseService;
import gradle.demo.service.CourseUserService;
import gradle.demo.service.UserService;
import gradle.demo.util.MD5Util;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ExceptionDefinition;
import gradle.demo.util.result.ExceptionDefinitions;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    private static final String BEGIN_TIME_PATTERN = "yyyy-MM-dd";

    private static final int SECURE_KEY_LENGTH = 4;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseUserService courseUserServiceImpl;

    @Autowired
    private CourseRecordService courseRecordServiceImpl;

    @Autowired
    private ClassroomService classroomServiceImpl;

    @Autowired
    private UserService userServiceImpl;


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
        //校验时间
        if (record.getDay() != PeriodUtil.getDayOfWeek(record.getBeginPeriod(), BEGIN_TIME_PATTERN)) {
            throw new BusinessException(ExceptionDefinitions.INCORRECT_CLASS_TIME);
        }
        //生成六位识别码
        String secureKey = MD5Util.getCheckCode(6);
        while (courseMapper.selectByCode(secureKey) != null) {
            secureKey = MD5Util.getCheckCode(6);
        }
        record.setCode(secureKey);
        courseMapper.insert(record);

        //添加对应的课时信息
        Calendar calendar = Calendar.getInstance();
        Date date = PeriodUtil.parse(record.getBeginPeriod(), BEGIN_TIME_PATTERN);
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        List<CourseRecord> courseRecordList = Lists.newArrayList();
        for (int i = 0; i < record.getLesson(); i++) {
            CourseRecord courseRecord = new CourseRecord();
            courseRecord.setCheckCode(MD5Util.getCheckCode(SECURE_KEY_LENGTH));
            courseRecord.setCourseId(record.getId());
            courseRecord.setCourseName(record.getName());
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            courseRecord.setClassTime(PeriodUtil.format(calendar.getTime(), BEGIN_TIME_PATTERN));
            courseRecordList.add(courseRecord);
        }
        courseRecordServiceImpl.batchAdd(courseRecordList);

        //绑定老师和课程的关系
        CourseUser courseUser = new CourseUser();
        courseUser.setCourseId(record.getId());
        courseUser.setUserId(Integer.valueOf(record.getTIds()));
        courseUser.setUserType(UserType.TEACHER.getCode());
        User user = userServiceImpl.getById(courseUser.getUserId());
        courseUser.setIdNumber(user.getIdNumber());
        courseUser.setUserName(user.getName());
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

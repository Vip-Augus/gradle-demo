package gradle.demo.service.impl;

import gradle.demo.dao.ClassroomMapper;
import gradle.demo.model.Course;
import gradle.demo.util.result.ExceptionDefinitions;
import gradle.demo.model.Classroom;
import gradle.demo.model.dto.ClassroomQueryParam;
import gradle.demo.model.enums.ClassTime;
import gradle.demo.model.enums.ClassroomStatus;
import gradle.demo.service.ClassroomService;
import gradle.demo.service.CourseService;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.result.BusinessException;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Author by JingQ on 2018/1/4
 */
@Service
public class ClassroomServiceImpl implements ClassroomService {

    @Autowired
    private ClassroomMapper classroomMapper;

    @Autowired
    private CourseService courseServiceImpl;

    @Override
    public Classroom getById(Integer id) {
        return classroomMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Classroom record) {
        return classroomMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return classroomMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Classroom add(Classroom record) {
        ClassroomQueryParam queryParam = new ClassroomQueryParam();
        queryParam.setBuildingNumber(record.getBuildingNumber());
        queryParam.setClassroom(record.getClassroom());
        if (!CollectionUtils.isEmpty(classroomMapper.query(queryParam))) {
            throw new BusinessException(ExceptionDefinitions.CLASSROOM_CONFLICT);
        }
        classroomMapper.insert(record);
        return record;
    }

    @Override
    public List<Classroom> getList(ClassroomQueryParam queryParam) {
        long currentTime = System.currentTimeMillis();
        String classTimeFormat = PeriodUtil.format(currentTime, "HH:mm");
        String currentPeriod = PeriodUtil.format(currentTime, "yyyy-MM-dd");
        //现在上的是第几节课
        ClassTime classTime = ClassTime.fromTime(classTimeFormat);
        List<Classroom> classrooms = classroomMapper.query(queryParam);
        for (Classroom classroom : classrooms) {
            if (ClassroomStatus.STOP.getCode() == classroom.getStatus()) {
                //如果实验室状态为停用,跳过判断当天使用情况
                continue;
            }
            if (ClassTime.OTHER == classTime) {
                classroom.setStatus(ClassroomStatus.FREE.getCode());
                continue;
            }
            if (getPeriods(classTime, currentPeriod, classroom).contains(classTime.getCode())) {
                classroom.setStatus(ClassroomStatus.TAKE_UP.getCode());
            } else {
                classroom.setStatus(ClassroomStatus.FREE.getCode());
            }
        }
        return classrooms;
    }

    @Override
    public List<Course> getUsingStatement(Integer cid, String currentTime) {
        return courseServiceImpl.getUsingStatementByCID(cid, null, currentTime);
    }

    @Override
    public List<Integer> getPeriods(ClassTime classTime, String currentPeriod, Classroom classroom) {
        if (classTime == null) {
            return Lists.newArrayList();
        }
        //某一天该实验室所上的实验课
        List<Course> courses = courseServiceImpl.getUsingStatementByCID(classroom.getId(), classTime.getCode(), currentPeriod);
        List<Integer> usingTimes = Lists.newArrayList();
        for (Course course : courses) {
            usingTimes.addAll(PeriodUtil.getSectionClass(course.getClassBegin(), course.getClassEnd()));
        }
        return usingTimes;
    }
}

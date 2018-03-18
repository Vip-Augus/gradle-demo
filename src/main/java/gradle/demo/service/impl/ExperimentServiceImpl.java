package gradle.demo.service.impl;

import gradle.demo.dao.ExperimentMapper;
import gradle.demo.model.Classroom;
import gradle.demo.model.Course;
import gradle.demo.model.Experiment;
import gradle.demo.model.enums.ClassTime;
import gradle.demo.service.ClassroomService;
import gradle.demo.service.CourseService;
import gradle.demo.service.ExperimentService;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ExceptionDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author JingQ on 2017/12/24.
 */
@Slf4j
@Service
public class ExperimentServiceImpl implements ExperimentService {

    @Autowired
    private ExperimentMapper experimentMapper;

    @Autowired
    private ClassroomService classroomServiceImpl;

    @Autowired
    private CourseService courseServiceImpl;

    @Override
    public Experiment getById(Integer id) {
        return experimentMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Experiment record) {
        if (!isValid(record)) {
            throw new BusinessException(new ExceptionDefinition("EP000101", "实验室时间冲突"));
        }
        return experimentMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return experimentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Experiment add(Experiment record) {
        Course course = courseServiceImpl.getById(record.getCourseId());
        //没有这门课---异常抛出后续再加
        if (course == null) {
            BusinessException exception = new BusinessException(new ExceptionDefinition("0000111", "没有这门课"));
            log.error("没有这门课", exception);
            throw exception;
        }
        if (!isValid(record)) {
            BusinessException exception = new BusinessException(new ExceptionDefinition("0001111", "实验室地点被占用了,无法添加该课程"));
            log.error("实验室地点被占用", exception);
            throw exception;
        }
        experimentMapper.insert(record);
        return record;
    }

    @Override
    public List<Experiment> getByIds(List<Integer> ids) {
        return experimentMapper.selectByIds(ids);
    }

    @Override
    public List<Experiment> getUsingStatementByCID(Integer cid, Integer day, String currentPeriod) {
        return experimentMapper.selectInUseByClassroomId(cid, day, currentPeriod);
    }

    /**
     * 校验更新和插入时该实验室是否可用
     * @param record            实验课信息
     * @return                  实验室是否可用
     */
    private boolean isValid(Experiment record) {
        Experiment oldExperiment = experimentMapper.selectByPrimaryKey(record.getId());
        ClassTime classTime = ClassTime.fromCode(record.getDay());
        Classroom classroom = classroomServiceImpl.getById(record.getClassroomId());
        List<Integer> periodsBegin = classroomServiceImpl.getPeriods(classTime, record.getBeginPeriod(), classroom);
        List<Integer> periodsEnd = classroomServiceImpl.getPeriods(classTime, record.getEndPeriod(), classroom);
        if (oldExperiment != null) {
            List<Integer> periodsOld = PeriodUtil.getSectionClass(oldExperiment.getClassBegin(), oldExperiment.getClassEnd());
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

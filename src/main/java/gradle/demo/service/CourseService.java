package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.Course;

import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
public interface CourseService extends BaseServiceTemplate<Course> {

    /**
     * 批量查询
     *
     * @param ids 课程IDs
     * @return 课程列表
     */
    List<Course> getByIds(List<Integer> ids);

    /**
     * 某间实验室最近上的实验课
     *
     * @param cid           教室地点id
     * @param day           周几
     * @param currentPeriod 当前时间段(格式:yyyy-MM-dd)
     * @return
     */
    List<Course> getUsingStatementByCID(Integer cid, Integer day, String currentPeriod);

    /**
     * 根据识别码查询
     *
     * @param code 识别码
     * @return 课程信息
     */
    Course getByCode(String code);
}

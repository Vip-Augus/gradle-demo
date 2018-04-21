package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.CourseRecord;
import gradle.demo.model.User;
import gradle.demo.model.dto.CourseRecordDTO;

import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
public interface CourseRecordService extends BaseServiceTemplate<CourseRecord> {

    /**
     * 查询
     *
     * @param courseId 课ID
     * @return 记录
     */
    List<CourseRecord> getListByCourseId(Integer courseId);

    /**
     * 查询
     *
     * @param classroomId 教室ID
     * @return 记录
     */
    List<CourseRecord> getListByClassroomId(Integer classroomId);

    /**
     * 查询
     *
     * @param courseId     课程ID
     * @param user 用户
     * @return 记录
     */
    List<CourseRecordDTO> getByCourseIdAndUser(Integer courseId, User user);

    /**
     * 批量插入
     *
     * @param recordList
     * @return
     */
    int batchAdd(List<CourseRecord> recordList);

    /**
     * 根据课程时间和课程ID获取课时
     *
     * @param classTime
     * @param courseId
     * @return
     */
    CourseRecord getByClassTimeAndCID(String classTime, Integer courseId);
}

package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.CourseUser;

import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
public interface CourseUserService extends BaseServiceTemplate<CourseUser> {

    /**
     * 根据用户id查询所选课程
     *
     * @param userId 用户ID
     * @return 课程ID列表
     */
    List<Integer> getCIDsByUserID(Integer userId);

    /**
     * 批量插入
     *
     * @param courseId 实验课程ID
     * @param userIds  用户ID列表
     * @return 批量插入的数量
     */
    int batchAdd(Integer courseId, List<Integer> userIds);

    /**
     * 根据课程id获取用户Id列表
     *
     * @param courseId 课程ID
     * @return 用户ID列表
     */
    List<Integer> getUserIdsByCId(Integer courseId);

    /**
     * 获取与这门课有关联的用户
     *
     * @param courseId 课程ID
     * @return 用户总数
     */
    int getCourseCountByCId(Integer courseId);
}

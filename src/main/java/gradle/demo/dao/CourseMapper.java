package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
@Mapper
public interface CourseMapper extends BaseMapperTemplate<Course> {

    /**
     * 批量查询
     *
     * @param ids 课程ID列表
     * @return 课程列表
     */
    List<Course> selectByIds(@Param("ids") List<Integer> ids);

    /**
     * 查询室使用情况--正在上的课
     *
     * @param classroomId 室ID
     * @param day         周几
     * @param currentTime 当前时间
     * @return
     */
    List<Course> selectInUseByClassroomId(@Param("classroomId") Integer classroomId, @Param("day") Integer day, @Param("currentTime") String currentTime);

    /**
     * 根据识别码进行课程查询
     *
     * @param code 课程识别码
     * @return 课程信息
     */
    Course selectByCode(@Param("code") String code);
}

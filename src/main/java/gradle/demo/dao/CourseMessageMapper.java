package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.CourseMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author by JingQ on 2018/3/27
 */
@Mapper
public interface CourseMessageMapper extends BaseMapperTemplate<CourseMessage> {

    /**
     * 根据课程ID查询
     *
     * @param courseId 课程ID
     * @return 留言列表
     */
    List<CourseMessage> selectByCourseId(@Param("courseId") Integer courseId);

    /**
     * 根据位移量查找
     *
     * @param courseId 课程ID
     * @param offset   位移量
     * @param limit    每页数量
     * @return 留言列表
     */
    List<CourseMessage> selectByParam(@Param("courseId") Integer courseId, @Param("offset") Integer offset, @Param("limit") Integer limit);
}

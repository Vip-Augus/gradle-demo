package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.HomeworkOuter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author by JingQ on 2018/4/20
 */
@Mapper
public interface HomeworkOuterMapper extends BaseMapperTemplate<HomeworkOuter> {

    /**
     * 根据课程ID查找作业外层列表
     *
     * @param courseId
     * @return
     */
    List<HomeworkOuter> selectByCourseId(@Param("courseId") Integer courseId);

}

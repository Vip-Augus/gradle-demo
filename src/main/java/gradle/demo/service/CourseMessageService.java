package gradle.demo.service;


import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.CourseMessage;
import gradle.demo.model.dto.CourseMessageQueryParam;

import java.util.List;

/**
 * @author by JingQ on 2018/3/27
 */
public interface CourseMessageService extends BaseServiceTemplate<CourseMessage> {

    /**
     * 根据课程ID获取
     *
     * @param courseId 课程ID
     * @return 留言列表
     */
    List<CourseMessage> getByCourseId(Integer courseId);

    /**
     * 获取
     *
     * @param param 查询条件
     * @return 留言列表
     */
    List<CourseMessage> getByParam(CourseMessageQueryParam param);

}

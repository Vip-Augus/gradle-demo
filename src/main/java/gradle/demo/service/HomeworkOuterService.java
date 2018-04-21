package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.HomeworkOuter;

import java.util.List;

/**
 * @author by JingQ on 2018/4/20
 */
public interface HomeworkOuterService extends BaseServiceTemplate<HomeworkOuter> {

    /**
     * 根据课程ID进行查询作业外层列表
     *
     * @param courseId
     * @return
     */
    List<HomeworkOuter> getByCourseId(Integer courseId);
}

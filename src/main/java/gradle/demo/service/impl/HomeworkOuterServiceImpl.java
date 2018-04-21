package gradle.demo.service.impl;

import gradle.demo.dao.HomeworkOuterMapper;
import gradle.demo.model.HomeworkOuter;
import gradle.demo.service.HomeworkOuterService;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author by JingQ on 2018/4/20
 */
@Service
public class HomeworkOuterServiceImpl implements HomeworkOuterService{

    @Autowired
    private HomeworkOuterMapper homeworkOuterMapper;

    @Override
    public List<HomeworkOuter> getByCourseId(Integer courseId) {
        List<HomeworkOuter> result = homeworkOuterMapper.selectByCourseId(courseId);
        if (CollectionUtils.isEmpty(result)) {
            return Lists.newArrayList();
        }
        return result;
    }

    @Override
    public HomeworkOuter getById(Integer id) {
        return homeworkOuterMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(HomeworkOuter record) {
        return homeworkOuterMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return homeworkOuterMapper.deleteByPrimaryKey(id);
    }

    @Override
    public HomeworkOuter add(HomeworkOuter record) {
        homeworkOuterMapper.insert(record);
        return record;
    }
}

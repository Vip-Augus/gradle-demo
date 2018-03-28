package gradle.demo.service.impl;

import gradle.demo.dao.CourseMessageMapper;
import gradle.demo.model.CourseMessage;
import gradle.demo.model.dto.CourseMessageQueryParam;
import gradle.demo.service.CourseMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author by JingQ on 2018/3/27
 */
@Service
public class CourseMessageServiceImpl implements CourseMessageService {

    @Autowired
    private CourseMessageMapper courseMessageMapper;

    @Override
    public List<CourseMessage> getByCourseId(Integer courseId) {
        return courseMessageMapper.selectByCourseId(courseId);
    }

    @Override
    public List<CourseMessage> getByParam(CourseMessageQueryParam param) {
        if (param.getPageNo() != null && param.getPageSize() != null) {
            return getByCourseId(param.getCourseId());
        }
        return courseMessageMapper.selectByParam(param.getCourseId(), param.getPageNo()*param.getPageSize(), param.getPageSize());
    }

    @Override
    public CourseMessage getById(Integer id) {
        return courseMessageMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(CourseMessage record) {
        return courseMessageMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return courseMessageMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CourseMessage add(CourseMessage record) {
        courseMessageMapper.insert(record);
        return record;
    }
}

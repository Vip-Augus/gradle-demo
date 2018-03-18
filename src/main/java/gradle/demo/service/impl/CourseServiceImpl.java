package gradle.demo.service.impl;

import gradle.demo.dao.CourseMapper;
import gradle.demo.model.Course;
import gradle.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by GJW on 2018/1/7.
 */
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    CourseMapper courseMapper;

    @Override
    public Course getById(Integer id) {
        return courseMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Course record) {
        return courseMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteById(Integer id) {
        return courseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Course add(Course record) {
        courseMapper.insertSelective(record);
        return record;
    }

    @Override
    public List<Course> getList() {
        return courseMapper.getList();
    }
}

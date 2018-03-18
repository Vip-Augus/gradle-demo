package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.Course;

import java.util.List;

/**
 * Created by GJW on 2018/1/7.
 */
public interface CourseService extends BaseServiceTemplate<Course> {
    List<Course> getList();
}

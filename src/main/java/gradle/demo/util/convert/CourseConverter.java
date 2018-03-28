package gradle.demo.util.convert;

import gradle.demo.model.Course;
import gradle.demo.model.dto.CourseDTO;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 课程信息转换器
 *
 * @author by JingQ on 2018/1/2
 */
@Service
public class CourseConverter extends ModelMapper {

    public CourseDTO course2DTO(Course course) {
        if (course == null) {
            return null;
        }
        return this.map(course, CourseDTO.class);
    }

    public List<CourseDTO> courseList2DTOList(List<Course> courseList) {
        if (CollectionUtils.isEmpty(courseList)) {
            return Lists.newArrayList();
        }
        return this.map(courseList, new TypeToken<List<CourseDTO>>() {
        }.getType());
    }

}

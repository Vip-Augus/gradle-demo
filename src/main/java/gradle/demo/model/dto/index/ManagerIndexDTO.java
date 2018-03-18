package gradle.demo.model.dto.index;

import gradle.demo.model.CourseReview;
import lombok.Data;

import java.util.List;

/**
 * @author guojiawei
 * @date 2018/1/13
 */
@Data
public class ManagerIndexDTO extends TeacherIndexDTO{
    protected List<CourseReview> courseReviewList;
}

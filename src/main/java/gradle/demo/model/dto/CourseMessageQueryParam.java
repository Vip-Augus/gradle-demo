package gradle.demo.model.dto;

import lombok.Data;

/**
 * @author by JingQ on 2018/3/27
 */
@Data
public class CourseMessageQueryParam {

    private Integer pageNo;

    private Integer pageSize;

    private Integer courseId;
}

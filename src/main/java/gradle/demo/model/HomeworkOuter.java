package gradle.demo.model;

import lombok.Data;

/**
 * @author by JingQ on 2018/4/20
 */
@Data
public class HomeworkOuter {

    private Integer id;

    private Integer courseId;

    private String homeworkName;

    private String homeworkContent;

    private Integer createId;

    private String createDate;

    private String modifyDate;
}

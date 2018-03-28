package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.CourseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
@Mapper
public interface CourseRecordMapper extends BaseMapperTemplate<CourseRecord> {

    /**
     * 根据课程ID查询出每一次上课程内容
     *
     * @param courseId 课程ID
     * @return 课程内容列表
     */
    List<CourseRecord> selectByCId(@Param("courseId") Integer courseId);

    /**
     * 根据室地点查询
     *
     * @param classroomId 室ID
     * @return 课程记录
     */
    List<CourseRecord> selectByClassroomId(@Param("classroomId") Integer classroomId);

    /**
     * 更新老师上传后的教材文件链接
     *
     * @param id      课时ID
     * @param fileUrl 文件链接
     * @return 更细记录
     */
    int updateCRFileUrl(@Param("id") Integer id, @Param("fileUrl") String fileUrl);
}

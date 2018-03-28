package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.Homework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
@Mapper
public interface HomeworkMapper extends BaseMapperTemplate<Homework> {

    /**
     * 根据每一次课时查找到学生提交记录---老师用,查询全部
     *
     * @param courseRecordId 实验课记录ID
     * @return 学生提交记录
     */
    List<Homework> selectByCRId(@Param("courseRecordId") Integer courseRecordId);

    /**
     * 根据每一次课时查找到学生提交记录---学生用,查出学生个人的提交记录
     *
     * @param courseRecordId 实验课记录ID
     * @param userId         学生ID
     * @return 学生提交记录
     */
    List<Homework> selectByCRIdAndUserId(@Param("courseRecordId") Integer courseRecordId, @Param("userId") Integer userId);

    /**
     * 查询用户课程中所有作业
     *
     * @param courseId 课程ID
     * @param userId   用户ID
     * @return 作业列表
     */
    List<Homework> selectByCIdAndUserId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    /**
     * 查询作业链接
     *
     * @param courseRecordId 课时ID
     * @return 作业链接列表
     */
    List<String> selectHomeworkUrlsByCourseRecordId(@Param("courseRecordId") Integer courseRecordId);

    /**
     * 打分
     *
     * @param id      主键ID
     * @param score   分数
     * @param comment 评语
     * @return 更新条数
     */
    int markHomework(@Param("id") Integer id, @Param("score") String score, @Param("comment") String comment);

    /**
     * 更新老师批改作业
     *
     * @param idNumber       学生学号
     * @param courseRecordId 课时ID
     * @param url            批改结果
     * @return 更新条数
     */
    int updateMarkHomework(@Param("idNumber") String idNumber, @Param("courseRecordId") Integer courseRecordId, @Param("url") String url);
}

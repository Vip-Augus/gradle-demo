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
     * 根据每一次作业外层ID查找到学生提交记录---老师用,查询全部
     *
     * @param homeworkOuterId 实验课记录ID
     * @return 学生提交记录
     */
    List<Homework> selectByHWOuterId(@Param("homeworkOuterId") Integer homeworkOuterId);

    /**
     * 根据每一次作业外层ID查找到学生提交记录---学生用,查出学生个人的提交记录
     *
     * @param homeworkOuterId 实验课记录ID
     * @param userId          学生ID
     * @return 学生提交记录
     */
    List<Homework> selectByHWOuterIdAndUserId(@Param("homeworkOuterId") Integer homeworkOuterId, @Param("userId") Integer userId);

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
     * @param homeworkOuterId 作业外层IDID
     * @return 作业链接列表
     */
    List<String> selectHomeworkUrlsByHWOuterId(@Param("homeworkOuterId") Integer homeworkOuterId);

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
     * 根据主键ID更新批改作业
     *
     * @param id
     * @param url
     * @return
     */
    int markHomeworkUrl(@Param("id") Integer id, @Param("url") String url);
    /**
     * 更新老师批改作业
     *
     * @param idNumber        学生学号
     * @param homeworkOuterId 作业外层ID
     * @param url             批改结果
     * @return 更新条数
     */
    int updateMarkHomework(@Param("idNumber") String idNumber, @Param("homeworkOuterId") Integer homeworkOuterId, @Param("url") String url);
}

package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.Homework;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
public interface HomeworkService extends BaseServiceTemplate<Homework> {

    /**
     * 根据recordId和用户ID查询提交的作业(老师是看到所有学生, 学生只能看到自己的提交记录)
     *
     * @param homeworkOuterId 作业外层ID
     * @param userId          用户ID
     * @return 作业列表
     */
    List<Homework> getDetailsByHomeworkOuterId(Integer homeworkOuterId, Integer userId);


    /**
     * 课时作业链接
     *
     * @param homeworkOuterId 作业外层ID
     * @return 作业链接列表
     */
    List<String> getHomeworkUrlsByHWOuterID(Integer homeworkOuterId);

    /**
     * 打分
     *
     * @param id      主键ID
     * @param score   分数
     * @param comment 评语
     * @return 更新条数
     */
    int markHomework(Integer id, String score, String comment);

    /**
     * 批改学生作业
     *
     * @param id
     * @param file
     * @return
     */
    int markHomeworkUrl(Integer id, MultipartFile file);

    /**
     * 更新老师批改结果
     *
     * @param file            zip压缩包
     * @param homeworkOuterId 作业外层ID
     * @return 更新条数
     */
    int markHomework(MultipartFile file, Integer homeworkOuterId) throws IOException;
}

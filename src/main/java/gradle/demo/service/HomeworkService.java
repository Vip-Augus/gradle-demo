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
     * @param courseRecordId 课程记录ID
     * @param userId         用户ID
     * @return 作业列表
     */
    List<Homework> getDetailsByCourseRecordId(Integer courseRecordId, Integer userId);


    /**
     * 课时作业链接
     *
     * @param courseRecordId 课时ID
     * @return 作业链接列表
     */
    List<String> getHomeworkUrlsByCRID(Integer courseRecordId);

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
     * 更新老师批改结果
     *
     * @param file           zip压缩包
     * @param courseRecordId 课时ID
     * @return 更新条数
     */
    int markHomework(MultipartFile file, Integer courseRecordId) throws IOException;
}

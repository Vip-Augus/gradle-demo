package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.ExperimentRecord;

import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
public interface ExperimentRecordService extends BaseServiceTemplate<ExperimentRecord> {

    /**
     * 查询
     *
     * @param epId 课ID
     * @return 记录
     */
    List<ExperimentRecord> getListByEPId(Integer epId);

    /**
     * 查询
     *
     * @param classroomId 教室ID
     * @return 记录
     */
    List<ExperimentRecord> getListByClassroomId(Integer classroomId);

    /**
     * 查询
     *
     * @param epId     课程ID
     * @param idNumber 学号
     * @return 记录
     */
    List<ExperimentRecord> getByEPIdAndUser(Integer epId, String idNumber);
}

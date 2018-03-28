package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.CheckInRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 签到DAO
 *
 * @author by JingQ on 2018/3/14
 */
@Mapper
public interface CheckInMapper extends BaseMapperTemplate<CheckInRecord> {

    /**
     * 根据学号获取签到记录
     *
     * @param idNumber        学号
     * @param courseRecordIds 课程记录ID
     * @return 签到记录列表
     */
    List<CheckInRecord> selectByIdNumberAndCourseRecordIds(@Param("idNumber") String idNumber, @Param("courseRecordIds") List<Integer> courseRecordIds);

    /**
     * 根据学号和课程记录ID
     *
     * @param idNumber       学号
     * @param courseRecordId 课程记录ID
     * @return 签到记录
     */
    CheckInRecord selectByIdNumberAndCourseRecord(@Param("idNumber") String idNumber, @Param("courseRecordId") Integer courseRecordId);

    /**
     * 学号搜索，倒序
     *
     * @param idNumber 学号
     * @return 签到记录
     */
    List<CheckInRecord> selectByIdNumber(@Param("idNumber") String idNumber);

    /**
     * 根据课程ID查询
     *
     * @param courseId 课程ID
     * @return 签到记录
     */
    List<CheckInRecord> selectByCourseId(@Param("courseId") Integer courseId);

    /**
     * 根据课时ID查询
     *
     * @param courseRecordId 课时ID
     * @return 签到列表
     */
    List<CheckInRecord> selectByCourseRecordId(@Param("courseRecordId") Integer courseRecordId);
}
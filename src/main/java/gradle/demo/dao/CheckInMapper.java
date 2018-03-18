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
     * @param idNumber    学号
     * @param epRecordIds 课程记录ID
     * @return 签到记录列表
     */
    List<CheckInRecord> selectByIdNumberAndEpRecordIds(@Param("idNumber") String idNumber, @Param("epRecordIds") List<Integer> epRecordIds);

    /**
     * 根据学号和课程记录ID
     *
     * @param idNumber   学号
     * @param epRecordId 课程记录ID
     * @return 签到记录
     */
    CheckInRecord selectByIdNumberAndEpRecord(@Param("idNumber") String idNumber, @Param("epRecordId") Integer epRecordId);

}
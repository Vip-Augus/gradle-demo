package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.College;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author JingQ on 2017/12/24.
 */
@Mapper
public interface CollegeMapper extends BaseMapperTemplate<College> {

    /**
     * 批量查询学院信息
     * @param ids   ID列表
     * @return      学院信息列表
     */
    List<College> selectByIds(@Param("ids") List<Integer> ids);

}

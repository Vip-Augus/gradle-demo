package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.Classroom;
import gradle.demo.model.dto.ClassroomQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 实验室地点
 * Author by JingQ on 2018/1/4
 */
@Mapper
public interface ClassroomMapper extends BaseMapperTemplate<Classroom> {

    /**
     * 根据条件进行查询
     * @param queryParam    查询条件
     * @return              结果
     */
    List<Classroom> query(ClassroomQueryParam queryParam);
}

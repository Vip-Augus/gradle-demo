package gradle.demo.base;

/**
 * 基础的增删查改模板
 *
 * @author JingQ on 2018/02/07.
 */
public interface BaseMapperTemplate<T> {

    T selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(Integer id);

    int insert(T record);

    int updateByPrimaryKey(T record);

}

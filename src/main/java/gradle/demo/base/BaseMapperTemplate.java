package gradle.demo.base;

/**
 * 基础的增删查改模板
 *
 * @author JingQ on 2018/02/07.
 */
public interface BaseMapperTemplate<T> {

    /**
     * 根据主键ID进行查询
     *
     * @param id 主键ID
     * @return T
     */
    T selectByPrimaryKey(Integer id);

    /**
     * 根据主键ID进行删除
     *
     * @param id 主键ID
     * @return 删除数量
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * T 进行插入
     *
     * @param record T
     * @return 插入数量
     */
    int insert(T record);

    /**
     * 更新数据
     *
     * @param record T
     * @return 更新数量
     */
    int updateByPrimaryKey(T record);

}

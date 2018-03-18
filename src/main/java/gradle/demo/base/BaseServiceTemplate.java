package gradle.demo.base;

/**
 * 基础服务模板
 *
 * @author JingQ on 2018/02/07.
 */
public interface BaseServiceTemplate<T> {

    T getById(Integer id);

    int update(T record);

    int deleteById(Integer id);

    T add(T record);

}

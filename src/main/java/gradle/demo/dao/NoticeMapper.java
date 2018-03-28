package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yejingqi
 */
@Mapper
public interface NoticeMapper extends BaseMapperTemplate<Notice> {


    /**
     * 根据位移量获取公告消息列表
     *
     * @param offset 位移量
     * @param limit  数量
     * @return 公告列表
     */
    List<Notice> getNoticeList(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取全部公告的数量
     * @return 全部公告的数量
     */
    int getAllNumber();

}
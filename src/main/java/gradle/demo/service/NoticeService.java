package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.Notice;
import gradle.demo.model.dto.NoticeDTO;

import java.util.List;

/**
 * @author by GJW on 2018/1/7.
 */
public interface NoticeService extends BaseServiceTemplate<Notice> {
    /**
     * 根据位移量获取消息
     *
     * @param offset 位移量
     * @param limit  数量
     * @return 消息列表
     */
    List<Notice> getList(int offset, int limit);


    /**
     * 根据位移量获取消息 --- DTO
     *
     * @param offset 位移量
     * @param limit  数量
     * @return 消息列表
     */
    List<NoticeDTO> getNoticeDTOList(int offset, int limit);

    /**
     * 根据主键ID获取公告详情
     *
     * @param id 主键ID
     * @return 公告
     */
    NoticeDTO getNoticeDTO(Integer id);

    /**
     * 获取总量
     *
     * @return 全部公告的数量
     */
    int countALlNumber();

}

package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.Notice;
import gradle.demo.model.dto.NoticeDTO;

import java.util.List;

/**
 * Created by GJW on 2018/1/7.
 */
public interface NoticeService  extends BaseServiceTemplate<Notice> {
    List<Notice> getList(int offset, int limit);

    List<Notice> getMessageList(int userId, int offset, int limit);

    List<NoticeDTO> getNoticeDTO(int offset, int limit);

    List<NoticeDTO> getMessageNoticeDTO(int userId, int offset, int limit);
}

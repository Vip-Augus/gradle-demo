package gradle.demo.service.impl;

import gradle.demo.dao.NoticeMapper;
import gradle.demo.model.Notice;
import gradle.demo.model.dto.NoticeDTO;
import gradle.demo.service.NoticeService;
import gradle.demo.service.UserService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author by GJW on 2018/1/7.
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    NoticeMapper noticeMapper;

    @Autowired
    UserService userService;

    @Override
    public Notice getById(Integer id) {
        return noticeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Notice record) {
        return noticeMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return noticeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Notice add(Notice record) {
        noticeMapper.insert(record);
        return record;
    }

    @Override
    public List<Notice> getList(int offset, int limit) {
        List<Notice> notices = noticeMapper.getNoticeList(offset, limit);
        if (CollectionUtils.isEmpty(notices)) {
            return Collections.emptyList();
        }
        return notices;
    }


    @Override
    public List<NoticeDTO> getNoticeDTOList(int offset, int limit) {
        List<Notice> notices = getList(offset, limit);
        if (CollectionUtils.isEmpty(notices)) {
            return Collections.emptyList();
        }
        List<NoticeDTO> dtos = Lists.newArrayList();
        fillMessageNoticeDTO(notices, dtos);
        return dtos;
    }

    @Override
    public NoticeDTO getNoticeDTO(Integer id) {
        return notice2DTO(getById(id));
    }

    @Override
    public int countALlNumber() {
        return noticeMapper.getAllNumber();
    }


    private void fillMessageNoticeDTO(List<Notice> notices, List<NoticeDTO> dtos) {
        for (Notice notice : notices) {
            dtos.add(notice2DTO(notice));
        }
    }

    private NoticeDTO notice2DTO(Notice notice) {
        NoticeDTO dto = new NoticeDTO();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setContent(notice.getContent());
        dto.setCreateTime(notice.getCreateTime());
        dto.setCreateUserName(userService.getById(notice.getCreateId()).getName());
        dto.setCreateId(notice.getCreateId());
        return dto;
    }
}

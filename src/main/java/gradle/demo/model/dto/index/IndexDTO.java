package gradle.demo.model.dto.index;

import gradle.demo.model.Note;
import gradle.demo.model.dto.NoticeDTO;
import lombok.Data;

import java.util.List;

/**
 * @author guojiawei
 * @date 2018/1/13
 */
@Data
public class IndexDTO {
    protected List<NoticeDTO> noticeList;
    protected List<Note> noteList;
}

package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.Note;

import java.util.List;

/**
 * @author guojiawei
 * @date 2018/1/13
 */
public interface NoteService extends BaseServiceTemplate<Note> {
    List<Note> getPlanList(int userId);
}

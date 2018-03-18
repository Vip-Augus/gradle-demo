package gradle.demo.service.impl;

import gradle.demo.dao.NoteMapper;
import gradle.demo.model.Note;
import gradle.demo.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author guojiawei
 * @date 2018/1/13
 */
@Service
public class NoteServiceImpl implements NoteService{
    @Autowired
    NoteMapper noteMapper;

    @Override
    public Note getById(Integer id) {
        return noteMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Note record) {
        return noteMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteById(Integer id) {
        return noteMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Note add(Note record) {
        noteMapper.insertSelective(record);
        return record;
    }

    @Override
    public List<Note> getPlanList(int userId) {
        return noteMapper.getPlanList(userId);
    }
}

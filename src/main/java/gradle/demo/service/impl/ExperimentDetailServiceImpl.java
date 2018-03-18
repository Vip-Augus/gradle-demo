package gradle.demo.service.impl;

import gradle.demo.dao.ExperimentDetailMapper;
import gradle.demo.model.ExperimentDetail;
import gradle.demo.service.ExperimentDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author JingQ on 2017/12/24.
 */
@Service
public class ExperimentDetailServiceImpl implements ExperimentDetailService {

    @Autowired
    private ExperimentDetailMapper experimentDetailMapper;

    @Override
    public ExperimentDetail getById(Integer id) {
        return experimentDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(ExperimentDetail record) {
        return experimentDetailMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return experimentDetailMapper.deleteByPrimaryKey(id);
    }

    @Override
    public ExperimentDetail add(ExperimentDetail record) {
        experimentDetailMapper.insert(record);
        return record;
    }

    @Override
    public List<ExperimentDetail> getDetailsByEPRecordId(Integer epRecordId, Integer userId) {
        if (userId == null) {
            return experimentDetailMapper.selectByEPRecordId(epRecordId);
        }
        return experimentDetailMapper.selectByEPRecordAndUserId(epRecordId, userId);
    }
}

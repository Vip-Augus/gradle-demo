package gradle.demo.service.impl;

import gradle.demo.dao.CourseUserMapper;
import gradle.demo.model.CourseUser;
import gradle.demo.service.CourseUserService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author JingQ on 2017/12/24.
 */
@Service
public class CourseUserServiceImpl implements CourseUserService {

    @Autowired
    private CourseUserMapper courseUserMapper;

    @Override
    public CourseUser getById(Integer id) {
        return courseUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(CourseUser record) {
        return courseUserMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return courseUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CourseUser add(CourseUser record) {
        courseUserMapper.insert(record);
        return record;
    }

    @Override
    public List<Integer> getCIDsByUserID(Integer userId) {
        List<CourseUser> epUsers = courseUserMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(epUsers)) {
            return Lists.newArrayList();
        }
        return Lists.transform(epUsers, new Function<CourseUser, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable CourseUser courseUser) {
                return courseUser.getCourseId();
            }
        });
    }

    @Override
    public int batchAdd(Integer courseId, List<Integer> userIds) {
        //暂时不用
        if (CollectionUtils.isEmpty(userIds)) {
            return 0;
        }
        return 0;
    }

    @Override
    public List<Integer> getUserIdsByCId(Integer courseId) {
        List<Integer> userIds = courseUserMapper.selectUserIdsByCId(courseId);
        if(CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return userIds;
    }

    @Override
    public List<CourseUser> getByCourseId(Integer courseId) {
        List<CourseUser> result = courseUserMapper.selectByCourseId(courseId);
        if (CollectionUtils.isEmpty(result)) {
            return Lists.newArrayList();
        }
        return result;
    }

    @Override
    public int getCourseCountByCId(Integer courseId) {
        return courseUserMapper.selectCountByCId(courseId);
    }
}

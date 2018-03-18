package gradle.demo.service.impl;

import gradle.demo.dao.UserMapper;
import gradle.demo.model.User;
import gradle.demo.model.UserPage;
import gradle.demo.service.UserService;
import gradle.demo.util.MD5Util;
import gradle.demo.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Author JingQ on 2017/12/24.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(User record) {
        return userMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteById(Integer id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public User add(User record) {
        //接收到用户自己设定的密码后，插入数据中，进行加密
        //加密算法password = md5(md5(password) + salt))
        String salt = UUID.randomUUID().toString();
        String password = MD5Util.genMD5(MD5Util.genMD5(record.getPassword()) + salt);
        record.setSalt(salt);
        record.setPassword(password);
        userMapper.insert(record);
        return record;
    }

    @Override
    public boolean checkPassword(User userParam, User user) {
        if (Objects.equals(user, null) || Objects.equals(userParam, null)) {
            return false;
        }
        String passwordCheck = MD5Util.genMD5(MD5Util.genMD5(userParam.getPassword()) + user.getSalt());
        return StringUtil.isEquals(passwordCheck, user.getPassword());
    }

    @Override
    public User getByIdNumber(String idNumber, Integer type) {
        return userMapper.selectByIdNumber(idNumber, type);
    }

    @Override
    public List<User> getByIdNumbers(List<String> idNumbers, Integer type) {
        return userMapper.selectByIdNumbers(idNumbers, type);
    }

    @Override
    public List<User> getByType(Integer type) {
        return userMapper.selectByType(type);
    }

    @Override
    public List<User> getTeachers(int offset, int limit, String name) {
        List<User> userList = userMapper.selectTeachers(offset, limit, name);
        if(userList == null) {
            return Collections.emptyList();
        }
        return userList;
    }

    @Override
    public boolean deleteByIdNumber(String idNumber) {
        boolean b =  userMapper.deleteByIdNumber(idNumber);
        System.out.println(b);
        return b;
    }

    @Override
    public boolean updateUserAuth(Integer userId, String auth, Integer type) {
        return userMapper.updateAuth(userId, auth, type);
    }

    @Override
    public int importUser(List<User> list) {
        return userMapper.batchInsert(list);
    }

    @Override
    public UserPage getByPage(Integer type, Integer page, Integer pageSize) {
        UserPage userPage = new UserPage();
        userPage.setTotalCount(userMapper.selectAllCountByType(type));
        userPage.setList(userMapper.selectByPage(type, page, pageSize));
        return userPage;
    }
}

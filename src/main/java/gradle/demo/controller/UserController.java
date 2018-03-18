package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.User;
import gradle.demo.model.dto.UserDTO;
import gradle.demo.model.dto.UserPageDTO;
import gradle.demo.service.ExperimentUserService;
import gradle.demo.service.UserService;
import gradle.demo.util.CodeConstants;
import gradle.demo.util.ImportUtil;
import gradle.demo.util.MD5Util;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.StringUtil;
import gradle.demo.util.convert.UserConverter;
import gradle.demo.util.result.ApiResponse;
import gradle.demo.util.result.Message;
import gradle.demo.util.result.SingleResult;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 用户控制器
 * @author  by JingQ on 2018/1/1
 */
@Slf4j
@RestController
@RequestMapping(value = "/web/user")
@Api(value = "用户管理", tags = "Controller")
public class UserController {

    private static final int PAGE_SIZE = 10;
    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private ExperimentUserService experimentUserServiceImpl;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "用户登录", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "idNumber", name = "学号/工号", required = true, paramType = "String"),
            @ApiImplicitParam(value = "password", name = "密码", required = true, paramType = "String"),
            @ApiImplicitParam(value = "type", name = "用户类型", required = true, paramType = "Integer")
    })
    public JSON login(@RequestBody User userParam, HttpServletRequest request) {
        SingleResult<UserDTO> result = new SingleResult<>();
        User user = userServiceImpl.getByIdNumber(userParam.getIdNumber(), userParam.getType());
        //校验用户信息
        if (user == null || !userServiceImpl.checkPassword(userParam, user)) {
            result.returnError(CodeConstants.INVALID_USER_INFO);
            log.error(CodeConstants.INVALID_USER_INFO, userParam.getIdNumber());
            return (JSON) JSON.toJSON(result);
        }
        HttpSession session = request.getSession();
        //用户成功登录后，将数据存在session中，将密码清除掉
        UserDTO userDTO = userConverter.user2DTO(user);
        session.setAttribute(CodeConstants.USER_INFO_CONSTANT, user);
        result.returnSuccess(userDTO);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "registry", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "用户后门注册", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "idNumber", name = "学号/工号", required = true, paramType = "String"),
            @ApiImplicitParam(value = "password", name = "密码", required = true, paramType = "String"),
            @ApiImplicitParam(value = "type", name = "用户类型", required = true, paramType = "Integer")
    })
    public JSON registry(@RequestBody User userParam, HttpServletRequest request) {
        SingleResult<UserDTO> result = new SingleResult<>();
        User user = userServiceImpl.getByIdNumber(userParam.getIdNumber(), userParam.getType());
        if (user != null) {
            result.returnError(CodeConstants.USER_IS_EXISTS);
            return (JSON) JSON.toJSON(result);
        }
        try {
            userServiceImpl.add(userParam);
        } catch (Exception e) {
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(userConverter.user2DTO(userParam));
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取登录用户信息", tags = "1.0.0")
    public ApiResponse<User> getUser(HttpServletRequest request) {
        User user;
        try {
            user = SessionUtil.getUser(request.getSession());
        } catch (Exception e) {
            return ApiResponse.error(new Message("11111111", "获取用户错误"));
        }
        return ApiResponse.success(user);
    }


    @RequestMapping(value = "teacher", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取教室和管理员信息列表", tags = "1.0.0")
    public JSON getUserByType(HttpServletRequest request) {
        List<UserDTO> userList = Lists.newArrayList();
        SingleResult<List<UserDTO>> result = new SingleResult<>();
        try {
            int page = StringUtil.getInteger(request.getParameter("page"));
            String name = request.getParameter("name");
            userList = userConverter.users2DTOS(userServiceImpl.getTeachers(page * PAGE_SIZE, PAGE_SIZE, name));
        } catch (Exception e) {
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(userList);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据用户类型获取用户列表", tags = "1.0.0")
    public JSON getUsers(
            @ApiParam(name = "type", value = "用户类型", type = "Integer", required = true) @RequestParam("type") Integer type, @RequestParam("page") Integer page) {
        SingleResult<UserPageDTO> result = new SingleResult<>();
        try {
            //前端大老说前端进行分页
            result.returnSuccess(userConverter.userPage2DTO(userServiceImpl.getByPage(type, 0, 0)));
        } catch (Exception e) {
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        return (JSON) JSON.toJSON(result);
    }

    //根据idNumber删除User
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除用户", tags = "1.0.0")
    public JSON deleteByIdNumber(
            @ApiParam(name = "idNumber", value = "学号或者工号", required = true, type = "String") @RequestParam("idNumber") String idNumber, HttpServletRequest request) {
        SingleResult<List<User>> result = new SingleResult<>();
        try {
            Boolean deleteBoolean = userServiceImpl.deleteByIdNumber(idNumber);
            if (!deleteBoolean) {
                result.returnError(CodeConstants.NO_DATA);
                return (JSON) JSON.toJSON(result);
            }
        } catch (Exception e) {
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(null);
        return (JSON) JSON.toJSON(result);
    }

    //根据idNumber更新UserAuthority
    @RequestMapping(value = "auth", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "更新用户权限", tags = "1.0.0")
    public JSON updateUserAuth(
            @ApiParam(name = "userId", value = "用户ID", type = "Integer", required = true) @RequestParam("userId") Integer userId,
            @ApiParam(name = "auth", value = "用户ID", type = "String", required = true) @RequestParam("auth") String auth, HttpServletRequest request) {
        SingleResult<List<User>> result = new SingleResult<>();
        try {
            Integer authority = Integer.valueOf(auth, 2);
            Integer type = 1;
//            if ((authority & 1) == 1) {
//                type = 0;
//            }
            Boolean updateBoolean = userServiceImpl.updateUserAuth(userId, Integer.toHexString(authority), type);
            if (!updateBoolean) {
                result.returnError("更新失败");
                return (JSON) JSON.toJSON(result);
            }
        } catch (Exception e) {
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(null);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "password", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "确认密码", tags = "1.0.0")
    public JSON passwordConfirm(@RequestBody User userParam, HttpServletRequest request) {
        SingleResult<UserDTO> result = new SingleResult<>();
        User user = userServiceImpl.getByIdNumber(userParam.getIdNumber(), userParam.getType());
        //校验用户信息
        if (user == null || !userServiceImpl.checkPassword(userParam, user)) {
            result.returnError(CodeConstants.INVALID_USER_INFO);
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(null);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "importUser", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "导入用户", tags = "1.0.0")
    public JSON importStudent(
            @ApiParam(name = "userType", value = "用户类型", type = "Integer", required = true) @RequestParam("userType") Integer userType,
            @ApiParam(name = "file", value = "文件", type = "File", required = true) @RequestParam("file") MultipartFile file) {
        SingleResult<Integer> result = new SingleResult<>();
        List<User> users = filterUser(userType, file);
        try {
            result.returnSuccess(userServiceImpl.importUser(users));
        } catch (Exception e) {
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        return (JSON) JSON.toJSON(result);
    }

    private List<User> filterUser(Integer userType, MultipartFile file) {
        List<String> contentList = Lists.newArrayList();
        try {
            if (file.isEmpty()) {
                return null;
            }
            contentList = ImportUtil.exportListFromExcel(file.getInputStream(), 0);
            Iterator<String> iterator = contentList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals("|null|null|null|null|null|null|null|null|null|null|null|null|null|null")) {
                    iterator.remove();
                }
            }
        } catch (Exception e) {
        }
        //去掉第一行
        contentList.remove(0);
        if (CollectionUtils.isEmpty(contentList)) {
            return null;
        }
        String initPassword = "123456";
        List<User> users = Lists.newArrayList();
        for (String tmp : contentList) {
            List<String> content = Lists.newArrayList(tmp.split("\\|"));
            //学号,专业,姓名,班级,入学时间,籍贯,民族,出生日期,性别,政治面貌,手机号码,邮箱,家庭住址,简介
            User user = new User();
            user.setType(userType);
            user.setIdNumber(content.get(1));
            String salt = UUID.randomUUID().toString();
            String password = MD5Util.genMD5(MD5Util.genMD5(initPassword) + salt);
            user.setSalt(salt);
            user.setPassword(password);
            user.setProfession(content.get(2));
            user.setName(content.get(3));
            user.setClassTitle(content.get(4));
            user.setJoinTime(content.get(5));
            user.setNativePlace(content.get(6));
            user.setEthnic(content.get(7));
            user.setBirthday(content.get(8));
            user.setGender(Integer.parseInt(content.get(9)));
            user.setPolitical(content.get(10));
            user.setPhone(content.get(11));
            user.setEmail(content.get(12));
            user.setAddress(content.get(13));
            user.setIntroduction(content.get(14));
            users.add(user);
        }
        return users;
    }
}

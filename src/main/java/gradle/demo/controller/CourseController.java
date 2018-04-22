package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.Course;
import gradle.demo.model.CourseUser;
import gradle.demo.model.User;
import gradle.demo.model.dto.CourseDTO;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.ClassroomService;
import gradle.demo.service.CourseService;
import gradle.demo.service.CourseUserService;
import gradle.demo.service.UserService;
import gradle.demo.util.ImportUtil;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.convert.CourseConverter;
import gradle.demo.util.result.ApiResponse;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.Message;
import gradle.demo.util.result.SingleResult;
import com.google.common.base.Function;
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

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

/**
 * 课程（最外层）
 *
 * @author by JingQ on 2018/1/2
 */
@Slf4j
@RestController
@RequestMapping("/class/course")
@Api(value = "课程管理", tags = "Controller")
public class CourseController {

    private static final String SPLIT_SIGNAL = ",";

    /**
     * 课程服务
     */
    @Autowired
    private CourseService courseServiceImpl;

    /**
     * course转换器
     */
    @Autowired
    private CourseConverter courseConverter;

    /**
     * 课程和用户映射服务
     */
    @Autowired
    private CourseUserService courseUserServiceImpl;

    /**
     * 用户服务
     */
    @Autowired
    private UserService userServiceImpl;


    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加课程", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "课程名称", paramType = "String", required = true),
            @ApiImplicitParam(name = "briefIntroduction", value = "课程简介", paramType = "String", required = true),
            @ApiImplicitParam(name = "classroomId", value = "教室ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "beginPeriod", value = "开始时间", paramType = "String", allowableValues = "起始学期:yyyy"),
            @ApiImplicitParam(name = "endPeriod", value = "结束时间", paramType = "String", allowableValues = "结束学期：yyyy"),
            @ApiImplicitParam(name = "term", value = "学期", paramType = "Integer", allowableValues = "1: 上学期/ 2:下学期"),
            @ApiImplicitParam(name = "lesson", value = "课时" ,paramType = "Integer"),
            @ApiImplicitParam(name = "buildingNumber", value = "教学楼号码", paramType = "Integer"),
            @ApiImplicitParam(name = "classroom", value = "教室门牌号", paramType = "String"),
            @ApiImplicitParam(name = "day", value = "周几", paramType = "Integer", allowableValues = "1-7"),
            @ApiImplicitParam(name = "classBegin", value = "第几节开始", paramType = "Integer", allowableValues = "1-12"),
            @ApiImplicitParam(name = "classEnd", value = "第几节结束", paramType = "Integer", allowableValues = "1-12"),
    })
    public ApiResponse add(@RequestBody Course course, HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        //插入成功后，将老师id与课程绑定
        try {
            course.setTIds(user.getId().toString());
            course = courseServiceImpl.add(course);
        } catch (Exception e) {
            log.error("创建课程失败", e);
            return ApiResponse.error(new Message("CO000001", "创建课程失败"));
        }
        log.info("课程创建成功: ", course.getId());
        return ApiResponse.success(course);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取课程列表", tags = "1.0.0")
    public ApiResponse elective(HttpServletRequest request) {
        ListResult<CourseDTO> result = new ListResult<>();
        User user= SessionUtil.getUser(request.getSession());
        try {
            List<Integer> courseIds = courseUserServiceImpl.getCIDsByUserID(user.getId());
            if (!CollectionUtils.isEmpty(courseIds)) {
                List<Course> courseList = courseServiceImpl.getByIds(courseIds);
                return ApiResponse.success(courseConverter.courseList2DTOList(courseList));
            }
        } catch (Exception e) {
            log.error("查询课程失败: ", e);
            result.returnError(e.getMessage());
        }
        return ApiResponse.success(Lists.newArrayList());
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除课程信息", tags = "1.0.0")
    public ApiResponse deleteCourse(
            @ApiParam(name = "id", value = "课程ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<Integer> result = new SingleResult<>();
        try {
            courseUserServiceImpl.deleteById(id);
        } catch (Exception e) {
            log.error("删除课程失败: ", e);
            result.returnError(e.getMessage());
            return ApiResponse.error(new Message("CO000002", "删除课程失败"));
        }
        result.returnSuccess(id);
        return ApiResponse.success();
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新课程信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "name", value = "课程名称", paramType = "String", required = true),
            @ApiImplicitParam(name = "briefIntroduction", value = "课程简介", paramType = "String", required = true),
            @ApiImplicitParam(name = "classroomId", value = "教室ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "beginPeriod", value = "开始时间", paramType = "String", allowableValues = "起始学期:yyyy"),
            @ApiImplicitParam(name = "endPeriod", value = "结束时间", paramType = "String", allowableValues = "结束学期：yyyy"),
            @ApiImplicitParam(name = "term", value = "学期", paramType = "Integer", allowableValues = "1: 上学期/ 2:下学期"),
            @ApiImplicitParam(name = "lesson", value = "课时" ,paramType = "Integer"),
            @ApiImplicitParam(name = "buildingNumber", value = "教学楼号码", paramType = "Integer"),
            @ApiImplicitParam(name = "classroom", value = "教室门牌号", paramType = "String"),
            @ApiImplicitParam(name = "day", value = "周几", paramType = "Integer", allowableValues = "1-7"),
            @ApiImplicitParam(name = "classBegin", value = "第几节开始", paramType = "Integer", allowableValues = "1-12"),
            @ApiImplicitParam(name = "classEnd", value = "第几节结束", paramType = "Integer", allowableValues = "1-12"),
    })
    public ApiResponse updateCourse (@RequestBody Course course, HttpServletRequest request) {
        SingleResult<Course> result = new SingleResult<>();
        try {
            courseServiceImpl.update(course);
            log.info("课程信息更新成功:", course);
            result.returnSuccess(course);
        } catch (Exception e) {
            log.error("课程信息更新失败:", e);
            return ApiResponse.error(new Message("CO000003", "课程信息更新失败"));
        }
        return ApiResponse.success();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取单个课程的信息", tags = "1.0.0")
    public ApiResponse getRecord(
            @ApiParam(name = "id", value = "课程ID", required = true, type = "Integer") @RequestParam("id") Integer id, HttpServletRequest request) {
        Course record = courseServiceImpl.getById(id);
        return ApiResponse.success(record);
    }

    @RequestMapping(value = "byCode", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取单个课程的信息", tags = "1.0.0")
    public ApiResponse getRecordByCode(
            @ApiParam(name = "code", value = "课程识别码", required = true, type = "String") @RequestParam("code") String code, HttpServletRequest request) {
        Course record = courseServiceImpl.getByCode(code);
        return ApiResponse.success(record);
    }

    @RequestMapping(value = "/join", method = RequestMethod.POST)
    @ApiOperation(value = "加入课程--绑定用户和课程的关系", tags = "1.0.0")
    public ApiResponse joinCourse(
            @ApiParam(name = "courseId", value = "课程ID", type = "Integer", required = true) @RequestParam("courseId") Integer courseId,
            HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        CourseUser cs = new CourseUser();
        cs.setUserType(user.getType());
        cs.setUserId(user.getId());
        cs.setIdNumber(user.getIdNumber());
        cs.setCourseId(courseId);
        cs.setUserName(user.getName());
        courseUserServiceImpl.add(cs);
        return ApiResponse.success();
    }

    @RequestMapping(value = "importStudent", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "导入课程与学生关联关系", tags = "1.0.0")
    public JSON importStudent(
            @ApiParam(name = "file", value = "文件", required = true, type = "File") @RequestParam("file") MultipartFile file,
            @ApiParam(name = "courseId", value = "课程ID", required = true, type = "Integer") @RequestParam("courseId") Integer epId, HttpServletRequest request) {
        SingleResult<Integer> result = new SingleResult<>();
        List<String> fileUserIds = filterUserIds(file);
        List<User> existUsers = userServiceImpl.getByIdNumbers(fileUserIds, UserType.STUDENT.getCode());
        List<Integer> existUserIds = Lists.transform(existUsers, new Function<User, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable User user) {
                return user.getId();
            }
        });
//        result.returnSuccess(courseUserServiceImpl.batchAdd(epId, existUserIds));
        return (JSON) JSON.toJSON(result);
    }

    private List<String> filterUserIds(MultipartFile file) {
        List<String> contentList = Lists.newArrayList();
        try {
            if (file.isEmpty()) {
                return null;
            }
            contentList = ImportUtil.exportListFromExcel(file.getInputStream(), 0);
            Iterator<String> iterator = contentList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals("|null|null|null|null|null|null")) {
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
        List<String> userIds = Lists.newArrayList();
        for (String tmp : contentList) {
            userIds.add(Lists.newArrayList(tmp.split("\\|")).get(1));
        }
        return userIds;
    }
}

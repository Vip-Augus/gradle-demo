package gradle.demo.controller;

import com.google.common.collect.Lists;
import gradle.demo.model.CourseMessage;
import gradle.demo.model.User;
import gradle.demo.model.dto.CourseMessageQueryParam;
import gradle.demo.service.CourseMessageService;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.result.ApiResponse;
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
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author by JingQ on 2018/3/27
 */
@Slf4j
@RestController
@RequestMapping("/class/courseMessage")
@Api(value = "课程留言板控制器", tags = "Controller")
public class CourseMessageController {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private CourseMessageService courseMessageServiceImpl;


    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ApiOperation(value = "新增留言", tags = "1.1.0")
    public ApiResponse publish(
            @ApiParam(name = "content", value = "内容", type = "String", required = true) @RequestParam("content") String content,
            @ApiParam(name = "courseId", value = "课程ID", type = "Integer", required = true) @RequestParam("courseId") Integer courseId,
            HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        CourseMessage courseMessage = convertMessage(user, courseId, content);
        courseMessageServiceImpl.add(courseMessage);
        return ApiResponse.success();
    }

    @RequestMapping(value = "listAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取课程留言", tags = "1.1.0")
    public ApiResponse getAllByCourseId(
            @ApiParam(name = "courseId", value = "课程ID", type = "Integer", required = true) @RequestParam("courseId") Integer courseId) {
        List<CourseMessage> messages = courseMessageServiceImpl.getByCourseId(courseId);
        if (CollectionUtils.isEmpty(messages)) {
            messages = Lists.newArrayList();
        }
        return ApiResponse.success(messages);
    }


    @RequestMapping(value = "search", method = RequestMethod.GET)
    @ApiOperation(value = "获取课程留言", tags = "1.1.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "courseId", value = "课程ID", required = true, paramType = "Integer"),
            @ApiImplicitParam(name = "pageNo", value = "页码", required = true, paramType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", required = true, paramType = "Integer"),
    })
    public ApiResponse getAllByParam(@RequestBody CourseMessageQueryParam param) {
        List<CourseMessage> messages = courseMessageServiceImpl.getByParam(param);
        if (CollectionUtils.isEmpty(messages)) {
            messages = Lists.newArrayList();
        }
        return ApiResponse.success(messages);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ApiOperation(value = "删除留言", tags = "1.1.0")
    public ApiResponse delete(
            @ApiParam(name = "id", value = "留言ID", type = "Integer", required = true) @RequestParam("id") Integer id
    ) {
        int count = courseMessageServiceImpl.deleteById(id);
        return ApiResponse.success(count);
    }

    private CourseMessage convertMessage(User user, Integer courseId, String content) {
        CourseMessage message = new CourseMessage();
        message.setContent(content);
        message.setCourseId(courseId);
        message.setPublishTime(PeriodUtil.format(System.currentTimeMillis(), DATE_PATTERN));
        message.setUserId(user.getId());
        message.setUserName(user.getName());
        message.setIdNumber(user.getIdNumber());
        return message;
    }
}

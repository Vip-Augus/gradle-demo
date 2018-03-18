package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.Course;
import gradle.demo.model.User;
import gradle.demo.service.CourseService;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.SingleResult;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author  by GJW on 2018/1/7.
 */
@RestController
@RequestMapping(value = "/web/course")
@Api(value = "学科管理", tags = "Controller")
public class CourseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    CourseService courseService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "查询学科列表", tags = "1.0.0")
    public JSON getCourseList() {
        ListResult<Course> listResult = new ListResult<>();
        List<Course> courses = Lists.newArrayList();
        try {
            courses = courseService.getList();
            listResult.returnSuccess(courses);
        } catch (Exception e) {
            LOGGER.error("查询学科列表失败", e);
            listResult.returnError("查询学科列表失败");
        }
        return (JSON) JSON.toJSON(listResult);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取学科详情", tags = "1.0.0")
    public JSON getCourseDetail(
            @ApiParam(name = "id", value = "学科Id", required = true, type = "Integer") @RequestParam("id") Integer id) {
        SingleResult<Course> result = new SingleResult<>();

        try {
            Course course = courseService.getById(id);
            result.returnSuccess(course);
        } catch (Exception e) {
            LOGGER.error("查询学科失败", e);
            result.returnError("查询学科失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "修改学科信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "学科Id", required = true, paramType = "Integer"),
            @ApiImplicitParam(name = "name", value = "学科名字", required = true, paramType = "String"),
            @ApiImplicitParam(name = "brief", value = "学科简介", required = true, paramType = "String"),
            @ApiImplicitParam(name = "period", value = "课时", required = true, paramType = "String"),
            @ApiImplicitParam(name = "test", value = "考试", required = true, paramType = "Integer", allowableValues = "0: 不需要/ 1: 需要")
    })
    public JSON updateCourseDetail(@RequestBody Course course) {
        SingleResult<Course> result = new SingleResult<>();
        try {
            course.setModifyDate(new Date());
            courseService.update(course);
            result.returnSuccess(course);
        } catch (Exception e) {
            LOGGER.error("修改学科失败", e);
            result.returnError("修改学科失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "新增学科信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "学科名字", required = true, paramType = "String"),
            @ApiImplicitParam(name = "brief", value = "学科简介", required = true, paramType = "String"),
            @ApiImplicitParam(name = "period", value = "课时", required = true, paramType = "String"),
            @ApiImplicitParam(name = "test", value = "考试", required = true, paramType = "Integer", allowableValues = "0: 不需要/ 1: 需要")
    })
    public JSON addCourseDetail(@RequestBody Course course, HttpServletRequest request) {
        SingleResult<Course> result = new SingleResult<>();
        try {
            User user = SessionUtil.getUser(request.getSession());
            course.setCreateId(user.getId());
            course.setCreateDate(new Date());
            course.setModifyDate(new Date());

            courseService.add(course);
            result.returnSuccess(course);
        } catch (Exception e) {
            LOGGER.error("添加学科失败", e);
            result.returnError("添加学科失败");
        }
        return (JSON) JSON.toJSON(result);
    }
}

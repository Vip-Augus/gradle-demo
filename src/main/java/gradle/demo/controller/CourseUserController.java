package gradle.demo.controller;

import gradle.demo.model.CourseUser;
import gradle.demo.service.CourseUserService;
import gradle.demo.util.result.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author by JingQ on 2018/4/22
 */
@RestController
@Api(value = "课程成员管理器", tags = "Controller")
@RequestMapping("/class/courseUser")
public class CourseUserController {

    @Autowired
    private CourseUserService courseUserServiceImpl;


    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ApiOperation(value = "查询该课程的成员", tags = "1.1.0")
    public ApiResponse getByCourseId(
            @ApiParam(name = "courseId", value = "课程ID", type = "Integer") @RequestParam("courseId") Integer courseId) {
        List<CourseUser> courseUsers = courseUserServiceImpl.getByCourseId(courseId);
        return ApiResponse.success(courseUsers);
    }
}

package gradle.demo.controller;

import gradle.demo.model.HomeworkOuter;
import gradle.demo.model.User;
import gradle.demo.service.HomeworkOuterService;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.result.ApiResponse;
import gradle.demo.util.result.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author by JingQ on 2018/4/20
 */
@Slf4j
@RestController
@RequestMapping("/class/homeworkOuter")
@Api(value = "作业外层，记录次数", tags = "controller")
public class HomeworkOuterController {

    @Autowired
    private HomeworkOuterService homeworkOuterServiceImpl;


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加一次作（外层）", tags = "1.1.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "homeworkName", value = "作业名称", paramType = "String", required = true),
            @ApiImplicitParam(name = "homeworkContent", value = "作业简介", paramType = "String", required = true),
            @ApiImplicitParam(name = "courseId", value = "课程ID", paramType = "Integer", required = true),
    })
    public ApiResponse addHWOuter(@RequestBody HomeworkOuter homeworkOuter, HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        try {
            homeworkOuter.setCreateId(user.getId());
            homeworkOuterServiceImpl.add(homeworkOuter);
        } catch (Exception ex) {
            log.error("添加作业记录失败", ex);
            return ApiResponse.error(new Message("ZY000003", "添加作业失败"));
        }
        return ApiResponse.success(homeworkOuter);
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "获得课程布置的作业（外层）", tags = "1.1.0")
    public ApiResponse hwOuterList(
            @ApiParam(name = "courseId", value = "课程ID", type = "Integer", required = true) @RequestParam("courseId") Integer courseId) {
        List<HomeworkOuter> homeworkOuters = homeworkOuterServiceImpl.getByCourseId(courseId);
        return ApiResponse.success(homeworkOuters);
    }
}

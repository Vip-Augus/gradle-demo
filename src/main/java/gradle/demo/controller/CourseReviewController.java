package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.Course;
import gradle.demo.model.CourseReview;
import gradle.demo.model.User;
import gradle.demo.model.enums.ReviewType;
import gradle.demo.service.CourseReviewService;
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
@RequestMapping(value = "/web/course/review")
@Api(value = "学科审批", tags = "Controller")
public class CourseReviewController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReviewController.class);

    @Autowired
    CourseService courseService;

    @Autowired
    CourseReviewService courseReviewService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取学科Review列表", tags = "1.0.0")
    public JSON getCourseReviewList() {
        ListResult<CourseReview> listResult = new ListResult<>();
        List<CourseReview> reviews = Lists.newArrayList();
        try {
            reviews = courseReviewService.getListByState(ReviewType.UNREVIEW.getCode());
            listResult.returnSuccess(reviews);
        }catch (Exception e) {
            LOGGER.error("查询待审核学科列表失败", e);
            listResult.returnError("查询待审核学科列表失败");
        }
        return (JSON) JSON.toJSON(listResult);
    }

    @RequestMapping(value = "/state", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更改学科审批状态", tags = "1.0.0")
    public JSON updateCourseReviewState(
            @ApiParam(name = "id", value = "学科审批ID", type = "Integer", required = true) @RequestParam("id") Integer id, 
            @ApiParam(name = "state", value = "审批状态")@RequestParam("state") Integer state) {
        SingleResult<Course> result = new SingleResult<>();
        try {
            if(ReviewType.ACCEPT.getCode().equals(state) || ReviewType.REFUSE.getCode().equals(state)) {
                courseReviewService.updateState(id, state);
                if(ReviewType.ACCEPT.getCode().equals(state)) {
                    Course course = getCourseFromReview(id);
                    courseService.add(course);
                    result.returnSuccess(course);
                } else {
                    result.returnSuccess(null);
                }
            } else {
                result.returnError("参数非法");
            }
        }catch (Exception e) {
            LOGGER.error("更新审核学科状态失败", e);
            result.returnError("更新审核学科状态失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "学科审批信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "学科名字", required = true, paramType = "String"),
            @ApiImplicitParam(name = "brief", value = "学科简介", required = true, paramType = "String"),
            @ApiImplicitParam(name = "period", value = "课时", required = true, paramType = "String"),
            @ApiImplicitParam(name = "test", value = "考试", required = true, paramType = "Integer", allowableValues = "0: 不需要/ 1: 需要")
    })
    public JSON addCourseReview(@RequestBody CourseReview courseReview, HttpServletRequest request) {
        SingleResult<CourseReview> result = new SingleResult<>();
        try {
            User user = SessionUtil.getUser(request.getSession());
            courseReview.setCreateId(user.getId());
            courseReview.setApplicant(user.getName());
            courseReview.setCreateDate(new Date());
            courseReview.setState(0);
            CourseReview review = courseReviewService.add(courseReview);
            result.returnSuccess(review);
        }catch (Exception e) {
            LOGGER.error("申请创建学科失败", e);
            result.returnError("申请创建学科失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取学科审批信息", tags = "1.0.0")
    public JSON getCourseReviewDetail(
            @ApiParam(name = "id", value = "学科审批ID", type = "Integer", required = true) @RequestParam("id") Integer id) {
        SingleResult<CourseReview> result = new SingleResult<>();

        try {
            CourseReview review = courseReviewService.getById(id);
            result.returnSuccess(review);
        }catch (Exception e) {
            LOGGER.error("查询待审核学科失败", e);
            result.returnError("查询待审核学科失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    private Course getCourseFromReview(int id) {
        CourseReview review = courseReviewService.getById(id);
        Course course = new Course();
        course.setName(review.getName());
        course.setBrief(review.getBrief());
        course.setCreateDate(new Date());
        course.setCreateId(review.getCreateId());
        course.setModifyDate(new Date());
        course.setPeriod(review.getPeriod());
        course.setTest(review.getTest());
        return course;
    }
}

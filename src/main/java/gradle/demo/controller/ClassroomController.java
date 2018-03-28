package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.Classroom;
import gradle.demo.model.Course;
import gradle.demo.model.dto.ClassroomQueryParam;
import gradle.demo.service.ClassroomService;
import gradle.demo.service.CourseRecordService;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.SingleResult;
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

/**
 * 教室地点控制器
 * @author by JingQ on 2018/1/4
 */
@RestController
@Api(value = "教室信息管理", tags = "Controller")
@RequestMapping("/class/classroom")
public class ClassroomController {

    private static final String TIME_TEMPLATE = "yyyy-MM-dd";

    private Logger LOGGER = LoggerFactory.getLogger(ClassroomController.class);

    @Autowired
    private ClassroomService classroomServiceImpl;

    @Autowired
    private CourseRecordService courseRecordServiceImpl;


    @ApiOperation(value = "添加教室信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "buildingNumber", value = "建筑楼号码", required = true, paramType = "Integer"),
            @ApiImplicitParam(name = "classroom", value = "教室号码", required = true, paramType = "String"),
            @ApiImplicitParam(name = "capacity", value = "教室容量", required = true, paramType = "Integer"),
            @ApiImplicitParam(name = "status", value = "教室状态", required = true, paramType = "Integer", allowableValues = "0: 禁用/ 1: 启用"),
            @ApiImplicitParam(name = "principal", value = "负责人", paramType = "String")
    })
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public JSON addClassroom(@RequestBody Classroom classroom, HttpServletRequest request) {
        SingleResult<Classroom> result = new SingleResult<>();
        try {
            classroom = classroomServiceImpl.add(classroom);
            LOGGER.info("成功添加教室: ", classroom);
        } catch (BusinessException e) {
            LOGGER.error("添加教室失败: ", e);
            result.returnError(e);
            return (JSON) JSON.toJSON(result);
        } catch (Exception e) {
            LOGGER.error("添加教室失败: ", e);
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(classroom);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新教室信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "教室ID", required = true, paramType = "Integer"),
            @ApiImplicitParam(name = "buildingNumber", value = "建筑楼号码", required = true, paramType = "Integer"),
            @ApiImplicitParam(name = "classroom", value = "教室号码", required = true, paramType = "String"),
            @ApiImplicitParam(name = "principal", value = "教室负责人", paramType = "String"),
            @ApiImplicitParam(name = "status", value = "教室状态", paramType = "Integer", allowableValues = "0: 禁用/ 1: 启用")
    })
    public JSON updateClassroom(@RequestBody Classroom classroom, HttpServletRequest request) {
        SingleResult<Classroom> result = new SingleResult<>();
        try {
            classroomServiceImpl.update(classroom);
            LOGGER.info("教室信息更新成功:", classroom);
        } catch (Exception e) {
            LOGGER.error("教室信息更新失败:", e);
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(classroom);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "根据ID获取教室信息", tags = "1.0.0")
    public JSON getClassroom(
            @ApiParam(name = "id", value = "教室ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<Classroom> result = new SingleResult<>();
        Classroom classroom;
        try {
            classroom = classroomServiceImpl.getById(id);
        } catch (Exception e) {
            LOGGER.error("查询教室失败: ", e);
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(classroom);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除教室", tags = "1.0.0")
    public JSON deleteClassroom(
            @ApiParam(name = "id", value = "教室ID", required = true, type = "Integer") @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<Integer> result = new SingleResult<>();
        try {
            classroomServiceImpl.deleteById(id);
        } catch (Exception e) {
            LOGGER.error("删除教室失败: ", e);
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(id);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "query", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "教室列表查询", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "buildingNumber", value = "教师楼号码", required = true, paramType = "Integer"),
            @ApiImplicitParam(name = "classroom", value = "教室号码",  paramType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "Integer", allowableValues = "0: 禁用/1: 启用")
    })
    public JSON query(@RequestBody ClassroomQueryParam queryParam, HttpServletRequest request) {
        ListResult<Classroom> result = new ListResult<>();
        try {
            result.returnSuccess(classroomServiceImpl.getList(queryParam));
        } catch (Exception e) {
            LOGGER.error("根据条件查询失败:", e);
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "details", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取教室最近的课程信息", tags = "1.0.0")
    public JSON queryDetail(
            @ApiParam(name = "id", value = "教室ID", required = true, type = "Integer") @RequestParam("id") Integer id, HttpServletRequest request) {
        ListResult<Course> result = new ListResult<>();
        try {
            String currentTime = PeriodUtil.format(System.currentTimeMillis(), TIME_TEMPLATE);
            result.returnSuccess(classroomServiceImpl.getUsingStatement(id, currentTime));
        } catch (Exception e) {
            LOGGER.error("查询最近上的实验课失败:", e);
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        return (JSON) JSON.toJSON(result);
    }

}

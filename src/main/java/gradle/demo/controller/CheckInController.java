package gradle.demo.controller;

import gradle.demo.model.CheckInRecord;
import gradle.demo.model.CourseRecord;
import gradle.demo.model.User;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.CheckInService;
import gradle.demo.service.CourseRecordService;
import gradle.demo.service.UserService;
import gradle.demo.util.PeriodUtil;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.StringUtil;
import gradle.demo.util.result.ApiResponse;
import gradle.demo.util.result.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 签到
 *
 * @author by JingQ on 2018/3/14
 */
@RestController
@Api(value = "签到管理器", tags = "Controller")
@RequestMapping("/class/checkIn")
public class CheckInController {

    @Autowired
    private CheckInService checkInServiceImpl;

    @Autowired
    private CourseRecordService courseRecordService;

    @Autowired
    private UserService userServiceImpl;


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "签到", tags = "1.1.0")
    public ApiResponse add(
            @ApiParam(name = "courseId", value = "课程ID", type = "Integer", required = true) @RequestParam("courseId") Integer courseId,
            @ApiParam(name = "code", value = "签到识别码", type = "String", required = true) @RequestParam("code") String code,
            HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        CheckInRecord record = checkInServiceImpl.getByCourserRecordIdAndUserId(courseId, user.getIdNumber());
        if (record != null) {
            return ApiResponse.error(new Message("QD000003", "您已经签到，不需要再签了"));
        }
        String currentTime = PeriodUtil.format(System.currentTimeMillis(), "yyyy-MM-dd");
        CourseRecord courseRecord = courseRecordService.getByClassTimeAndCID(currentTime, courseId);
        if (courseRecord == null) {
            return ApiResponse.error(new Message("QD000001", "不在签到时间内"));
        }
        if (!StringUtil.isEquals(courseRecord.getCheckCode(), code)) {
            return ApiResponse.error(new Message("QD000002", "识别码不正确"));
        }
        checkInServiceImpl.add(convertRecord(user, courseRecord));
        return ApiResponse.success();
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "获取签到列表", tags = "1.1.0")
    public ApiResponse getByIdNumber(HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        List<CheckInRecord> records = checkInServiceImpl.getByIdNumber(user.getIdNumber());
        return ApiResponse.success(records);
    }

    @RequestMapping(value = "/getListByCourseRecordId", method = RequestMethod.GET)
    @ApiOperation(value = "获取签到列表", tags = "1.1.0")
    public ApiResponse getByIdNumberAndCourserRecordId(
            @ApiParam(name = "courseRecordId", value = "课时Id", type = "Integer", required = true) @RequestParam("courseRecordId") Integer courseRecordId) {
        List<CheckInRecord> records = checkInServiceImpl.getByCourseRecordId(courseRecordId);
        return ApiResponse.success(records);
    }

    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ApiOperation(value = "修改学生的签到状态", tags = "1.1.0")
    public ApiResponse changeStatus(
            @ApiParam(name = "idNumber", value = "学号", required = true) @RequestParam("idNumber") String idNumber,
            @ApiParam(name = "status", value = "签到状态", allowableValues = "0:未签到， 1:已签到", required = true) @RequestParam("status") Integer status,
            @ApiParam(name = "courseRecordId", value = "课时ID", required = true) @RequestParam("courseRecordId") Integer courseRecordId) {
        CheckInRecord record = checkInServiceImpl.getByCourserRecordIdAndUserId(courseRecordId, idNumber);
        // 取消签到
        if (status == 0) {
           if (record != null) {
               checkInServiceImpl.deleteById(record.getId());
               return ApiResponse.success();
           }
        } else if (status == 1) {
            if (record == null) {
                //添加签到记录
                User user = userServiceImpl.getByIdNumber(idNumber, UserType.STUDENT.getCode());
                CourseRecord courseRecord = courseRecordService.getById(courseRecordId);
                if (courseRecord == null) {
                    return ApiResponse.error(new Message("QD000004", "没有这节课时"));
                }
                CheckInRecord checkInRecord = convertRecord(user, courseRecord);
                checkInServiceImpl.add(checkInRecord);
            }
        }
        return ApiResponse.success();
    }

    private CheckInRecord convertRecord(User user, CourseRecord courseRecord) {
        CheckInRecord record = new CheckInRecord();
        record.setCourseId(courseRecord.getCourseId());
        record.setCourseRecordId(courseRecord.getId());
        record.setUserId(user.getId());
        record.setIdNumber(user.getIdNumber());
        record.setCourseName(courseRecord.getCourseName());
        record.setLongitude("123");
        record.setLatitude("321");
        return record;
    }
}

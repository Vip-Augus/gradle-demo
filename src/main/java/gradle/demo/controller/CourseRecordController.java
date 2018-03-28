package gradle.demo.controller;

import gradle.demo.model.CourseRecord;
import gradle.demo.model.Homework;
import gradle.demo.model.User;
import gradle.demo.model.dto.CourseRecordDTO;
import gradle.demo.service.*;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.result.ApiResponse;
import gradle.demo.util.result.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.assertj.core.util.Lists;
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
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 课程记录控制器
 *
 * @author by JingQ on 2018/1/6
 */
@Slf4j
@RestController
@RequestMapping("/class/courseRecord")
@Api(value = "课程记录", tags = "Controller")
public class CourseRecordController {


    private static final String BASE_DIR = "record/";
    private static final String[] excelHeader = {"学号", "分数", "评语", "作业链接"};
    @Autowired
    private CourseRecordService courseRecordServiceImpl;
    @Autowired
    private FileManageService fileManageServiceImpl;
    @Autowired
    private CourseUserService courseUserService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private HomeworkService homeworkServiceImpl;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "课程记录添加", tags = "1.0.0")
    public ApiResponse addRecord(
            @ApiParam(name = "courseName", value = "课时名称", required = true, type = "String") @RequestParam("courseName") String courseName,
            @ApiParam(name = "courseId", value = "课程ID", required = true, type = "Integer") @RequestParam("courseId") Integer courseId,
            @ApiParam(name = "uploadEndTime", value = "上传作业最迟时间", type = "String") @RequestParam(value = "uploadEndTime", required = false) String uploadEndTime,
            @ApiParam(name = "classroomId", value = "教室Id", type = "Integer") @RequestParam(value = "classroomId", required = false) Integer classroomId,
            @ApiParam(name = "file", value = "课时材料", type = "file") @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        StringBuilder filePath = new StringBuilder(BASE_DIR);
        filePath.append(courseName).append("/").append(courseId).append("/");
        String url = fileManageServiceImpl.upload(file, filePath.toString());
        CourseRecord record = convertRecord(courseName, courseId, uploadEndTime, url, classroomId);
        courseRecordServiceImpl.add(record);
        log.info("成功添加课时记录:", record.getId());
        return ApiResponse.success(record);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新课程信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程记录ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "courseName", value = "课程名称", paramType = "String", required = true),
            @ApiImplicitParam(name = "briefIntroduction", value = "课程简介", paramType = "String", required = true),
            @ApiImplicitParam(name = "classroomId", value = "教室ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "beginPeriod", value = "开始时间", paramType = "String", allowableValues = "起始日期:yyyy-MM-dd"),
            @ApiImplicitParam(name = "endPeriod", value = "结束时间", paramType = "String", allowableValues = "结束日期：yyyy-MM-dd"),
            @ApiImplicitParam(name = "day", value = "周几", paramType = "Integer", allowableValues = "1-7"),
            @ApiImplicitParam(name = "classBegin", value = "第几节开始", paramType = "Integer", allowableValues = "1-12"),
            @ApiImplicitParam(name = "classEnd", value = "第几节结束", paramType = "Integer", allowableValues = "1-12"),
            @ApiImplicitParam(name = "courseFileUrl", value = "课程课时材料", paramType = "file")
    })
    public ApiResponse updateRecord(@RequestBody CourseRecord record, HttpServletRequest request) {
        try {
            courseRecordServiceImpl.update(record);
            log.info("成功修改课时记录: ", record.getId());
        } catch (Exception e) {
            log.error("修改课时内容失败:", record.getId(), e);
            return ApiResponse.error(new Message("CR000002", "修改课时内容失败"));
        }
        return ApiResponse.success(record);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取课程记录信息", tags = "1.0.0")
    public ApiResponse getRecord(
            @ApiParam(name = "id", value = "课程记录ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        CourseRecord record = courseRecordServiceImpl.getById(id);
        if (record == null) {
            return ApiResponse.error(new Message("CR000003", "课时ID不正确"));
        }
        return ApiResponse.success(record);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除课程记录信息", tags = "1.0.0")
    public ApiResponse deleteRecord(@ApiParam(name = "id", value = "课程记录ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        int count = courseRecordServiceImpl.deleteById(id);
        return ApiResponse.success(count);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取课程记录列表", tags = "1.0.0")
    public ApiResponse queryListByEPId(
            @ApiParam(name = "courseId", value = "课程ID", required = true, type = "Integer") @RequestParam("courseId") Integer courseId,
            HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        List<CourseRecordDTO> courseRecords = courseRecordServiceImpl.getByCourseIdAndUser(courseId, user);
        if (CollectionUtils.isEmpty(courseRecords)) {
            return ApiResponse.success(Lists.newArrayList());
        }
        return ApiResponse.success(courseRecords);
    }


    @RequestMapping(value = "export", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "导出每次课堂记录的成绩", tags = "1.0.0")
    public void exportScore(
            @ApiParam(name = "courseRecordId", value = "课程记录Id", type = "Integer", required = true) @RequestParam("courseRecordId") Integer courseRecordId, HttpServletResponse response) {
        CourseRecord record = courseRecordServiceImpl.getById(courseRecordId);
        try {
            List<Homework> details = homeworkServiceImpl.getDetailsByCourseRecordId(courseRecordId, null);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;fileName=" + URLEncoder.encode(record.getCourseName(), "UTF-8") + ".xls");
            HSSFWorkbook wb = export(details, record.getCourseName());
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            log.error("导出分数失败: ", e);
        }
    }

    private CourseRecord convertRecord(String courseName, Integer courseId, String uploadEndTime, String fileUrl, Integer classroomId) {
        CourseRecord record = new CourseRecord();
        record.setCourseId(courseId);
        record.setCourseName(courseName);
        record.setUploadEndTime(uploadEndTime);
        record.setCourseFileUrl(fileUrl);
        record.setClassroomId(classroomId);
        return record;
    }


    private HSSFWorkbook export(List<Homework> details, String sheetName) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCellStyle style = wb.createCellStyle();

        for (int i = 0; i < excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
        }

        for (int i = 0; i < details.size(); i++) {
            row = sheet.createRow(i + 1);
            Homework detail = details.get(i);
            row.createCell(0).setCellValue(detail.getIdNumber());
            row.createCell(1).setCellValue(detail.getScore());
            row.createCell(2).setCellValue(detail.getComment());
            row.createCell(3).setCellValue(detail.getHomeworkUrl());
        }

        sheet.autoSizeColumn((short) 0); //调整第一列宽度
        sheet.autoSizeColumn((short) 1); //调整第二列宽度
        sheet.autoSizeColumn((short) 2); //调整第三列宽度
        sheet.autoSizeColumn((short) 3); //调整第四列宽度

        return wb;
    }
}

package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.ExperimentDetail;
import gradle.demo.model.ExperimentMessage;
import gradle.demo.model.ExperimentRecord;
import gradle.demo.model.Notice;
import gradle.demo.model.User;
import gradle.demo.service.*;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.SingleResult;
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
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.List;

/**
 * 课程记录控制器
 *
 * @author by JingQ on 2018/1/6
 */
@Slf4j
@RestController
@RequestMapping("/web/epRecord")
@Api(value = "课程记录", tags = "Controller")
public class ExperimentRecordController {


    private static final String BASE_DIR = "record/";
    private static final String[] excelHeader = {"学号", "分数", "评语", "作业链接"};
    @Autowired
    private ExperimentRecordService experimentRecordServiceImpl;
    @Autowired
    private FileManageService fileManageServiceImpl;
    @Autowired
    private ExperimentMessageService messageService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ExperimentUserService experimentUserService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ExperimentDetailService experimentDetailServiceImpl;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "课程记录添加", tags = "1.0.0")
    public JSON addRecord(
            @ApiParam(name = "epName", value = "课程名称", required = true, type = "String") @RequestParam("epName") String epName,
            @ApiParam(name = "epId", value = "课程ID", required = true, type = "Integer") @RequestParam("epId") Integer epId,
            @ApiParam(name = "uploadEndTime", value = "上传作业最迟时间", required = true, type = "String") @RequestParam("uploadEndTime") String uploadEndTime,
            @ApiParam(name = "classroomId", value = "教室Id", required = true, type = "Integer") @RequestParam("classroomId") Integer classroomId,
            @ApiParam(name = "file", value = "实验材料", type = "file") @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        SingleResult<ExperimentRecord> result = new SingleResult<>();
        if (file.isEmpty()) {
            log.error("未选择材料,发布实验失败: ", epId, epName);
            result.returnError("未选择材料,发布实验失败");
        } else {
            try {
                StringBuilder filePath = new StringBuilder(BASE_DIR);
                filePath.append(epName).append("/").append(epId).append("/");
                String url = fileManageServiceImpl.upload(file, filePath.toString());
                ExperimentRecord record = convertRecord(epName, epId, uploadEndTime, url, classroomId);
                result.returnSuccess(experimentRecordServiceImpl.add(record));
                log.info("成功添加实验记录:", record.getId());
                sendMessage(record, request);
            } catch (Exception e) {
                log.error("发布实验失败: ", epName, e);
                result.returnError("发布实验失败");
            }
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新课程信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程记录ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "epName", value = "课程名称", paramType = "String", required = true),
            @ApiImplicitParam(name = "briefIntroduction", value = "课程简介", paramType = "String", required = true),
            @ApiImplicitParam(name = "classroomId", value = "教室ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "beginPeriod", value = "开始时间", paramType = "String", allowableValues = "起始日期:yyyy-MM-dd"),
            @ApiImplicitParam(name = "endPeriod", value = "结束时间", paramType = "String", allowableValues = "结束日期：yyyy-MM-dd"),
            @ApiImplicitParam(name = "classBegin", value = "周几", paramType = "Integer", allowableValues = "1-7"),
            @ApiImplicitParam(name = "classEnd", value = "第几节开始", paramType = "Integer", allowableValues = "1-12"),
            @ApiImplicitParam(name = "courseId", value = "第几节结束", paramType = "Integer", allowableValues = "1-12"),
            @ApiImplicitParam(name = "epFileUrl", value = "课程实验材料", paramType = "file")
    })
    public JSON updateRecord(@RequestBody ExperimentRecord record, HttpServletRequest request) {
        SingleResult<ExperimentRecord> result = new SingleResult<>();
        try {
            experimentRecordServiceImpl.update(record);
            result.returnSuccess(record);
            log.info("成功修改实验记录: ", record.getId());
        } catch (Exception e) {
            log.error("修改实验内容失败:", record.getId(), e);
            result.returnError("修改实验内容失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取课程记录信息", tags = "1.0.0")
    public JSON getRecord(
            @ApiParam(name = "id", value = "课程记录ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<ExperimentRecord> result = new SingleResult<>();
        try {
            ExperimentRecord record = experimentRecordServiceImpl.getById(id);
            result.returnSuccess(record);
        } catch (Exception e) {
            log.error("查询实验内容失败:", id, e);
            result.returnError("查询实验内容失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除课程记录信息", tags = "1.0.0")
    public JSON deleteRecord(@ApiParam(name = "id", value = "课程记录ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<Integer> result = new SingleResult<>();
        try {
            result.returnSuccess(experimentRecordServiceImpl.deleteById(id));
            log.error("删除实验内容成功:", id);
        } catch (Exception e) {
            log.error("删除实验内容失败:", id, e);
            result.returnError("删除实验内容失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取课程记录列表", tags = "1.0.0")
    public JSON queryListByEPId(
            @ApiParam(name = "epId", value = "课程ID", required = true, type = "Integer") @RequestParam("epId") Integer epId,
            HttpServletRequest request) {
        ListResult<ExperimentRecord> result = new ListResult<>();
        try {
            User user = SessionUtil.getUser(request.getSession());
            result.returnSuccess(experimentRecordServiceImpl.getByEPIdAndUser(epId, user.getIdNumber()));
        } catch (Exception e) {
            log.error("查询实验内容列表失败:", epId, e);
            result.returnError("查询实验内容列表失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "export", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "导出每次课堂记录的成绩", tags = "1.0.0")
    public void exportScore(
            @ApiParam(name = "epRecordId", value = "课程记录Id", type = "Integer", required = true) @RequestParam("epRecordId") Integer epRecordId, HttpServletResponse response) {
        ExperimentRecord record = experimentRecordServiceImpl.getById(epRecordId);
        try {
            List<ExperimentDetail> details = experimentDetailServiceImpl.getDetailsByEPRecordId(epRecordId, null);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;fileName=" + URLEncoder.encode(record.getEpName(), "UTF-8") + ".xls");
            HSSFWorkbook wb = export(details, record.getEpName());
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            log.error("导出分数失败: ", e);
        }
    }

    private ExperimentRecord convertRecord(String epName, Integer epId, String uploadEndTime, String fileUrl, Integer classroomId) {
        ExperimentRecord record = new ExperimentRecord();
        record.setEpId(epId);
        record.setEpName(epName);
        record.setUploadEndTime(uploadEndTime);
        record.setEpFileUrl(fileUrl);
        record.setClassroomId(classroomId);
        return record;
    }

    private void sendMessage(ExperimentRecord record, HttpServletRequest request) {
        try {
            User user = SessionUtil.getUser(request.getSession());
            Notice notice = new Notice();
            notice.setTitle("创建实验课" + record.getEpName() + "通知");
            notice.setContent(getContent(record));
            notice.setCreateId(user.getId());
            notice.setEpId(record.getEpId());
            notice.setCreateTime(new Date());
            noticeService.add(notice);
            List<Integer> studentIds = experimentUserService.getUserIdsByEpId(record.getEpId());
            new Thread() {
                @Override
                public void run() {
                    for (Integer id : studentIds) {
                        ExperimentMessage message = new ExperimentMessage();
                        message.setFromId(user.getId());
                        message.setToId(id);
                        message.setEpId(record.getEpId());
                        message.setTitle(notice.getTitle());
                        message.setContent(notice.getContent());
                        message.setHasRead(0);
                        message.setCreatedDate(new Date());
                        message.setConversationId(message.getConversationId());
                        messageService.addMessage(message);
                    }
                }
            }.start();
        } catch (Exception e) {
            log.error("发送消息失败：", e);
        }
    }

    private String getContent(ExperimentRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("您的实验课： ");
        sb.append(experimentService.getById(record.getEpId()).getName());
        sb.append(" 发布了新的实验：");
        sb.append(record.getEpName());
        sb.append(", 请前往【查看实验课】模块查看详情。");
        return sb.toString();
    }

    private HSSFWorkbook export(List<ExperimentDetail> details, String sheetName) {
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
            ExperimentDetail detail = details.get(i);
            row.createCell(0).setCellValue(detail.getIdNumber());
            row.createCell(1).setCellValue(detail.getScore());
            row.createCell(2).setCellValue(detail.getComment());
            row.createCell(3).setCellValue(detail.getEpFileUrl());
        }

        sheet.autoSizeColumn((short) 0); //调整第一列宽度
        sheet.autoSizeColumn((short) 1); //调整第二列宽度
        sheet.autoSizeColumn((short) 2); //调整第三列宽度
        sheet.autoSizeColumn((short) 3); //调整第四列宽度

        return wb;
    }
}

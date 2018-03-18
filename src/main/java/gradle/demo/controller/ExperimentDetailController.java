package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.ExperimentDetail;
import gradle.demo.model.Score;
import gradle.demo.model.User;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.ExperimentDetailService;
import gradle.demo.service.FileManageService;
import gradle.demo.service.ScoreService;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 课程作业记录
 * Author by JingQ on 2018/1/6
 */
@Slf4j
@RestController
@RequestMapping("/web/epDetail")
@Api(value = "课程作业管理", tags = "Controller")
public class ExperimentDetailController {

    private static final String BASE_DIR = "epDetail/";

    @Autowired
    private FileManageService fileManageServiceImpl;

    @Autowired
    private ExperimentDetailService experimentDetailServiceImpl;

    @Autowired
    private ScoreService scoreServiceImpl;

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "新增课程作业记录", tags = "1.0.0")
    public JSON addRecord(
            @ApiParam(name = "epId", value = "课程ID", required = true, type = "Integer") @RequestParam("epId") Integer epId, @RequestParam("epRecordId") Integer epRecordId, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        SingleResult<ExperimentDetail> result = new SingleResult<>();
        User user;
        String url = null;
        try {
            user = SessionUtil.getUser(request.getSession());
            url = fileManageServiceImpl.upload(file, BASE_DIR+"/"+epRecordId+"/");
        } catch (Exception e) {
            log.error("上传失败: ", e);
            return (JSON) JSON.toJSON(e.getMessage());
        }
        ExperimentDetail record = convertDetail(epId, epRecordId, user, url, file.getOriginalFilename());
//        insertScore(epRecordId, user.getId());
        result.returnSuccess(experimentDetailServiceImpl.add(record));
        log.info("上传成功: epDetailId:", record.getId());
        return (JSON) JSON.toJSON(result);
    }


    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新作业信息", tags = "1.1.0")
    public JSON updateRecord(@RequestBody ExperimentDetail record, HttpServletRequest request) {
        SingleResult<ExperimentDetail> result = new SingleResult<>();
        try {
            experimentDetailServiceImpl.update(record);
            result.returnSuccess(record);
        } catch (Exception e) {
            log.error("更新失败: ", e);
            return (JSON) JSON.toJSON(e.getMessage());
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取单条作业信息", tags = "1.1.0")
    public JSON getRecord(@RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<ExperimentDetail> result = new SingleResult<>();
        ExperimentDetail record = experimentDetailServiceImpl.getById(id);
        result.returnSuccess(record);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除单条作业信息", tags = "1.1.0")
    public JSON deleteRecord(@RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<Integer> result = new SingleResult<>();
        try {
            result.returnSuccess(experimentDetailServiceImpl.deleteById(id));
        } catch (Exception e) {
            log.error("删除失败: ", e);
            return (JSON) JSON.toJSON(e.getMessage());
        }
        return (JSON) JSON.toJSON(result);
    }



    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取作业列表", tags = "1.1.0")
    public JSON queryListByEPId(@RequestParam("epRecordId") Integer epRecordId, HttpServletRequest request) {
        ListResult<ExperimentDetail> result = new ListResult<>();
        User user;
        String url = null;
        try {
            user = SessionUtil.getUser(request.getSession());
        } catch (Exception e) {
            log.error("查询用户失败: ", e);
            return (JSON) JSON.toJSON(e.getMessage());
        }
        try {
            Integer userId = UserType.TEACHER.getCode().equals(user.getType()) ? null : user.getId();
            result.returnSuccess(experimentDetailServiceImpl.getDetailsByEPRecordId(epRecordId, userId));
        } catch (Exception e) {
            log.error("查询上传作业列表失败:", epRecordId, e);
            result.returnError("查询实验内容列表失败");
        }
        return (JSON) JSON.toJSON(result);
    }

    private ExperimentDetail convertDetail(Integer epId, Integer epRecordId, User user, String fileUrl, String fileName) {
        ExperimentDetail detail = new ExperimentDetail();
        detail.setEpId(epId);
        detail.setEpRecordId(epRecordId);
        detail.setUserId(user.getId());
        detail.setEpFileName(fileName);
        detail.setUploadName(user.getName());
        detail.setIdNumber(user.getIdNumber());
        detail.setEpFileUrl(fileUrl);
        return detail;
    }

    private void insertScore(Integer epRecordId, Integer stuId) {
        Score score = new Score();
        score.setEprecordId(epRecordId);
        score.setStudentId(stuId);
        scoreServiceImpl.add(score);
    }


}

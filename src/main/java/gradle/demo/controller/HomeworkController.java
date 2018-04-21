package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.config.MinioConfigBean;
import gradle.demo.model.CourseRecord;
import gradle.demo.model.Homework;
import gradle.demo.model.HomeworkOuter;
import gradle.demo.model.User;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.CourseRecordService;
import gradle.demo.service.HomeworkOuterService;
import gradle.demo.service.HomeworkService;
import gradle.demo.service.FileManageService;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.StringUtil;
import gradle.demo.util.file.UploadObject;
import gradle.demo.util.result.ApiResponse;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ExceptionDefinitions;
import gradle.demo.util.result.Message;
import gradle.demo.util.result.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 课程作业记录
 *
 * @author by JingQ on 2018/1/6
 */
@Slf4j
@RestController
@RequestMapping("/class/homework")
@Api(value = "课程作业管理", tags = "Controller")
public class HomeworkController {

    private static final String BASE_DIR = "homework/";

    @Autowired
    private FileManageService fileManageServiceImpl;

    @Autowired
    private HomeworkService homeworkServiceImpl;

    @Autowired
    private HomeworkOuterService homeworkOuterServiceImpl;

    @Autowired
    private MinioConfigBean minioConfigBean;


    @RequestMapping(value = "/student/add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "学生上传课时作业", tags = "1.0.0")
    public ApiResponse addRecord(
            @ApiParam(name = "courseId", value = "课程ID", required = true, type = "Integer") @RequestParam("courseId") Integer courseId,
            @ApiParam(name = "homeworkOuterId", value = "作业外层ID", required = true, type = "Integer") @RequestParam("homeworkOuterId") Integer homeworkOuterId,
            @ApiParam(name = "file", value = "作业", required = true, type = "file") @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        //如果已经提交过作业
        if (!CollectionUtils.isEmpty(homeworkServiceImpl.getDetailsByHomeworkOuterId(homeworkOuterId, user.getId()))) {
            return ApiResponse.error(new Message("ZY0000001", "已经提交过，无需重新提交"));
        }
        HomeworkOuter homeworkOuter = homeworkOuterServiceImpl.getById(homeworkOuterId);
        String url;
        try {
            url = fileManageServiceImpl.upload(file, BASE_DIR + homeworkOuter.getHomeworkName() + "_" + homeworkOuter.getCreateDate() + "/");
        } catch (Exception e) {
            log.error("上传失败: ", e);
            return ApiResponse.error(new Message("ZY000001", e.getMessage()));
        }
        Homework record = convertDetail(courseId, homeworkOuterId, user, url, file.getOriginalFilename());
        homeworkServiceImpl.add(record);
        log.info("上传成功: courseDetailId:", record.getId());
        return ApiResponse.success(record);
    }

    @RequestMapping(value = "/downloadAll", method = RequestMethod.GET)
    @ApiOperation(value = "下载一个课时的全部作业", tags = "1.1.0")
    @ResponseBody
    public ApiResponse downloadHomeworkByCID(
            @ApiParam(name = "homeworkOuterId", value = "作业外层ID") @RequestParam("homeworkOuterId") Integer homeworkOuterId,
            HttpServletResponse response) throws IOException {
        HomeworkOuter homeworkOuter = homeworkOuterServiceImpl.getById(homeworkOuterId);
        if (homeworkOuter == null) {
            return ApiResponse.error(new Message("ZY0000005", "没有这个作业"));
        }
        List<String> homeworkUrls = homeworkServiceImpl.getHomeworkUrlsByHWOuterID(homeworkOuterId);
        List<UploadObject> uploadObjects = convertFromUrls(homeworkUrls);
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(homeworkOuter.getHomeworkName() + ".zip", "utf-8"));
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        for (UploadObject uploadObject : uploadObjects) {
            InputStream inputStream = fileManageServiceImpl.getInputStreamFromObject(uploadObject);
            if (inputStream == null) {
                continue;
            }
            ZipEntry entry = new ZipEntry(uploadObject.getFullName());
            // 建立一个目录进入点
            zos.putNextEntry(entry);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            inputStream.close();
            zos.closeEntry();// 关闭当前目录进入点，将输入流移动下一个目录进入点
        }
        zos.close();
        return ApiResponse.success();
    }

    @RequestMapping(value = "markScore", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新作业分数", tags = "1.1.0")
    public ApiResponse markScore(
            @ApiParam(name = "id", value = "主键ID", type = "Integer", required = true) @RequestParam("id") Integer id,
            @ApiParam(name = "score", value = "分数", type = "String", required = true) @RequestParam("score") String score,
            @ApiParam(name = "comment", value = "评语", type = "String", required = true) @RequestParam("comment") String comment) {
        int count = homeworkServiceImpl.markHomework(id, score, comment);
        return ApiResponse.success(count);
    }

    @RequestMapping(value = "markHomework", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "老师上传批改的zip包", tags = "1.1.0")
    public ApiResponse markHomework(
            @ApiParam(name = "homeworkOuterId", value = "课时ID", type = "Integer", required = true) @RequestParam("homeworkOuterId") Integer courseRecordId,
            @ApiParam(name = "file", value = "批改后的zip包", type = "file", required = true) @RequestParam("file") MultipartFile file) throws IOException {
        String fileOringinalName = file.getOriginalFilename();
        String fileSuffix = fileOringinalName.substring(fileOringinalName.lastIndexOf(".") + 1, fileOringinalName.length());
        if (!StringUtil.isEquals(fileSuffix, "zip")) {
           throw new BusinessException(ExceptionDefinitions.INCORRECT_FILE_SUFFIX);
        }
        homeworkServiceImpl.markHomework(file, courseRecordId);
        return ApiResponse.success();
    }

    @RequestMapping(value = "returnHomework", method = RequestMethod.POST)
    @ApiOperation(value = "老师返回批改的作业", tags = "1.1.0")
    public ApiResponse returnHomework(
            @ApiParam(name = "id", value = "学生提交作业的ID", type = "Integer", readOnly = true) @RequestParam("id") Integer id,
            @ApiParam(name = "file", value = "批改后作业", type = "file", readOnly = true) @RequestParam("file") MultipartFile file) {
        if (id == 0 || file.isEmpty()) {
            return ApiResponse.error(new Message("ZY000006", "返回作业格式不正确"));
        }
        homeworkServiceImpl.markHomeworkUrl(id, file);
        return ApiResponse.success();
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新作业信息", tags = "1.1.0")
    public JSON updateRecord(@RequestBody Homework record, HttpServletRequest request) {
        SingleResult<Homework> result = new SingleResult<>();
        try {
            homeworkServiceImpl.update(record);
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
    public ApiResponse getRecord(@RequestParam("id") Integer id, HttpServletRequest request) {
        Homework record = homeworkServiceImpl.getById(id);
        return ApiResponse.success(record);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除单条作业信息", tags = "1.1.0")
    public ApiResponse deleteRecord(@RequestParam("id") Integer id, HttpServletRequest request) {
        try {
            homeworkServiceImpl.deleteById(id);
        } catch (Exception e) {
            log.error("删除失败: ", e);
            return ApiResponse.error(new Message("ZY000002", e.getMessage()));
        }
        return ApiResponse.success();
    }


    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取作业列表", tags = "1.1.0")
    public ApiResponse queryListByEPId(
            @ApiParam(name = "homeworkOuterId", value = "课时ID", required = true, type = "Integer") @RequestParam("homeworkOuterId") Integer courseRecordId,
            HttpServletRequest request) {
        User user = SessionUtil.getUser(request.getSession());
        try {
            Integer userId = UserType.TEACHER.getCode().equals(user.getType()) ? null : user.getId();
            List<Homework> homeworkList = homeworkServiceImpl.getDetailsByHomeworkOuterId(courseRecordId, userId);
            return ApiResponse.success(homeworkList);
        } catch (Exception e) {
            log.error("查询上传作业列表失败:", courseRecordId, e);
            return ApiResponse.error(new Message("ZY000003", e.getMessage()));
        }
    }

    private Homework convertDetail(Integer courseId, Integer courseRecordId, User user, String fileUrl, String fileName) {
        Homework detail = new Homework();
        detail.setHomeworkOuterId(courseRecordId);
        detail.setCourseId(courseId);
        detail.setUserId(user.getId());
        detail.setHomeworkName(fileName);
        detail.setUploadName(user.getName());
        detail.setIdNumber(user.getIdNumber());
        detail.setHomeworkUrl(fileUrl);
        return detail;
    }

    private List<UploadObject> convertFromUrls(List<String> homeworkUrls) {
        List<UploadObject> uploadObjects = Lists.newArrayList();
        for (String url : homeworkUrls) {
            UploadObject uploadObject = new UploadObject();
            String realPath = url.replace(minioConfigBean.getStaticUrl(), "");
            int index = realPath.lastIndexOf("/");
            uploadObject.setDir(realPath.substring(0, index + 1));
            uploadObject.setFullName(realPath.substring(index + 1, realPath.length()));
            uploadObjects.add(uploadObject);
        }
        return uploadObjects;
    }

}

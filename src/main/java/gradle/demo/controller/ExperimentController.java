package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.Experiment;
import gradle.demo.model.User;
import gradle.demo.model.dto.ExperimentDTO;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.ClassroomService;
import gradle.demo.service.ExperimentService;
import gradle.demo.service.ExperimentUserService;
import gradle.demo.service.UserService;
import gradle.demo.util.CodeConstants;
import gradle.demo.util.ImportUtil;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.convert.EPConverter;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.SingleResult;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

/**
 * 课程（最外层）
 * @author by JingQ on 2018/1/2
 */
@Slf4j
@RestController
@RequestMapping("/web/ep")
@Api(value = "课程管理", tags = "Controller")
public class ExperimentController {

    private static final String SPLIT_SIGNAL = ",";

    /**
     * 课程信息服务
     */
    @Autowired
    private ExperimentService experimentServiceImpl;

    /**
     * ep转换器
     */
    @Autowired
    private EPConverter epConverter;

    /**
     * 实验课程和用户映射服务
     */
    @Autowired
    private ExperimentUserService experimentUserServiceImpl;

    /**
     * 实验室服务
     */
    @Autowired
    private ClassroomService classroomServiceImpl;

    /**
     * 用户服务
     */
    @Autowired
    private UserService userServiceImpl;


    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "添加课程", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "课程名称", paramType = "String", required = true),
            @ApiImplicitParam(name = "briefIntroduction", value = "课程简介", paramType = "String", required = true),
            @ApiImplicitParam(name = "classroomId", value = "教室ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "beginPeriod", value = "开始时间", paramType = "String", allowableValues = "起始日期:yyyy-MM-dd"),
            @ApiImplicitParam(name = "endPeriod", value = "结束时间", paramType = "String", allowableValues = "结束日期：yyyy-MM-dd"),
            @ApiImplicitParam(name = "classBegin", value = "周几", paramType = "Integer", allowableValues = "1-7"),
            @ApiImplicitParam(name = "classEnd", value = "第几节开始", paramType = "Integer", allowableValues = "1-12"),
            @ApiImplicitParam(name = "courseId", value = "第几节结束", paramType = "Integer", allowableValues = "1-12"),
    })
    public JSON add(@RequestBody Experiment experiment, HttpServletRequest request) {
        SingleResult<Experiment> result = new SingleResult<>();
        Experiment insertEP;
        //插入成功后，将老师id与实验课绑定
        try {
            insertEP = experimentServiceImpl.add(experiment);
        } catch (BusinessException e) {
            log.error("创建实验课失败: ", e);
            result.returnError(e);
            return (JSON) JSON.toJSON(result);
        } catch (Exception e) {
            log.error("创建实验课失败", e);
            result.returnError("创建实验课失败");
            return (JSON) JSON.toJSON(result);
        }
        if (insertEP == null) {
            result.returnError(CodeConstants.CLASS_TIME_CONFLICT);
            return (JSON) JSON.toJSON(result);
        }
        List<String> tmp = Lists.newArrayList(insertEP.getTIds().split(SPLIT_SIGNAL));
        if (!CollectionUtils.isEmpty(tmp)) {
            List<Integer> tIDs = Lists.transform(tmp, new Function<String, Integer>() {
                @Nullable
                @Override
                public Integer apply(@Nullable String s) {
                    return Integer.parseInt(s);
                }
            });
            experimentUserServiceImpl.batchAdd(insertEP.getId(), tIDs);
        }
        log.info("实验课创建成功: ", insertEP.getId());
        result.returnSuccess(experiment);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取课程列表", tags = "1.0.0")
    public JSON elective(HttpServletRequest request) {
        ListResult<ExperimentDTO> result = new ListResult<>();
        User user;
        try {
            user = SessionUtil.getUser(request.getSession());
        } catch (Exception e) {
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        try {
            List<Integer> epIds = experimentUserServiceImpl.getEPIDsByUserID(user.getId());
            if (!CollectionUtils.isEmpty(epIds)) {
                List<Experiment> experimentList = experimentServiceImpl.getByIds(epIds);
                result.returnSuccess(epConverter.epList2DTOList(experimentList));
            }
        } catch (Exception e) {
            log.error("查询实验课失败: ", e);
            result.returnError(e.getMessage());
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除课程信息", tags = "1.0.0")
    public JSON deleteEP(
            @ApiParam(name = "id", value = "课程ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<Integer> result = new SingleResult<>();
        try {
            experimentUserServiceImpl.deleteById(id);
        } catch (Exception e) {
            log.error("删除实验课失败: ", e);
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        result.returnSuccess(id);
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "更新课程信息", tags = "1.0.0")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "课程ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "name", value = "课程名称", paramType = "String", required = true),
            @ApiImplicitParam(name = "briefIntroduction", value = "课程简介", paramType = "String", required = true),
            @ApiImplicitParam(name = "classroomId", value = "教室ID", paramType = "Integer", required = true),
            @ApiImplicitParam(name = "beginPeriod", value = "开始时间", paramType = "String", allowableValues = "起始日期:yyyy-MM-dd"),
            @ApiImplicitParam(name = "endPeriod", value = "结束时间", paramType = "String", allowableValues = "结束日期：yyyy-MM-dd"),
            @ApiImplicitParam(name = "classBegin", value = "周几", paramType = "Integer", allowableValues = "1-7"),
            @ApiImplicitParam(name = "classEnd", value = "第几节开始", paramType = "Integer", allowableValues = "1-12"),
            @ApiImplicitParam(name = "courseId", value = "第几节结束", paramType = "Integer", allowableValues = "1-12"),
    })
    public JSON updateEP(@RequestBody Experiment experiment, HttpServletRequest request) {
        SingleResult<Experiment> result = new SingleResult<>();
        try {
            experimentServiceImpl.update(experiment);
            log.info("实验室信息更新成功:", experiment);
            result.returnSuccess(experiment);
        } catch (Exception e) {
            log.error("实验室信息更新失败:", e);
            result.returnError(e.getMessage());
            return (JSON) JSON.toJSON(result);
        }
        return (JSON) JSON.toJSON(result);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取单个课程的信息", tags = "1.0.0")
    public JSON getRecord(
            @ApiParam(name = "id", value = "课程ID", required = true, type = "Integer") @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<Experiment> result = new SingleResult<>();
        Experiment record = experimentServiceImpl.getById(id);
        result.returnSuccess(record);
        return (JSON) JSON.toJSON(result);
    }


    @RequestMapping(value = "importStudent", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "导入课程与学生关联关系", tags = "1.0.0")
    public JSON importStudent(
            @ApiParam(name = "file", value = "文件", required = true, type = "File") @RequestParam("file") MultipartFile file,
            @ApiParam(name = "epId", value = "课程ID", required = true, type = "Integer") @RequestParam("epId") Integer epId, HttpServletRequest request) {
        SingleResult<Integer> result = new SingleResult<>();
        List<String> fileUserIds = filterUserIds(file);
        List<User> existUsers = userServiceImpl.getByIdNumbers(fileUserIds, UserType.STUDENT.getCode());
        List<Integer> existUserIds = Lists.transform(existUsers, new Function<User, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable User user) {
                return user.getId();
            }
        });
        result.returnSuccess(experimentUserServiceImpl.batchAdd(epId, existUserIds));
        return (JSON) JSON.toJSON(result);
    }

    private List<String> filterUserIds(MultipartFile file) {
        List<String> contentList = Lists.newArrayList();
        try {
            if (file.isEmpty()) {
                return null;
            }
            contentList = ImportUtil.exportListFromExcel(file.getInputStream(), 0);
            Iterator<String> iterator = contentList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals("|null|null|null|null|null|null")) {
                    iterator.remove();
                }
            }
        } catch (Exception e) {
        }
        //去掉第一行
        contentList.remove(0);
        if (CollectionUtils.isEmpty(contentList)) {
            return null;
        }
        List<String> userIds = Lists.newArrayList();
        for (String tmp : contentList) {
            userIds.add(Lists.newArrayList(tmp.split("\\|")).get(1));
        }
        return userIds;
    }
}

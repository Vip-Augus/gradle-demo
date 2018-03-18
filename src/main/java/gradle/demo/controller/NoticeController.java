package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.Notice;
import gradle.demo.model.User;
import gradle.demo.model.dto.NoticeDTO;
import gradle.demo.service.NoticeService;
import gradle.demo.service.UserService;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.StringUtil;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 公告栏
 *
 * @author  by GJW on 2018/1/7.
 */
@Controller
@RequestMapping(value = "/web/notice")
@Api(value = "公告", tags = "Controller")
public class NoticeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeController.class);

    private static final int PAGE_SIZE = 10;
    @Autowired
    NoticeService noticeService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "list", method = {RequestMethod.GET})
    @ResponseBody
    @ApiOperation(value = "公告列表获取", tags = "1.0.0")
    public JSON getNoticeList(HttpServletRequest request) {
        int page = StringUtil.getInteger(request.getParameter("page"));
        ListResult<NoticeDTO> listResult = new ListResult<>();
        try {
            List<NoticeDTO> dtos = noticeService.getNoticeDTO(page * PAGE_SIZE, PAGE_SIZE);
            listResult.returnSuccess(dtos);
        } catch (Exception e) {
            LOGGER.error("获取公告列表失败" + e);
            listResult.returnError("获取公告列表失败");
        } finally {
            return (JSON) JSON.toJSON(listResult);
        }
    }


    @RequestMapping(value = "send", method = {RequestMethod.POST})
    @ResponseBody
    @ApiOperation(value = "发送公告", tags = "1.0.0")
    public JSON sendNotice(
            @ApiParam(name = "content", value = "内容", type = "String", required = true) @RequestParam("content") String content, HttpServletRequest request) {
        SingleResult<Notice> result = new SingleResult<>();
        try {
            String title = request.getParameter("title");
            User user = SessionUtil.getUser(request.getSession());
            Notice notice = new Notice();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setCreateId(user.getId());
            notice.setEpId(0);
            notice.setCreateTime(new Date());
            noticeService.add(notice);
            result.returnSuccess(notice);
        } catch (Exception e) {
            LOGGER.error("发布公告失败" + e);
            result.returnError("发布公告失败");
        } finally {
            return (JSON) JSON.toJSON(result);
        }
    }

    @RequestMapping(value = "detail", method = {RequestMethod.GET})
    @ResponseBody
    @ApiOperation(value = "获取消息详情", tags = "1.0.0")
    public JSON getNoticeDetail(
            @ApiParam(name = "id", value = "消息ID", type = "Integer", required = true) @RequestParam("id") Integer id) {
        SingleResult<Notice> result = new SingleResult<>();
        try {
            Notice notice = noticeService.getById(id);
            if (notice == null) {
                result.returnError("公告Id错误");
                return (JSON) JSON.toJSON(result);
            }
            result.returnSuccess(notice);
        } catch (Exception e) {
            LOGGER.error("获取公告详情失败" + e);
            result.returnError("获取公告详情失败");
        } finally {
            return (JSON) JSON.toJSON(result);
        }
    }
}


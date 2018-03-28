package gradle.demo.controller;

import gradle.demo.model.Notice;
import gradle.demo.model.User;
import gradle.demo.model.dto.NoticeDTO;
import gradle.demo.service.NoticeService;
import gradle.demo.service.UserService;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.result.ApiResponse;
import gradle.demo.util.result.BusinessException;
import gradle.demo.util.result.ExceptionDefinitions;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 首页--公告栏
 *
 * @author  by GJW on 2018/1/7.
 */
@RestController
@RequestMapping(value = "/class/notice")
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
    public ApiResponse<ListResult<NoticeDTO>> getNoticeList(
            @ApiParam(name = "pageNo", value = "页码，从0开始计算", type = "Integer", required = true) @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @ApiParam(name = "pageSize", value = "每页数量", type = "Integer", required = true) @RequestParam(value = "pageSize", required = false) Integer pageSize, HttpServletRequest request) {
        ListResult<NoticeDTO> listResult = new ListResult<>();
        if (pageNo == null || pageSize == null) {
            throw new BusinessException(ExceptionDefinitions.INCORRECT_PAGE_PARAM);
        }
        try {
            List<NoticeDTO> dtos = noticeService.getNoticeDTOList(pageNo * pageSize, pageSize);
            listResult.returnSuccess(dtos);
            listResult.setTotalCount(noticeService.countALlNumber());
        } catch (Exception e) {
            LOGGER.error("获取公告列表失败" + e);
            return ApiResponse.error(new Message("GG0000001", "获取公告列表失败"));
        }
        return ApiResponse.success(listResult);
    }


    @RequestMapping(value = "send", method = {RequestMethod.POST})
    @ResponseBody
    @ApiOperation(value = "发送公告", tags = "1.0.0")
    public ApiResponse<Notice> sendNotice(
            @ApiParam(name = "content", value = "内容", type = "String", required = true) @RequestParam("content") String content,
            @ApiParam(name = "title", value = "标题", type = "String", required = true) @RequestParam("title") String title,
            HttpServletRequest request) {
        Notice notice = new Notice();
        User user = SessionUtil.getUser(request.getSession());
        try {
            notice.setTitle(title);
            notice.setContent(content);
            notice.setCreateId(user.getId());
            notice.setCreateTime(new Date());
            noticeService.add(notice);
        } catch (Exception e) {
            LOGGER.error("发布公告失败" + e);
            return ApiResponse.error(new Message("GG000002", "发送公告失败"));
        }
        return ApiResponse.success(notice);
    }

    @RequestMapping(value = "detail", method = {RequestMethod.GET})
    @ResponseBody
    @ApiOperation(value = "获取消息详情", tags = "1.0.0")
    public ApiResponse<NoticeDTO> getNoticeDetail(
            @ApiParam(name = "id", value = "公告ID", type = "Integer", required = true) @RequestParam("id") Integer id) {
        NoticeDTO notice = noticeService.getNoticeDTO(id);
        if (notice == null) {
            LOGGER.error("获取公告详情失败: 消息详细为空：{}", id);
            return ApiResponse.error(new Message("GG000003", "没有找到这条公告"));
        }
        return ApiResponse.success(notice);
    }
}


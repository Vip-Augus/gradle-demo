package gradle.demo.controller;

import com.alibaba.fastjson.JSON;
import gradle.demo.model.*;
import gradle.demo.model.dto.MessageListDTO;
import gradle.demo.model.enums.UserType;
import gradle.demo.service.*;
import gradle.demo.util.SessionUtil;
import gradle.demo.util.StringUtil;
import gradle.demo.util.result.ListResult;
import gradle.demo.util.result.SingleResult;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
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
 * @author  by GJW on 2018/1/5.
 */
@RestController
@RequestMapping(value = "/web/msg")
@Api(value = "消息管理", tags = "Controller")
public class MessageController {

    private static final int PAGE_SIZE = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    ExperimentMessageService messageService;

    @Autowired
    ExperimentUserService experimentUserService;

    @Autowired
    ExperimentService experimentService;

    @Autowired
    UserService userService;

    @Autowired
    NoticeService noticeService;

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取消息详细", tags = "1.0.0")
    public JSON getConversationDetail(
            @ApiParam(name = "id", value = "消息ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<ExperimentMessage> result = new SingleResult<>();
        try {
            User user = SessionUtil.getUser(request.getSession());
            ExperimentMessage message;
            if (user.getType().equals(UserType.STUDENT.getCode())) {
                message = messageService.getById(id);
                messageService.updateMessage(message.getId(), 1);
            } else {
                Notice notice = noticeService.getById(id);
                message = changeNoticeToMessage(notice);
            }
            if (message == null) {
                result.returnError("消息Id错误");
                return (JSON) JSON.toJSON(result);
            }
            result.returnSuccess(message);
            return (JSON) JSON.toJSON(result);

        } catch (Exception e) {
            LOGGER.error("获取消息失败：", e.getMessage());
            result.returnError("获取消息失败");
            return (JSON) JSON.toJSON(result);
        }
    }

    private ExperimentMessage changeNoticeToMessage(Notice notice) {
        ExperimentMessage message = new ExperimentMessage();
        message.setHasRead(1);
        message.setContent(notice.getContent());
        message.setTitle(notice.getTitle());
        message.setCreatedDate(notice.getCreateTime());
        return message;
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "获取消息列表", tags = "1.0.0")
    public JSON getMessageList(HttpServletRequest request) {
        ListResult<MessageListDTO> result = new ListResult<>();
        try {
            int state = StringUtil.getInteger(request.getParameter("state"));
            int page = StringUtil.getInteger(request.getParameter("page"));
            User localUser = SessionUtil.getUser(request.getSession());
            int localUserId = localUser.getId();
            List<MessageListDTO> dtos;
            if (localUser.getType().equals(UserType.STUDENT.getCode())) {
                dtos = messageService.getReceiveListDTO(localUserId, state, page * PAGE_SIZE, PAGE_SIZE);
            } else {
                List<Notice> notices = noticeService.getMessageList(localUserId, page * PAGE_SIZE, PAGE_SIZE);
                dtos = getMessageListDTOFromNotice(notices);
            }
            result.returnSuccess(dtos);
        } catch (Exception e) {
            result.returnError("查询消息列表失败");
            LOGGER.error("list", e);
            return (JSON) JSON.toJSON(result);
        }
        return (JSON) JSON.toJSON(result);
    }

    private List<MessageListDTO> getMessageListDTOFromNotice(List<Notice> noticeList) {
        List<MessageListDTO> dtos = Lists.newArrayList();
        for (Notice notice : noticeList) {
            MessageListDTO dto = new MessageListDTO();
            dto.setId(notice.getId());
            dto.setTitle(notice.getTitle());
            dto.setContent(notice.getContent());
            dto.setCreatedDate(notice.getCreateTime());
            dto.setEpName(experimentService.getById(notice.getEpId()).getName());
            dto.setSendUserName(userService.getById(notice.getCreateId()).getName());
            dto.setHasRead(1);
            dtos.add(dto);
        }
        return dtos;
    }

    @RequestMapping(value = "/send", method = {RequestMethod.POST})
    @ResponseBody
    @ApiOperation(value = "发送消息", tags = "1.0.0")
    public JSON addMessage(
            @ApiParam(name = "epId", value = "课程ID", type = "Integer", required = true)@RequestParam("epId") int epId,
            @ApiParam(name = "content", value = "消息内容", type = "String", required = true) @RequestParam("content") String content, HttpServletRequest request) {
        SingleResult<String> result = new SingleResult<>();
        try {
            String title = request.getParameter("title");
            User user = SessionUtil.getUser(request.getSession());
            Notice notice = new Notice();
            notice.setTitle(title);
            notice.setContent(content);
            notice.setCreateId(user.getId());
            notice.setEpId(epId);
            notice.setCreateTime(new Date());
            noticeService.add(notice);
            List<Integer> studentIds = experimentUserService.getUserIdsByEpId(epId);
            // FIXME by JINGQ 消息添加换成批量新增
            List<ExperimentMessage> insertList = Lists.newArrayList();
            for (Integer id : studentIds) {
                ExperimentMessage message = new ExperimentMessage();
                message.setFromId(user.getId());
                message.setToId(id);
                message.setEpId(epId);
                message.setTitle(title);
                message.setContent(content);
                message.setHasRead(0);
                message.setCreatedDate(new Date());
                message.setConversationId(message.getConversationId());
                insertList.add(message);
            }
            if (CollectionUtils.isNotEmpty(insertList)) {
                messageService.batchInsert(insertList);
            }
            result.returnSuccess("发送成功");
            return (JSON) JSON.toJSON(result);

        } catch (Exception e) {
            LOGGER.error(" 发送消息失败：", e.getMessage());
            result.returnSuccess("发送消息失败");
            return (JSON) JSON.toJSON(result);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "删除消息", tags = "1.0.0")
    public JSON deleteMessage(
            @ApiParam(name = "id", value = "消息ID", type = "Integer", required = true) @RequestParam("id") Integer id, HttpServletRequest request) {
        SingleResult<Integer> result = new SingleResult<>();
        try {
            User user = SessionUtil.getUser(request.getSession());
            ExperimentMessage message;
            if (user.getType().equals(UserType.STUDENT)) {
                messageService.deleteById(id);
            } else {
                noticeService.deleteById(id);
            }

            return (JSON) JSON.toJSON(200);

        } catch (Exception e) {
            LOGGER.error("删除消息失败：", e.getMessage());
            result.returnError("删除消息失败");
            return (JSON) JSON.toJSON(result);
        }
    }
}

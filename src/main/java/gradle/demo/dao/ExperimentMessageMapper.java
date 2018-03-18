package gradle.demo.dao;

import gradle.demo.base.BaseMapperTemplate;
import gradle.demo.model.ExperimentMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by GJW on 2018/1/4.
 */
@Mapper
public interface ExperimentMessageMapper extends BaseMapperTemplate<ExperimentMessage> {

    /**
     * 添加message
     * @param message
     * @return
     */
    int addMessage(ExperimentMessage message);

    /**
     * 获取对话信息
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<ExperimentMessage> getConversitionDetail(@Param("conversationId") String conversationId,
                                                  @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取用户消息列表
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<ExperimentMessage> getConversitionList(@Param("userId") int userId,
                                                @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取消息数量
     * @param entityId
     * @param entityType
     * @return
     */
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    /**
     * 获取未读消息数
     * @param userId
     * @param conversationId
     * @return
     */
    int getUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    /**
     * 更新消息阅读状态
     * @param id
     * @param status
     * @return
     */
    int updateStatus(@Param("id") int id, @Param("status") int status);

    int batchInsert(@Param("list") List<ExperimentMessage> messages);

    List<ExperimentMessage> getReceiveMessageList(@Param("toId") int toId, @Param("hasRead") int hasRead, @Param("offset") int offset, @Param("limit") int limit);

}

package gradle.demo.service;

import gradle.demo.base.BaseServiceTemplate;
import gradle.demo.model.ExperimentMessage;
import gradle.demo.model.dto.MessageListDTO;

import java.util.List;

/**
 * Created by GJW on 2018/1/5.
 */
public interface ExperimentMessageService extends BaseServiceTemplate<ExperimentMessage> {

    int addMessage(ExperimentMessage message);

    int getUnreadCount(int userId, String conversationId);

    int batchInsert(List<ExperimentMessage> messages);

    List<ExperimentMessage> getReceiveList(int toId, int state, int offset, int limit);

    void updateMessage(int id, int hasRead);

    List<MessageListDTO> getReceiveListDTO(int toId, int state, int offset, int limit);
}

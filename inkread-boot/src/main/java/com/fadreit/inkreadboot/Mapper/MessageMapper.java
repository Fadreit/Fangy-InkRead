package com.fadreit.inkreadboot.Mapper;

import com.fadreit.inkreadboot.Entity.Message;
import com.fadreit.inkreadboot.Common.MessageResult;
import com.fadreit.inkreadboot.Entity.MessageLike;
import com.fadreit.inkreadboot.Vo.Message_getMessageByIdVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    /**
     * 添加留言
     * @param message
     */
    void addMessage(Message message);

    /**
     * 通过id获取留言视图
     * @param id
     * @return
     * 多表查询
     */
    MessageResult getMessageVo(Integer id);

    /**
     * 获取留言墙
     * @param sort
     * @return
     */
    List<MessageResult> getMessageWall(String sort);

    /**
     * 获取用户的留言点赞记录ids
     */
    List<Integer> queryLikeIds(Integer userId);

    /**
     * 获取所有留言
     * @return
     */
    List<MessageResult> PageListMessages();

    /**
     * 通过id获取留言Vo
     * @param id
     * @return
     */
    Message_getMessageByIdVo getMessageById(Integer id);

    /**
     * 通过id获取留言
     * @param id
     * @return
     */
    Message getMessage(Integer id);

    /**
     * 添加留言点赞记录
     * @param messageLike
     */
    void addMessageLike(MessageLike messageLike);

    /**
     * 更新留言点赞数
     * @param message
     */
    void updateMessageLikeCount(Message message);

    /**
     * 删除留言
     * @param id
     */
    void deleteMessage(Integer id);

    /**
     * 管理员获取留言
     * @param keyword
     * @return
     */
    List<MessageResult> adminPageListMessages(String keyword);

    /**
     * 获取留言点赞记录
     * @param id
     * @param userId
     * @return
     */
    MessageLike getMessageLike(Integer id, Integer userId);

    /**
     * 删除留言点赞记录
     * @param id
     */
    void deleteMessageLike(Integer id);

    /**
     * 获取留言的点赞记录ids
     * @param id
     * @return
     */
    List<Integer> queryMessageLikeIds(Integer id);
}

package com.fadreit.inkreadboot.Service;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Entity.Message;
import com.fadreit.inkreadboot.Vo.Message_adminPageListMessagesVo;

public interface MessageService {
    /**
     * 添加留言
     * @param message 留言信息
     * @return Result
     */
    Result addMessage(Message message);

    /**
     * 获取留言墙
     * @param sort 排序方式
     * @return Result
     */
    Result getMessageWall(String sort);

    /**
     * 获取所有留言
     * @param page 页码
     * @param size 每页数量
     * @return Result
     */
    Result PageListMessages(Integer page, Integer size);

    /**
     * 根据id获取留言
     * @param id 留言id
     * @return Result
     */
    Result getMessageById(Integer id);

    /**
     * 留言点赞
     * @param id 留言id
     * @return Result
     */
    Result likeMessage(Integer id);

    /**
     * 删除留言
     * @param id 留言id
     * @return Result
     */
    Result deleteMessage(Integer id);

    /**
     * 管理员获取留言列表
     * @param vo 留言列表参数
     * @return Result
     */
    Result adminPageListMessages(Message_adminPageListMessagesVo vo);

    /**
     * 留言取消点赞
     * @param id 留言id
     * @return Result
     */
    Result unlikeMessage(Integer id);
}

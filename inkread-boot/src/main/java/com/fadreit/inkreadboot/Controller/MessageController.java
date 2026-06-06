package com.fadreit.inkreadboot.Controller;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Entity.Message;
import com.fadreit.inkreadboot.Service.MessageService;
import com.fadreit.inkreadboot.Vo.Message_adminPageListMessagesVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用于处理留言相关的请求
 */

@Slf4j
@RestController
public class MessageController {

    //注入Service
    @Autowired
    private MessageService messageService;

    /**
     * 10.1 发布留言
     * 添加留言
     * @param message 留言信息
     * @return 响应结果
     */
    @PostMapping("/api/messages")
    public Result addMessage(@RequestBody Message message){
        //日志记录
        log.info("添加留言：{}", message);
        //调用Service
        return messageService.addMessage(message);
    }

    /**
     * 10.2 留言墙
     * 获取留言墙
     * @param sort 排序方式
     *             默认为 latest
     * @return 响应结果
     */
    @GetMapping("/api/messages/wall")
    public Result getMessageWall(@RequestParam String sort){
        //日志记录
        log.info("前端界面查看留言墙");
        //调用Service
        return messageService.getMessageWall(sort);
    }

    /**
     * 10.3 留言区-列表
     * 获取留言列表
     * @param page 页码
     * @param size 每页数量
     * @return 响应结果
     */
    @GetMapping("/api/messages")
    public Result PageListMessages(@RequestParam Integer page, @RequestParam Integer size){
        //日志记录
        log.info("获取留言列表");
        //调用Service
        return messageService.PageListMessages(page, size);
    }

    /**
     * 10.4 留言详情
     * 获取留言详情
     * @param id 留言id
     * @return 响应结果
     */
    @GetMapping("/api/messages/{id}")
    public Result getMessageById(@PathVariable Integer id){
        //日志记录
        log.info("获取留言：{}", id);
        //调用Service
        return messageService.getMessageById(id);
    }

    /**
     * 10.5 留言点赞
     * @param id 留言id
     * @return 响应结果
     */
    @PostMapping("/api/messages/{id}/like")
    public Result likeMessage(@PathVariable Integer id){
        //日志记录
        log.info("点赞留言：{}", id);
        //调用Service
        return messageService.likeMessage(id);
    }

    /**
     * 10.6 取消点赞
     * @param id 留言id
     * @return 响应结果
     */
    @DeleteMapping("/api/messages/{id}/like")
    public Result unlikeMessage(@PathVariable Integer id){
        //日志记录
        log.info("取消点赞留言：{}", id);
        //调用Service
        return messageService.unlikeMessage(id);
    }

    /**
     * 10.7 删除留言
     * @return 响应结果
     * 用户可删除自己留言
     * 管理员可删除所有留言
     */
    @DeleteMapping("/api/messages/{id}")
    public Result deleteMessage(Integer id){
        //日志记录
        log.info("删除留言{}",id);
        //调用Service
        return messageService.deleteMessage(id);
    }

    /**
     * 10.8 管理员获取留言列表
     * @param page 页码
     * @param size 每页数量
     * @return 响应结果
     */
    @GetMapping("/api/admin/messages")
    public Result adminPageListMessages(Message_adminPageListMessagesVo  vo){
        //日志记录
        log.info("管理员获取留言列表");
        //调用Service
        return messageService.adminPageListMessages(vo);
    }
}

package com.fadreit.inkreadboot.Service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import com.fadreit.inkreadboot.Common.PageResult;
import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Message_likeMessageDto;
import com.fadreit.inkreadboot.Entity.Message;
import com.fadreit.inkreadboot.Entity.MessageLike;
import com.fadreit.inkreadboot.Mapper.MessageMapper;
import com.fadreit.inkreadboot.Mapper.UserMapper;
import com.fadreit.inkreadboot.Service.MessageService;
import com.fadreit.inkreadboot.Common.MessageResult;
import com.fadreit.inkreadboot.Vo.Message_adminPageListMessagesVo;
import com.fadreit.inkreadboot.Vo.Message_getMessageByIdVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 留言服务实现类
 */

@Component
public class MessageServiceImpl implements MessageService {

    //注入Mapper
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Result addMessage(Message message) {
        //判断content是否为空
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return Result.error(400,"留言内容不能为空");
        }
        //先获取当前用户id
        Integer userId = StpUtil.getLoginIdAsInt();
        //封装message
        message.setUserId(userId);
        //设置时间戳
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        //调用Mapper
        messageMapper.addMessage(message);
        //封装返回数据
        MessageResult mam = messageMapper.getMessageVo(message.getId());
        //返回结果
        return Result.success(mam);
    }

    @Override
    public Result getMessageWall(String sort) {
        //先判断sort是否有值
        if (sort == null || sort.trim().isEmpty()) {
            sort = "latest";
        }
        //判断是否登录，如果登录，获取当前用户id
        Integer userId = null;
        List<Integer> likeIds = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsInt();
            //查询当前用户的点赞记录的留言记录ids
            likeIds = messageMapper.queryLikeIds(userId);
        }
        //调用Mapper
        List<MessageResult> list = messageMapper.getMessageWall(sort);
        //循环遍历留言记录，看用户是否点赞
        if (likeIds != null && !likeIds.isEmpty() && StpUtil.isLogin()){
            for (MessageResult message : list) {
                if (likeIds.contains(message.getId())) {
                    message.setLiked(true);
                }
            }
        }
        return Result.success(list);
    }

    @Override
    public Result PageListMessages(Integer page, Integer size) {
        //判断page和size
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        //开始分页
        PageHelper.startPage(page, size);
        //判断是否登录，如果登录，获取当前用户id
        Integer userId = null;
        List<Integer> likeIds = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsInt();
            //查询当前用户的点赞记录的留言记录ids
            likeIds = messageMapper.queryLikeIds(userId);
        }
        //查询并返回结果
        List<MessageResult> list = messageMapper.PageListMessages();
        //封装分页结果（在修改数据之前先获取分页信息）
        PageInfo<MessageResult> pageInfo = new PageInfo<>(list);
        //如果登录，则判断用户是否点赞
        if (likeIds != null && !likeIds.isEmpty() && StpUtil.isLogin()){
            for (MessageResult messageResult : list) {
                if (likeIds.contains(messageResult.getId())) {
                    messageResult.setLiked(true);
                }
            }
        }
        //返回结果
        PageResult<MessageResult> pageResult = new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages(),
                pageInfo.getList()
        );
        return Result.success(pageResult);
    }

    @Override
    public Result getMessageById(Integer id) {
        //判断是否登录，如果登录，获取当前用户id
        Integer userId = null;
        List<Integer> likeIds = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsInt();
            //查询当前用户的点赞记录的留言记录ids
            likeIds = messageMapper.queryLikeIds(userId);
        }
        Message_getMessageByIdVo message = messageMapper.getMessageById(id);
        if (likeIds != null && !likeIds.isEmpty() && StpUtil.isLogin()){
            if (likeIds.contains(message.getId())) {
                message.setLiked(true);
            }
        }
        return Result.success(message);
    }

    @Override
    @Transactional
    public Result likeMessage(Integer id) {
        //获取当前用户id
        Integer userId = StpUtil.getLoginIdAsInt();
        //判断留言是否存在
        Message message = messageMapper.getMessage(id);
        if (message == null || message.getIsDeleted() == 1) {
            return Result.error(404,"留言不存在");
        }
        //判断当前用户是否已经点赞
        List<Integer> likeIds = messageMapper.queryLikeIds(userId);
        if (likeIds != null && likeIds.contains(id)) {
            return Result.error(400,"您已经点赞过此留言");
        }
        //封装数据
        MessageLike messageLike = new MessageLike(null,id,userId,LocalDateTime.now());
        //调用Mapper
        //插入点赞记录
        messageMapper.addMessageLike(messageLike);
        //更新点赞数
        message.setLikeCount(message.getLikeCount() + 1);
        messageMapper.updateMessageLikeCount(message);
        //封装响应数据
        Message_likeMessageDto dto = new Message_likeMessageDto();
        dto.setMessageId(message.getId());
        dto.setLikeCount(message.getLikeCount());
        //返回结果
        return Result.success(dto);
    }

    @Override
    public Result deleteMessage(Integer id) {
        //获得当前用户id
        Integer userId = StpUtil.getLoginIdAsInt();
        //获得当前用户的权限
        String role = userMapper.queryUserById(userId).getRole();
        //判断当前留言是否存在
        Message message = messageMapper.getMessage(id);
        if (message == null || message.getIsDeleted() == 1) {
            return Result.error(404,"留言不存在");
        }
        //用户仅可删除自己留言
        if (!message.getUserId().equals(userId) && !role.equals("admin")) {
            return Result.error(403,"您没有权限删除此留言");
        }
        //先删除相关的点赞记录
        //查询点赞记录的ids
        List<Integer> messagelikeIds = messageMapper.queryMessageLikeIds(id);
        for (Integer messageLikeId : messagelikeIds) {
            messageMapper.deleteMessageLike(messageLikeId);
        }
        //删除留言
        messageMapper.deleteMessage(id);
        //返回结果
        return Result.success();
    }

    @Override
    public Result adminPageListMessages(Message_adminPageListMessagesVo vo) {
        //开始分页
        PageHelper.startPage(vo.getPage(), vo.getSize());
        //查询并返回结果
        List<MessageResult> list = messageMapper.adminPageListMessages(vo.getKeyword());
        //封装分页结果
        PageInfo<MessageResult> pageInfo = new PageInfo<>(list);
        PageResult<MessageResult> pageResult = new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages(),
                pageInfo.getList()
        );
        return Result.success(pageResult);
    }

    @Override
    public Result unlikeMessage(Integer id) {
        //获取当前用户id
        Integer userId = StpUtil.getLoginIdAsInt();
        //判断留言是否存在
        Message message = messageMapper.getMessage(id);
        if (message == null || message.getIsDeleted() == 1) {
            return Result.error(404,"留言不存在");
        }
        //判断当前用户是否已经取消点赞
        List<Integer> likeIds = messageMapper.queryLikeIds(userId);
        if (likeIds == null || !likeIds.contains(id)) {
            return Result.error(400,"您没有点赞过此留言");
        }
        //封装数据
        //获取点赞留言记录
        MessageLike messageLike = messageMapper.getMessageLike(id,userId);
        //删除点赞记录
        messageMapper.deleteMessageLike(messageLike.getId());
        //更新点赞数
        message.setLikeCount(message.getLikeCount() - 1);
        messageMapper.updateMessageLikeCount(message);
        //封装响应数据
        Message_likeMessageDto dto = new Message_likeMessageDto();
        dto.setMessageId(message.getId());
        dto.setLikeCount(message.getLikeCount());
        return Result.success(dto);
    }
}

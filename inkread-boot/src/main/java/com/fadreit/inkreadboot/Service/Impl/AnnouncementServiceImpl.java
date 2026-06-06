package com.fadreit.inkreadboot.Service.Impl;

import com.fadreit.inkreadboot.Common.PageResult;
import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Announce_GetAllRequest;
import com.fadreit.inkreadboot.Dto.Announcement_AddRequest;
import com.fadreit.inkreadboot.Dto.Announcement_PageListRequest;
import com.fadreit.inkreadboot.Entity.Announcement;
import com.fadreit.inkreadboot.Mapper.AnnouncementMapper;
import com.fadreit.inkreadboot.Service.AnnouncementService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AnnouncementServiceImpl implements AnnouncementService {

    //注入Mapper
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public Result PageListAnnouncements(Announcement_PageListRequest request) {
        //开始分页
        PageHelper.startPage(request.getPage(), request.getSize());
        //查询并返回结果
        List<Announcement> list = announcementMapper.PageListAnnouncements();
        //封装分页结果
        Page<Announcement> page = (Page<Announcement>) list;
        PageResult<Announcement> pageResult = new PageResult<>(
                page.getTotal(),
                page.getPageNum(),
                page.getPageSize(),
                page.getPages(),
                page.getResult()
        );
        //返回结果
        return Result.success(pageResult);
    }

    @Override
    public Result GetAnnouncementById(Integer id) {
        //查询
        Announcement announcement = announcementMapper.GetAnnouncementById(id);
        if (announcement == null) {
            return Result.error(404, "公告不存在");
        }
        //判断是否启用或删除
        if (announcement.getStatus() == 0 || announcement.getIsDeleted() == 1) {
            return Result.error(404, "公告不存在");
        }
        return Result.success(announcement);
    }

    @Override
    public Result addAnnouncement(Announcement_AddRequest request) {
        //判断标题和内容是否为空
        if (request.getTitle() == null || request.getTitle().isEmpty() || request.getContent() == null || request.getContent().isEmpty()){
            return Result.error(400, "标题和内容不能为空");
        }
        //添加
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setStatus(request.getStatus());
        //设置时间
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setUpdatedAt(LocalDateTime.now());
        //添加
        announcementMapper.addAnnouncement(announcement);
        //查询回显
        Announcement new_announcement = announcementMapper.GetAnnouncementById(announcement.getId());
        //返回结果
        return Result.success(new_announcement);
    }

    @Override
    public Result getAllAnnouncements(Announce_GetAllRequest request) {
        //开始分页
        PageHelper.startPage(request.getPage(), request.getSize());
        //查询并返回结果
        List<Announcement> list = announcementMapper.getAllAnnouncements(request);
        //封装分页结果
        Page<Announcement> page = (Page<Announcement>) list;
        PageResult<Announcement> pageResult = new PageResult<>(
                page.getTotal(),
                page.getPageNum(),
                page.getPageSize(),
                page.getPages(),
                page.getResult()
        );
        //返回结果
        return Result.success(pageResult);
    }

    @Override
    public Result updateAnnouncement(Integer id, Announcement_AddRequest request) {
        //获取当前的公告
        Announcement announcement = announcementMapper.GetAnnouncementById(id);
        if (announcement == null) {
            return Result.error(404, "公告不存在");
        }
        if (request.getTitle() !=  null){
            announcement.setTitle(request.getTitle());
        }
        if (request.getContent() != null){
            announcement.setContent(request.getContent());
        }
        if (request.getStatus() != null){
            announcement.setStatus(request.getStatus());
        }
        announcement.setUpdatedAt(LocalDateTime.now());
        announcementMapper.updateAnnouncement(announcement);
        return Result.success(announcement);
    }

    @Override
    public Result deleteAnnouncement(Integer id) {
        //获取当前公告
        Announcement announcement = announcementMapper.GetAnnouncementById(id);
        if (announcement == null) {
            return Result.error(404, "公告不存在");
        }
        if (announcement.getIsDeleted() == 1) {
            return Result.error(404, "公告已删除");
        }
        announcementMapper.deleteAnnouncement(id);
        return Result.success();
    }

    @Override
    public Result updateAnnouncementStatus(Integer id, Integer status) {
        //获取当前公告
        Announcement announcement = announcementMapper.GetAnnouncementById(id);
        if (announcement == null) {
            return Result.error(404, "公告不存在");
        }
        if (announcement.getIsDeleted() == 1) {
            return Result.error(404, "公告已删除");
        }
        announcementMapper.updateAnnouncementStatus(id, status);
        announcement.setStatus(status);
        return Result.success(announcement);
    }
}

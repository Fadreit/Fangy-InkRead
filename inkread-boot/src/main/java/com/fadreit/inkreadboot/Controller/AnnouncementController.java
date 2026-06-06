package com.fadreit.inkreadboot.Controller;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Announce_GetAllRequest;
import com.fadreit.inkreadboot.Dto.Announcement_AddRequest;
import com.fadreit.inkreadboot.Dto.Announcement_PageListRequest;
import com.fadreit.inkreadboot.Service.AnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController

public class AnnouncementController {

    //注入Service
    private AnnouncementService announcementService;

    /**
     * 获取所有公告
     * @return
     */
    @GetMapping("/api/announcements")
    public Result PageListAnnouncements(Announcement_PageListRequest request){
        //日志记录
        log.info("前台查询公告");
        //调用Service
        return announcementService.PageListAnnouncements(request);
    }

    /**
     * 获取公告详情
     * @return
     */
    @GetMapping("/api/announcements/{id}")
    public Result GetAnnouncementById(@PathVariable Integer id){
        //日志记录
        log.info("前台查询公告详情");
        //调用Service
        return announcementService.GetAnnouncementById(id);
    }

    /**
     * 添加公告
     * @return
     */
    @PostMapping("/api/admin/announcements")
    public Result addAnnouncement(@RequestBody Announcement_AddRequest request){
        //日志记录
        log.info("后台添加公告");
        //调用Service
        return announcementService.addAnnouncement(request);
    }

    /**
     * 获取所有公告
     * @return
     */
    @GetMapping("/api/admin/announcements")
    public Result getAllAnnouncements(Announce_GetAllRequest  request){
        //日志记录
        log.info("后台获取所有公告");
        //调用Service
        return announcementService.getAllAnnouncements(request);
    }

    /**
     * 修改公告
     * @return
     */
    @PutMapping("/api/admin/announcements/{id}")
    public Result updateAnnouncement(@PathVariable Integer id, Announcement_AddRequest  request){
        //日志记录
        log.info("后台修改公告");
        //调用Service
        return announcementService.updateAnnouncement(id, request);
    }

    @DeleteMapping("/api/admin/announcements/{id}")
    public Result deleteAnnouncement(@PathVariable Integer id){
        //日志记录
        log.info("后台删除公告{}",id);
        //调用Service
        return announcementService.deleteAnnouncement(id);
    }

    @PutMapping("/api/admin/announcements/{id}/status")
    public Result updateAnnouncementStatus(@PathVariable Integer id, @RequestParam Integer status){
        //日志记录
        log.info("后台修改公告{}的状态为{}",id,status);
        //调用Service
        return announcementService.updateAnnouncementStatus(id, status);
    }
}

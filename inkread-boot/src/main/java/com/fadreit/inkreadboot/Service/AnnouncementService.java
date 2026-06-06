package com.fadreit.inkreadboot.Service;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Announce_GetAllRequest;
import com.fadreit.inkreadboot.Dto.Announcement_AddRequest;
import com.fadreit.inkreadboot.Dto.Announcement_PageListRequest;

public interface AnnouncementService {

    /**
     * 获取所有公告
     * @return
     */
    Result PageListAnnouncements(Announcement_PageListRequest request);

    /**
     * 根据id获取公告
     * @param id
     * @return
     */
    Result GetAnnouncementById(Integer id);

    /**
     * 添加公告
     * @param request
     * @return
     */
    Result addAnnouncement(Announcement_AddRequest request);

    /**
     * 获取所有公告
     * @return
     */
    Result getAllAnnouncements(Announce_GetAllRequest request);

    /**
     * 修改公告
     * @param id
     * @param request
     * @return
     */
    Result updateAnnouncement(Integer id, Announcement_AddRequest request);

    /**
     * 删除公告
     * @param id
     * @return
     */
    Result deleteAnnouncement(Integer id);

    /**
     * 修改公告状态
     * @param id
     * @param status
     * @return
     */
    Result updateAnnouncementStatus(Integer id, Integer status);
}

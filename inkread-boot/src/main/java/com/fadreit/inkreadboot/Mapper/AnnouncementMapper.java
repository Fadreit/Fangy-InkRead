package com.fadreit.inkreadboot.Mapper;

import com.fadreit.inkreadboot.Entity.Announcement;
import com.fadreit.inkreadboot.Dto.Announce_GetAllRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    /**
     * 获取所有公告
     * @return
     */
    List<Announcement> PageListAnnouncements();

    /**
     * 根据id获取公告
     * @param id
     * @return
     */
    Announcement GetAnnouncementById(Integer id);

    /**
     * 添加公告
     * @param announcement
     */
    void addAnnouncement(Announcement announcement);

    /**
     * 获取所有公告
     * @return
     */
    List<Announcement> getAllAnnouncements(Announce_GetAllRequest request);

    /**
     * 修改公告
     * @param announcement
     */
    void updateAnnouncement(Announcement announcement);

    /**
     * 删除公告
     * @param id
     */
    void deleteAnnouncement(Integer id);

    /**
     * 修改公告状态
     * @param id
     * @param status
     */
    void updateAnnouncementStatus(@Param("id") Integer id, @Param("status") Integer status);
}

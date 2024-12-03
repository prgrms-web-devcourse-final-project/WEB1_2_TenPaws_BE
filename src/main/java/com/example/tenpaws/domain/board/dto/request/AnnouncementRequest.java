package com.example.tenpaws.domain.board.dto.request;

import com.example.tenpaws.domain.admin.entity.Admin;
import com.example.tenpaws.domain.board.entity.Announcement;
import com.example.tenpaws.domain.board.entity.AnnouncementCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AnnouncementRequest {
    private AnnouncementCategory category;
    private String title;
    private String content;

    public Announcement toEntity(Admin admin) {
        return Announcement.builder()
                .admin(admin)
                .category(category)
                .title(title)
                .content(content)
                .viewCount(0L)
                .build();
    }
}

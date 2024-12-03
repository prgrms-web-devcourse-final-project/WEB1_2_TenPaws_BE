package com.example.tenpaws.domain.board.entity;

import com.example.tenpaws.domain.admin.entity.Admin;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(name = "category", nullable = false)
    private AnnouncementCategory category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @CreatedDate
    private LocalDate created_at;

    @Builder
    public Announcement(Long id, Admin admin, AnnouncementCategory category, String title, String content, Long viewCount) {
        this.id = id; // 테스트용 ID
        this.admin = admin;
        this.category = category;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

    public void update(AnnouncementCategory category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }
}

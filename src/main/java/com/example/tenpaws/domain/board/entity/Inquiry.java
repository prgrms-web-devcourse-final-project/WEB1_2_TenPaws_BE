package com.example.tenpaws.domain.board.entity;

import com.example.tenpaws.domain.shelter.entity.Shelter;
import com.example.tenpaws.domain.user.entity.User;
import com.example.tenpaws.global.exception.BaseException;
import com.example.tenpaws.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @CreatedDate
    private LocalDate created_at;

    @Builder
    public Inquiry(User user, Shelter shelter, String title, String content, Long viewCount) {
        this.user = user;
        this.shelter = shelter;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public String getWriterEmail() {
        if (user != null) {
            return user.getEmail();
        }
        if (shelter != null) {
            return shelter.getEmail();
        }
        throw new BaseException(ErrorCode.WRITER_NOT_FOUND);
    }
}
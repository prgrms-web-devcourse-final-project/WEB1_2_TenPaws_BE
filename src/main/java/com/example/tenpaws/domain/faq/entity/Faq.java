package com.example.tenpaws.domain.faq.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "faqs")
@Getter
@NoArgsConstructor
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Faq parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Faq> children;

    @Builder
    public Faq(Long id, String content, Faq parent) {
        this.id = id;
        this.content = content;
        this.parent = parent;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}

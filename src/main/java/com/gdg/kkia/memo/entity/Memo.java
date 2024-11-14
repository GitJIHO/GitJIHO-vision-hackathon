package com.gdg.kkia.memo.entity;

import com.gdg.kkia.diary.entity.Diary;
import com.gdg.kkia.member.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Setter
    private String content;
    @NotNull
    @Setter
    @CreatedDate
    private LocalDateTime writtenDatetime;
    @ManyToOne
    @JoinColumn(name = "member_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public Memo(String content, Member member) {
        this.content = content;
        this.member = member;
    }

}
package qna.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import qna.exception.NotFoundException;
import qna.exception.UnAuthorizedException;

import java.time.LocalDateTime;
import java.util.Objects;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "answer")
public class Answer {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY) // 자동 생성 전략을 사용하겠다는 어노테이션
    private Long id;

    @Column(nullable = true)
    private String contents;
    private boolean deleted = false;

    @CreatedDate //
    @Column(nullable = false)
    private LocalDateTime createAT;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAT;

    @ManyToOne
    @JoinColumn(nullable = false)
    Question question;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User writer;


    public Answer(User writer, Question question, String contents) {
        this(null, writer, question, contents);
    }

    public Answer(Long id, User writer, Question question, String contents) {
        this.id = id;

        if (Objects.isNull(writer)) {
            throw new UnAuthorizedException();
        }

        if (Objects.isNull(question)) {
            throw new NotFoundException();
        }

        this.writer = writer;
        this.question = question;
        this.contents = contents;
    }

    public boolean isOwner(User writer) {
        return this.writer.getId().equals(writer.getId());
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWriterId() {
        return writer.getId();
    }

    public String getContents() {
        return contents;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "contents='" + contents + '\'' +
                ", deleted=" + deleted +
                ", question=" + question +
                ", writer=" + writer +
                ", id=" + id +
                '}';
    }
}

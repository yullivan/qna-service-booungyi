package qna.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;
import qna.exception.CannotDeleteException;
import qna.exception.NotFoundException;

import java.io.Writer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성 전략을 사용하겠다는 어노테이션
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true)
    private String contents;

    @Column(nullable = false)
    private boolean deleted = false;

    @CreatedDate
    private LocalDateTime createAT;

    @LastModifiedDate
    private LocalDateTime updateAT;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User writer;

    public User getWriter() {
        return writer;
    }

    @OneToMany(mappedBy = "question")
    private List<Answer> answer;

    public Question(String title, String contents) {
        this(null, title, contents);
    }

    public Question(Long id, String title, String contents) {
        this.id = id;
        this.title = title;
        this.contents = contents;
    }

    public Question writeBy(User writer) {
        this.writer = writer;
        return this;
    }

    public boolean isOwner(User writer) {
        return this.writer.getId().equals(writer.getId());
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
    }

    public Long getId() {
        return id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    //todo 작성자의 아이디로 질문을 찾은뒤에 답변도 함께 삭제진행
    public DeleteHistory deletedQuestion(List<Answer> answers,User writer) {
        if (!isOwner(writer)) {
            throw new IllegalArgumentException("질문을 삭제할 권한이 없습니다.");
        }
        if (answers==null || answers.isEmpty()) {
            setDeleted(true);
            return new DeleteHistory(
                    ContentType.QUESTION,
                    getId(),
                    writer,
                    LocalDateTime.now());
        } else {
            for (Answer answer : answers) {
                if (!answer.isOwner(writer)) {
                    throw new IllegalArgumentException("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.");
                }
            }
            setDeleted(true);
          return new DeleteHistory(
                    ContentType.QUESTION,
                    getId(),
                    writer,
                    LocalDateTime.now());
        }
    }
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writer=" + writer +
                ", deleted=" + deleted +
                '}';
    }
}

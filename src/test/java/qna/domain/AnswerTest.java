package qna.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {
    public static final Answer A1 = new Answer(UserTest.DORAEMON, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SPONGEBOB, QuestionTest.Q1, "Answers Contents2");

    @Test
    void deletedAnswerTest() {
        DeleteHistory deleteHistory = A1.deleteAnswer();
        assertThat(A1.isDeleted()).isTrue();
        System.out.println("A1.isDeleted() = " + A1.isDeleted());
        assertThat(deleteHistory.getContentType()).isEqualTo(ContentType.ANSWER);
        assertThat(deleteHistory.getUser()).isEqualTo(A1.getWriter());
    }

    @Test
    void deletedAnswerTest2() {
        DeleteHistory deleteHistory = A2.deleteAnswer();
        assertThat(A2.isDeleted()).isTrue();
        System.out.println("A2.isDeleted() = " + A2.isDeleted());
        assertThat(deleteHistory.getContentType()).isEqualTo(ContentType.ANSWER);
        assertThat(deleteHistory.getUser()).isEqualTo(A2.getWriter());
    }
}

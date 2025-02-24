package qna.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QuestionTest {

    public static final Question Q1 = new Question("title1", "contents1").writeBy(UserTest.DORAEMON);
    public static final Question Q2 = new Question("title2", "contents2").writeBy(UserTest.SPONGEBOB);

    public static final List<Answer> A1 =
            List.of(new Answer(UserTest.DORAEMON, Q1, "Answers Contents1"),
                    new Answer(UserTest.DORAEMON, Q1, "Answers Contents2"),
                    new Answer(UserTest.DORAEMON, Q1, "Answers Contents3"));

    public static final List<Answer> A2 =
            List.of(new Answer(UserTest.DORAEMON, Q1, "Answers Contents1"),
                    new Answer(UserTest.SPONGEBOB, Q1, "Answers Contents2"),
                    new Answer(UserTest.DORAEMON, Q1, "Answers Contents3"));

    @Test
    void deletedQuestion1() {
        DeleteHistory deleteHistory = Q1.deletedQuestion(A1, UserTest.DORAEMON);
        assertThat(Q1.isDeleted()).isTrue();

        System.out.println("Q1.isDeleted() = " + Q1.isDeleted());
        assertThat(deleteHistory.getContentType()).isEqualTo(ContentType.QUESTION);
        assertThat(deleteHistory.getUser()).isEqualTo(UserTest.DORAEMON);
    }

    @Test
    void deletedQuestion2() {
        // 예외가 발생하는지 확인하는 코드
        assertThatThrownBy(() -> Q2.deletedQuestion(A2, UserTest.SPONGEBOB))
                .isInstanceOf(IllegalArgumentException.class) // 예외의 타입을 확인
                .hasMessageContaining("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다."); // 예외 메시지가 포함되어 있는지 확인

        // 예외 발생 후 Q2의 상태가 변경되지 않았는지 확인
        assertThat(Q2.isDeleted()).isFalse();
        System.out.println("Q2.isDeleted() = " + Q2.isDeleted());
    }

    @Test
    void deleteQuestion3() {
        DeleteHistory deleteHistory = Q1.deletedQuestion(null, UserTest.DORAEMON);
        assertThat(Q1.isDeleted()).isTrue();
    }
}

package qna.service;

import org.springframework.stereotype.Service;
import qna.domain.*;
import qna.exception.CannotDeleteException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QnaService {

    QuestionRepository qusetionRepositoty;
    DeleteHistoryRepository deleteHistoryRepository;
    AnswerRepository answerRepository;
    UserRepository userRepository;

    public QnaService(QuestionRepository qusetionRepositoty,
                      DeleteHistoryRepository deleteHistoryRepository,
                      AnswerRepository answerRepository,
                      UserRepository userRepository
    ) {
        this.qusetionRepositoty = qusetionRepositoty;
        this.deleteHistoryRepository = deleteHistoryRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;

    }
    // Todo 조건1. 데이터의 상태를 삭제상태 deleted = true 로 변경한다
    // Todo 조건2. 로그인 사용자와 질문한 사람이 같은경우만 삭제가능
    // TOdo 조건3. 질문에 답변이 없는경우 삭제가능
    // Todo 조건4. 답변이 있더라도 모든 답변들의 작성자가 질문 작성자와 같은경우에는 삭제가능
    // Todo 조건5. 질문을 삭제할때 답변도 함께 삭제해야하며 삭제할떄도 삭제 상태(dleted)를 true 로 변경한다.
    // Todo 조건6. 질문자가 삭제한 질문 또는 답변에 대한 삭제 이력을 DeleteHistory 에 저장한다.

    public String deleteQuestion(Long userId, long questionId) {
        //Todo 0. 사용자가 존재하는지 확인 ..굳이 할 필요가있을까?
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        //Todo 1. 삭제할 question의 id 를 넣고 존재하는지 확인.
        Question question = qusetionRepositoty.findById(questionId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 질문 입니다."));
        //Todo 2. 질문자와 로그인한 사용자가 같은지 확인하고 존재하는 질문 확인
        if (!question.isOwner(user)) {
            throw new IllegalArgumentException("질문자와 로그인한 사용자가 다릅니다.");
        }

        //Todo 3. 질문에 답변이 있는지 확인하고 없을경우 바로 삭제 진행 그리고 있을경우 답변자와 질문자가 같은지
        // 확인하고 같을경우 삭제 진행 다를경우 '다른 user 가 답변을 달아서 삭제 가 불가능 합니다.' return
        List<Answer> answers = answerRepository.findByQuestionIdAndDeletedFalse(questionId);
        if (answers.isEmpty()) {
            //TOdo 답변이 없어서 바로 삭제 가능.
            question.setDeleted(true);
            qusetionRepositoty.save(question);
        } else {
            //Todo 답변과 질문자의 id 가 같은지 각각 확인후 다른게 하나라도 있을경우 질문 삭제 불가
            for (Answer answer : answers) {
                if (!answer.isOwner(user)) {
                    throw new CannotDeleteException("다른 user 가 답변을 달아서 삭제 가 불가능 합니다.");
                }
            }
        }
            //Todo 질문과 답변 삭제 이력에 대한 정보를 DeleteHistory 에 저장
        deleteHistoryRepository.save(new DeleteHistory(
                ContentType.QUESTION,
                questionId,
                user,
                LocalDateTime.now()
        ));
        for (Answer answer : answers) {
            deleteHistoryRepository.save(new DeleteHistory(
                    ContentType.ANSWER,
                    answer.getId(),
                    user,
                    LocalDateTime.now()
            ));
            answer.setDeleted(true);
            answerRepository.save(answer);
        }
        question.setDeleted(true);
        return "성공적으로 삭제됨.";
    }
}

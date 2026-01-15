package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class TestServiceImplTest {

    @Autowired
    private TestServiceImpl testService;

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    private Question question;
    private List<Question> questions;
    private Student student;

    @BeforeEach
    void setUp() {
        question = new Question(
                "TestQuestion?",
                List.of(
                        new Answer("Answer1", true),
                        new Answer("Answer2", false)
                )
        );
        questions = List.of(question);
        student = new Student("Test", "Student");
    }

    @Test
    void shouldCountCorrectAnswer() {
        when(questionDao.findAll()).thenReturn(questions);
        mockUserAnswer(1);

        TestResult result = testService.executeTestFor(student);

        verifyPrintedQuestionAndAnswers(question);

        assertThat(result.getRightAnswersCount()).isEqualTo(1);
        assertThat(result.getAnsweredQuestions()).hasSize(1);
        assertThat(result.getAnsweredQuestions().get(0).text()).isEqualTo(question.text());
    }

    @Test
    void shouldCountWrongAnswerAsZero() {
        when(questionDao.findAll()).thenReturn(questions);
        mockUserAnswer(2);

        TestResult result = testService.executeTestFor(student);

        verifyPrintedQuestionAndAnswers(question);

        assertThat(result.getRightAnswersCount()).isZero();
        assertThat(result.getAnsweredQuestions()).hasSize(1);
        assertThat(result.getAnsweredQuestions().get(0).text()).isEqualTo(question.text());
    }

    private void mockUserAnswer(int answerNumber) {
        when(ioService.readIntForRangeWithPromptLocalized(
                eq(1),
                eq(question.answers().size()),
                eq("TestService.question.enter.answer.number"),
                eq("TestService.question.enter.answer.number.error")
        )).thenReturn(answerNumber);
    }

    private void verifyPrintedQuestionAndAnswers(Question question) {
        verify(ioService).printLine("");
        verify(ioService).printLineLocalized("TestService.answer.the.questions");

        ArgumentCaptor<String> formatCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);

        verify(ioService, atLeast(1)).printFormattedLine(formatCaptor.capture(), argsCaptor.capture());

        StringBuilder printed = new StringBuilder();
        for (int i = 0; i < formatCaptor.getAllValues().size(); i++) {
            String fmt = formatCaptor.getAllValues().get(i);
            Object[] args = argsCaptor.getAllValues().get(i);
            printed.append(String.format(fmt + "%n", args));
        }

        String output = printed.toString();
        assertThat(output).contains(question.text());
        for (int i = 0; i < question.answers().size(); i++) {
            var ans = question.answers().get(i);
            assertThat(output).contains(String.format("    %d) %s", i + 1, ans.text()));
        }
    }
}
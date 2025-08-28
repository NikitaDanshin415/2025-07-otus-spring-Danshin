package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestServiceImplTest {

    private LocalizedIOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;
    private List<Answer> answers;
    private Question question;
    private List<Question> questions;
    private Student student;

    @BeforeEach
    void setUp() {
        ioService = mock(LocalizedIOServiceImpl.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);

        question = new Question("TestQuestion?",
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

        verifyOutputContainsQuestionAndAnswers(question);

        assertEquals(1, result.getRightAnswersCount());
        assertEquals(1, result.getAnsweredQuestions().size());
        assertEquals(question.text(), result.getAnsweredQuestions().get(0).text());
    }

    @Test
    void shouldCountWrongAnswerAsZero() {
        when(questionDao.findAll()).thenReturn(questions);
        mockUserAnswer(2);

        TestResult result = testService.executeTestFor(student);

        verifyOutputContainsQuestionAndAnswers(question);

        assertEquals(0, result.getRightAnswersCount());
        assertEquals(1, result.getAnsweredQuestions().size());
        assertEquals(question.text(), result.getAnsweredQuestions().get(0).text());
    }
    

    private void mockUserAnswer(int answerNumber) {
        when(ioService.readIntForRangeWithPrompt(
                eq(1),
                eq(question.answers().size()),
                anyString(),
                anyString()
        )).thenReturn(answerNumber);
    }

    private void verifyOutputContainsQuestionAndAnswers(Question question) {
        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");

        ArgumentCaptor<String> formatCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);

        verify(ioService, atLeast(1)).printFormattedLine(formatCaptor.capture(), argsCaptor.capture());

        StringBuilder printed = new StringBuilder();
        for (int i = 0; i < formatCaptor.getAllValues().size(); i++) {
            printed.append(String.format(formatCaptor.getAllValues().get(i) + "%n", argsCaptor.getAllValues().get(i)));
        }

        String output = printed.toString();
        assertTrue(output.contains(question.text()));
        for (int i = 0; i < question.answers().size(); i++) {
            Answer ans = question.answers().get(i);
            assertTrue(output.contains(String.format("    %d) %s", i + 1, ans.text())));
        }
    }
}
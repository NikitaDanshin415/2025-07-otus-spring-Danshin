package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;
    private List<Answer> answers;
    private String question;
    private List<Question> questions;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
        question = "Is Java platform-independent?";

        answers = List.of(
                new Answer("Yes", true),
                new Answer("No", false)
        );
        questions = List.of(
                new Question(question, answers)
        );
    }

    @Test
    void shouldPrintQuestionsAndAnswers() {
        when(questionDao.findAll()).thenReturn(questions);

        testService.executeTest();
        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");

        var formatCaptor = ArgumentCaptor.forClass(String.class);
        var argsCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(
                ioService,
                atLeast(1)).printFormattedLine(formatCaptor.capture(), argsCaptor.capture()
        );

        var printed = new StringBuilder();
        for (int i = 0; i < formatCaptor.getAllValues().size(); i++) {
            printed.append(String.format(formatCaptor.getAllValues().get(i) + "%n", argsCaptor.getAllValues().get(i)));
        }

        String result = printed.toString();
        assertTrue(result.contains(question));
        assertTrue(result.contains("    Yes"));
        assertTrue(result.contains("    No"));
    }
}
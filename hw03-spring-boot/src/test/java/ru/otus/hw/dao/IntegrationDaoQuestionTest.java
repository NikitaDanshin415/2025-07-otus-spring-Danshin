package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class IntegrationDaoQuestionTest {
    private TestFileNameProvider fileNameProvider;
    private CsvQuestionDao questionDao;

    @BeforeEach
    public void setUp() {
        fileNameProvider = Mockito.mock(TestFileNameProvider.class);
        when(fileNameProvider.getTestFileName()).thenReturn("testQuestions.csv");
        questionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    public void findAllShouldReadQuestionsFromCsv() {
        List<Question> questions = questionDao.findAll();
        assertThat(questions).isNotEmpty();

        Question first = questions.get(0);
        assertThat(first.text()).isNotBlank();
        assertThat(first.answers()).isNotEmpty();
    }

    @Test
    public void questionFileNotExist() {
        when(fileNameProvider.getTestFileName()).thenReturn("nonexistent.csv");

        assertThrows(
                QuestionReadException.class,
                () -> questionDao.findAll()
        );
    }
}

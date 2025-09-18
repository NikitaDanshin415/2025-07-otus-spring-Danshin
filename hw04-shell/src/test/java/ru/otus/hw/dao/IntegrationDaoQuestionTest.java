package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CsvQuestionDao.class})
@ActiveProfiles("test")
class IntegrationDaoQuestionTest {

    @MockitoBean
    private TestFileNameProvider fileNameProvider;

    @Autowired
    private CsvQuestionDao questionDao;

    @BeforeEach
    void setUp() {
        when(fileNameProvider.getTestFileName()).thenReturn("testQuestions.csv");
    }

    @Test
    void findAllShouldReadQuestionsFromCsv() {
        List<Question> questions = questionDao.findAll();
        assertThat(questions).isNotEmpty();

        Question first = questions.get(0);
        assertThat(first.text()).isNotBlank();
        assertThat(first.answers()).isNotEmpty();
    }

    @Test
    void questionFileNotExist() {
        when(fileNameProvider.getTestFileName()).thenReturn("nonexistent.csv");

        assertThrows(QuestionReadException.class, () -> questionDao.findAll());
    }
}
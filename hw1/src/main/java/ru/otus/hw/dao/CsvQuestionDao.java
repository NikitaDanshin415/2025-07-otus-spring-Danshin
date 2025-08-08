package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try (var is = getClass().getClassLoader().getResourceAsStream(fileNameProvider.getTestFileName())) {
            if (is == null) {
                throw new QuestionReadException("File not found:" + fileNameProvider.getTestFileName());
            }

            Reader reader = new InputStreamReader(is);
            List<QuestionDto> questionDtos = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();

            return questionDtos.stream()
                    .map(QuestionDto::toDomainObject)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new QuestionReadException("Error when reading the CSV file: " + e.getMessage(), e);
        }
    }
}

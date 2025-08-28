package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            printQuestionWithAnswers(question);
            boolean isAnswerValid = getAndValidateUserAnswer(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private int getNumberOfCorrectAnswer(Question question) {
        var answers = question.answers();
        return IntStream.range(0, answers.size())
                .filter(i -> answers.get(i).isCorrect())
                .findFirst()
                .orElseThrow(() -> new QuestionReadException(
                        ioService.getMessage("TestService.error.no.csv.file")
                ));
    }

    private int getCountOfAnswers(Question question) {
        return question.answers().size();
    }

    private void printQuestionWithAnswers(Question question) {
        printQuestion(question);
        printAnswers(question);
    }

    private void printQuestion(Question question) {
        ioService.printFormattedLine("%s", question.text());
    }

    private void printAnswers(Question question) {
        IntStream.range(0, question.answers().size())
                .forEach(i -> ioService.printFormattedLine("    %d) %s",
                        i + 1, question.answers().get(i).text()));
    }

    private int getUserAnswer(Question question) {
        int countOfAnswers = getCountOfAnswers(question);

        return ioService.readIntForRangeWithPromptLocalized(
                1,
                countOfAnswers,
                ioService.getMessage("TestService.question.enter.answer.number",1, countOfAnswers),
                ioService.getMessage("TestService.question.enter.answer.number.error",1, countOfAnswers)
        );
    }

    private boolean getAndValidateUserAnswer(Question question) {
        int numberOfValidAnswer = getNumberOfCorrectAnswer(question) + 1;
        int userAnswer = getUserAnswer(question);

        return numberOfValidAnswer == userAnswer;
    }
}

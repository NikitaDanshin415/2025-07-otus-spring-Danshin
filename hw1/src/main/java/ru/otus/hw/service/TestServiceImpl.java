package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;


@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        List<Question> questions = questionDao.findAll();
        printQuestionsWithAnswers(questions);
    }

    private void printQuestionsWithAnswers(List<Question> questions) {
        questions.forEach(question -> {
            printQuestion(question);
            printAnswers(question);
        });
    }

    private void printQuestion(Question question) {
        ioService.printFormattedLine("%s", question.text());
    }

    private void printAnswers(Question question) {
        question.answers().forEach(answer -> ioService.printFormattedLine("    %s", answer.text()));
    }
}

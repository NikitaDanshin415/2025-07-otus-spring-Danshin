package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    @Override
    public void run(Student student) {
        try {
            if (Objects.isNull(student)) {
                ioService.printLineLocalized("LoginContext.login.fail");
                return;
            }

            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (Exception e) {
            ioService.printLineLocalized("TestRunner.error");
        }
    }

}

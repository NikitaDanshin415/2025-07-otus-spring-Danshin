package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final IOService ioService;

    @Override
    public void run() {
        try {
            testService.executeTest();
        } catch (Exception e) {
            ioService.printLine("An error occurred during the execution of the program. Please contact the developer.");
        }
    }
}

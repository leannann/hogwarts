package ru.skypro.hogwarts.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
public class InfoController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/port")
    public String getServerPort() {
        return "Application is running on port: " + serverPort;
    }

    @GetMapping("/sum")
    public int getParallelSum() {
        return IntStream.rangeClosed(1, 1_000_000)
                .parallel()
                .reduce(0, Integer::sum);
    }
}
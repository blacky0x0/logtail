package com.blacky;


import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

@RestController
@EnableAutoConfiguration
public class LogTail {

    public static Logger LOG = LoggerFactory.getLogger(LogTail.class);

    public static final String HTML_BREAKLINE = "<br>";
    public static final int HTML_BREAKLINE_LENGTH = HTML_BREAKLINE.length();

    public static final int MAX_LINES = 500;
    public static int COUNT_DOWN = MAX_LINES;
    public static final StringBuilder SERVER_LOG = new StringBuilder();
    public static final Queue<Integer> LINE_LENGTH_QUEUE = new LinkedList<>();

    @RequestMapping("/")
    String home() {
        return String.format("<div style='margin-left:40px'>%s</div>", SERVER_LOG.toString());
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LogTail.class, args);

        TailerListener listener = new MyTailerListener();
        String fileName = "/workspace/logtail/file.txt";
        Tailer.create(new File(fileName), listener, 500);
    }

    public static class MyTailerListener extends TailerListenerAdapter {
        public void handle(String line) {
            LINE_LENGTH_QUEUE.add(line.length() + HTML_BREAKLINE_LENGTH);
            SERVER_LOG.append(line).append(HTML_BREAKLINE);

            if (COUNT_DOWN > 0) {
                COUNT_DOWN--;
            } else {
                SERVER_LOG.delete(0, LINE_LENGTH_QUEUE.poll());
            }
        }
    }
}

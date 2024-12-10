/*
 * Copyright 2024 Josue Ruiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vgtools.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import com.vgtools.enums.KeyboardKey;
import com.vgtools.windowflow.CobolWindowFlow;

public class Command {
    public static String send(KeyboardKey key, String condition, CobolWindowFlow repository)
            throws Exception {
        return send(key.getValue(), new String[] { condition }, repository, Duration.ofSeconds(5));
    }

    public static String send(String input, String condition, CobolWindowFlow repository)
            throws Exception {
        return send(input, new String[] { condition }, repository, Duration.ofSeconds(5));
    }

    public static String send(String input, String[] conditions, CobolWindowFlow repository)
            throws Exception {
        return send(input, conditions, repository, Duration.ofSeconds(5));
    }

    public static String send(String input, String[] conditions, CobolWindowFlow repository, Duration duration)
            throws Exception {

        InputStream consoleOutput = repository.getConsoleOutput();
        OutputStream consoleInput = repository.getConsoleInput();
        char[][] console = repository.getConsole();
        String fileName = repository.getLogsFileName();
        // int[] cursor = repository.getConsoleCursor();

        consoleInput.write(input.getBytes());
        consoleInput.flush();

        StringBuilder output = new StringBuilder();
        StringBuilder outputFormatted = new StringBuilder();
        long starTime = System.currentTimeMillis();
        byte[] temp = new byte[1024];
        reading: while (true) {
            Thread.sleep(100);
            while (consoleOutput.available() > 0) {
                int i = consoleOutput.read(temp, 0, 1024);
                if (i < 0)
                    break;
                output.append(new String(temp, 0, i));
                String replacedText = toRawString(output.toString());
                outputFormatted.append(replacedText);
            }
            String ouputFormatedStr = outputFormatted.toString();
            for (int i = 0; i < conditions.length; i++) {
                if (ouputFormatedStr.contains(conditions[i])) {
                    break reading;
                }
            }
            if (System.currentTimeMillis() - starTime >= duration.toMillis())
                throw new TimeoutException();
        }
        String replacedText = outputFormatted.toString();
        // try {
        // ConsoleMatrixManager.update(replacedText, matrix, cursor);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        log(input, replacedText, console, fileName);
        return replacedText;
    }

    private static void log(String input, String output, char[][] console, String fileName) {
        input = toRawString(input);
        String path = "logs";
        if (fileName == null) {
            System.out.println("filename is null");
            return;
        }
        File folder = new File(path);
        if (!folder.exists())
            folder.mkdirs();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(path + File.separator + fileName + ".txt", true))) {
            writer.write("Input: {" + input + "}\n");
            writer.write("Output:");
            writer.write("\u001B[2J");
            writer.write("\u001B[H");
            writer.write(toFormattedString(output));
            writer.write("\u001B[2J");
            writer.write("\u001B[H");
            writer.write("\u001B[0m");
            writer.write("###############################################################");
            writer.write("\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String toRawString(String str) {
        return str.toString()
                .replaceAll("\u0001", "CTRL+A")
                .replaceAll("\u001B", "\\\\x1b")
                .replaceAll("\u000F", "\\\\x0f")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\r", "\\\\r");
    }

    private static String toFormattedString(String str) {
        return str.toString()
                .replaceAll("\\\\x1b", "\u001B")
                .replaceAll("\\\\x0f", "\u000F")
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\r", "\r");
    }
}

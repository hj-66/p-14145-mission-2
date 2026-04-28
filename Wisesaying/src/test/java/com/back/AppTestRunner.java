package com.back;

import com.back.standard.util.TestUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class AppTestRunner {
    private static final Path DB_PATH = Path.of("db/wiseSaying");

    public static String run(String input) {
        Map<Path, byte[]> backupFiles = backupDb();
        resetDb();

        Scanner scanner = TestUtil.genScanner(inputWithExit(input));
        ByteArrayOutputStream output = TestUtil.setOutToByteArray();

        try {
            new App(scanner).run();
            return output.toString();
        } finally {
            TestUtil.clearSetOutToByteArray();
            restoreDb(backupFiles);
        }
    }

    private static String inputWithExit(String input) {
        if (input.strip().endsWith("종료")) return input;

        return input + "\n종료";
    }

    private static Map<Path, byte[]> backupDb() {
        Map<Path, byte[]> backupFiles = new HashMap<>();

        if (!Files.exists(DB_PATH)) return backupFiles;

        try (Stream<Path> paths = Files.list(DB_PATH)) {
            for (Path path : paths.toList()) {
                if (Files.isRegularFile(path)) {
                    backupFiles.put(path, Files.readAllBytes(path));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return backupFiles;
    }

    private static void resetDb() {
        try {
            Files.createDirectories(DB_PATH);
            deleteAllDbFiles();
            Files.writeString(DB_PATH.resolve("lastId.txt"), "0");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void restoreDb(Map<Path, byte[]> backupFiles) {
        try {
            Files.createDirectories(DB_PATH);
            deleteAllDbFiles();

            for (Map.Entry<Path, byte[]> entry : backupFiles.entrySet()) {
                Files.write(entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteAllDbFiles() throws IOException {
        if (!Files.exists(DB_PATH)) return;

        try (Stream<Path> paths = Files.list(DB_PATH)) {
            for (Path path : paths.toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}

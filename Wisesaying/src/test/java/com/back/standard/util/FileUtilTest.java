package com.back.standard.util;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.repository.WiseSayingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilTest {
    private final Path dbPath = Path.of("db/wiseSaying");
    private Map<Path, byte[]> backupFiles;

    @BeforeEach
    void beforeEach() throws IOException {
        backupDb();
        resetDb();
    }

    @AfterEach
    void afterEach() throws IOException {
        restoreDb();
    }

    @Test
    @DisplayName("명언 파일 생성과 lastId 갱신")
    void t1() throws IOException {
        WiseSayingRepository wiseSayingRepository = new WiseSayingRepository();

        int id = wiseSayingRepository.create("현재를 사랑하라.", "작자미상");

        assertThat(id).isEqualTo(1);
        assertThat(Files.exists(dbPath.resolve("1.json"))).isTrue();
        assertThat(Files.readString(dbPath.resolve("lastId.txt"))).contains("1");
    }

    @Test
    @DisplayName("명언 파일 조회")
    void t2() {
        WiseSayingRepository wiseSayingRepository = new WiseSayingRepository();
        wiseSayingRepository.create("현재를 사랑하라.", "작자미상");

        WiseSaying wiseSaying = wiseSayingRepository.findById(1);

        assertThat(wiseSaying.id).isEqualTo(1);
        assertThat(wiseSaying.content).isEqualTo("현재를 사랑하라.");
        assertThat(wiseSaying.author).isEqualTo("작자미상");
    }

    @Test
    @DisplayName("명언 파일 수정")
    void t3() {
        WiseSayingRepository wiseSayingRepository = new WiseSayingRepository();
        wiseSayingRepository.create("현재를 사랑하라.", "작자미상");

        wiseSayingRepository.update(1, "나의 죽음을 적들에게 알리지 말라!", "이순신");
        WiseSaying wiseSaying = wiseSayingRepository.findById(1);

        assertThat(wiseSaying.id).isEqualTo(1);
        assertThat(wiseSaying.content).isEqualTo("나의 죽음을 적들에게 알리지 말라!");
        assertThat(wiseSaying.author).isEqualTo("이순신");
    }

    @Test
    @DisplayName("명언 파일 삭제")
    void t4() {
        WiseSayingRepository wiseSayingRepository = new WiseSayingRepository();
        wiseSayingRepository.create("현재를 사랑하라.", "작자미상");

        wiseSayingRepository.deleteById(1);

        assertThat(Files.exists(dbPath.resolve("1.json"))).isFalse();
        assertThat(wiseSayingRepository.findById(1)).isNull();
    }

    private void backupDb() throws IOException {
        backupFiles = new HashMap<>();

        if (!Files.exists(dbPath)) return;

        try (Stream<Path> paths = Files.list(dbPath)) {
            for (Path path : paths.toList()) {
                if (Files.isRegularFile(path)) {
                    backupFiles.put(path, Files.readAllBytes(path));
                }
            }
        }
    }

    private void resetDb() throws IOException {
        Files.createDirectories(dbPath);
        deleteAllDbFiles();
        Files.writeString(dbPath.resolve("lastId.txt"), "0");
    }

    private void restoreDb() throws IOException {
        Files.createDirectories(dbPath);
        deleteAllDbFiles();

        for (Map.Entry<Path, byte[]> entry : backupFiles.entrySet()) {
            Files.write(entry.getKey(), entry.getValue());
        }
    }

    private void deleteAllDbFiles() throws IOException {
        if (!Files.exists(dbPath)) return;

        try (Stream<Path> paths = Files.list(dbPath)) {
            for (Path path : paths.toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}

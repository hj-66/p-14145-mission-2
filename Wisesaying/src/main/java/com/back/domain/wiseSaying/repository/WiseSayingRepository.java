package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;

import java.io.FileReader;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class WiseSayingRepository {
    Gson gson = new Gson();

    public int getCount() {
        int count = -1;
        try (FileReader reader = new FileReader("db/wiseSaying/lastId.txt")) {
            count = gson.fromJson(reader, int.class);  // ⭐ 핵심
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int create(String content, String author) {
        int count = getCount() + 1;

        WiseSaying ws = new WiseSaying(count, content, author);

        try (FileWriter writer = new FileWriter("db/wiseSaying/%d.json".formatted(count))) {
            gson.toJson(ws, writer);  // ⭐ 핵심
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter("db/wiseSaying/lastId.txt")) {
            gson.toJson(count, writer);  // ⭐ 핵심
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    public WiseSaying findById(int id) {
        try (FileReader reader = new FileReader("db/wiseSaying/%d.json".formatted(id))) {
            return gson.fromJson(reader, WiseSaying.class);
        } catch (IOException e) {
            return null;
        }
    }

    public void deleteById(int id) {
        File file = new File("db/wiseSaying/%d.json".formatted(id));

        if (file.exists()) {
            if (!file.delete()) {
                System.out.println("파일 삭제 실패");
            }
        } else {
            System.out.println("파일이 존재하지 않습니다.");
        }

    }

    public void update(int id, String new_content, String new_author) {
        try {
            FileReader reader = new FileReader("db/wiseSaying/%d.json".formatted(id));
            WiseSaying ws = gson.fromJson(reader, WiseSaying.class);
            reader.close();

            if (ws.id == id) {
                ws.author = new_author;
                ws.content = new_content;
            }

            FileWriter writer = new FileWriter("db/wiseSaying/%d.json".formatted(id));
            gson.toJson(ws, writer);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
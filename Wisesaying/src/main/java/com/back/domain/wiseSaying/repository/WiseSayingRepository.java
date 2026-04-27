package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WiseSayingRepository {
    List<WiseSaying> WiseSayings = new ArrayList<>();

    public void create(int id, String content, String author) {
        WiseSayings.add(new WiseSaying(id, content, author));
    }

    public WiseSaying findById(int id) {
        Optional<WiseSaying> wise = WiseSayings.stream().filter(w -> w.id == id).findFirst();

        return wise.orElse(null);
    }

    public void deleteById(int id) {
        WiseSayings.removeIf(w -> w.id == id);
    }

    public void update(int id, String new_content, String new_author) {
        WiseSayings = WiseSayings.stream()
                .map(w -> {
                    if (w.id == id) {
                        return new WiseSaying(id, new_content, new_author);
                    }
                    return w;
                })
                .toList();
    }
}

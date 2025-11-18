package com.project.concurrency.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@NoArgsConstructor
public class Task {

    private Long id;
    private String title;
    private String description;

    public Task(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}

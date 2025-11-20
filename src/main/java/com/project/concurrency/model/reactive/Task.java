package com.project.concurrency.model.reactive;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "task")
public class Task {

    @Id
    @Column("task_id")
    private Long id;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    public Task(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}

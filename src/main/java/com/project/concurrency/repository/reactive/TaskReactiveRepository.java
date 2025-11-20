package com.project.concurrency.repository.reactive;

import com.project.concurrency.model.reactive.Task;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface TaskReactiveRepository extends R2dbcRepository<Task, Long> {
}


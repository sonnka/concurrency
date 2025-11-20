package com.project.webflux_concurrency.repository;

import com.project.webflux_concurrency.model.Task;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface TaskRepository extends R2dbcRepository<Task, Long> {
}


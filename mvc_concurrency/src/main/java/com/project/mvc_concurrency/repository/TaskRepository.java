package com.project.mvc_concurrency.repository;

import com.project.mvc_concurrency.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}


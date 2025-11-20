package com.project.concurrency.repository.jpa;

import com.project.concurrency.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}


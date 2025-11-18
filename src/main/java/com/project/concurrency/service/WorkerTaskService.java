package com.project.concurrency.service;

import com.project.concurrency.model.Task;
import com.project.concurrency.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class WorkerTaskService {

    private final TaskRepository repository;
    private final ExecutorService executor;

    public WorkerTaskService(TaskRepository repository,
                             @Qualifier("workerExecutor") ExecutorService executor) {
        this.repository = repository;
        this.executor = executor;
    }

    public CompletableFuture<Task> create(Task task) {
        return CompletableFuture.supplyAsync(() -> repository.save(task), executor);
    }

    public CompletableFuture<Optional<Task>> update(Long id, Task task) {
        return CompletableFuture.supplyAsync(() -> repository.update(id, task), executor);
    }

    public CompletableFuture<Optional<Task>> get(Long id) {
        return CompletableFuture.supplyAsync(() -> repository.findById(id), executor);
    }

    public CompletableFuture<Void> delete(Long id) {
        return CompletableFuture.runAsync(() -> repository.deleteById(id), executor);
    }
}

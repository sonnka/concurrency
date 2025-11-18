package com.project.concurrency.service;

import com.project.concurrency.model.Task;
import com.project.concurrency.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WorkerTaskService {

    private final TaskRepository repository;
    private final ExecutorService executor;

    public WorkerTaskService(TaskRepository repository) {
        this.repository = repository;
        this.executor = Executors.newFixedThreadPool(16);
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

    public CompletableFuture<List<Task>> getAll() {
        return CompletableFuture.supplyAsync(repository::findAll, executor);
    }

    public CompletableFuture<Void> delete(Long id) {
        return CompletableFuture.runAsync(() -> repository.deleteById(id), executor);
    }
}

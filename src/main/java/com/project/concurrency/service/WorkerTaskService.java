package com.project.concurrency.service;

import com.project.concurrency.model.Task;
import com.project.concurrency.repository.jpa.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CompletableFuture<Optional<Task>> update(Long id, Task updatedTask) {
        Task task = repository.findById(id).orElse(null);
        if(task != null && updatedTask != null) {
            if(updatedTask.getTitle() != null) {
                task.setTitle(updatedTask.getTitle());
            }
            if(updatedTask.getDescription() != null) {
                task.setDescription(updatedTask.getDescription());
            }
            return CompletableFuture.supplyAsync(() -> Optional.of(repository.save(task)), executor);
        }
        return CompletableFuture.supplyAsync(Optional::empty, executor);
    }

    public CompletableFuture<Optional<Task>> get(Long id) {
        return CompletableFuture.supplyAsync(() -> repository.findById(id), executor);
    }

    @Transactional("transactionManager")
    public CompletableFuture<Void> delete(Long id) {
        return CompletableFuture.runAsync(() -> repository.deleteById(id), executor);
    }
}

package com.project.mvc_concurrency.controller;

import com.project.mvc_concurrency.model.Task;
import com.project.mvc_concurrency.service.WorkerTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/worker/tasks")
public class WorkerTaskController {

    private final WorkerTaskService service;

    public WorkerTaskController(WorkerTaskService service) {
        this.service = service;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<Task>> create(@RequestBody Task task) {
        return service.create(task)
                .thenApply(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @PatchMapping("/{id}")
    public CompletableFuture<ResponseEntity<Task>> update(@PathVariable Long id, @RequestBody Task task) {
        return service.update(id, task)
                .thenApply(opt -> opt.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<Task>> get(@PathVariable Long id) {
        return service.get(id)
                .thenApply(opt -> opt.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return service.delete(id)
                .thenApply(v -> ResponseEntity.noContent().build());
    }
}

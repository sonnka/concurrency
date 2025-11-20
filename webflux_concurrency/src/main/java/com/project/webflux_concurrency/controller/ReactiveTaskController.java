package com.project.webflux_concurrency.controller;

import com.project.webflux_concurrency.model.Task;
import com.project.webflux_concurrency.service.ReactiveTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reactive/tasks")
public class ReactiveTaskController {

    private final ReactiveTaskService service;

    public ReactiveTaskController(ReactiveTaskService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Task>> create(@RequestBody Task task) {
        return service.create(task)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Task>> update(@PathVariable Long id, @RequestBody Task task) {
        return service.update(id, task)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Task>> get(@PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return service.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}

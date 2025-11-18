package com.project.concurrency.service;

import com.project.concurrency.model.Task;
import com.project.concurrency.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReactiveTaskService {

    private final TaskRepository repository;

    public ReactiveTaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Mono<Task> create(Task task) {
        return Mono.fromCallable(() -> repository.save(task));
    }

    public Mono<Task> update(Long id, Task task) {
        return Mono.defer(() -> Mono.justOrEmpty(repository.update(id, task)));
    }

    public Mono<Task> get(Long id) {
        return Mono.defer(() -> Mono.justOrEmpty(repository.findById(id)));
    }

    public Flux<Task> getAll() {
        return Flux.defer(() -> Flux.fromIterable(repository.findAll()));
    }

    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() -> repository.deleteById(id));
    }
}

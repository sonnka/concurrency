package com.project.concurrency.service;

import com.project.concurrency.model.Task;
import com.project.concurrency.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ReactiveTaskService {

    private final TaskRepository repository;

    public ReactiveTaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Mono<Task> create(Task task) {
        return Mono.fromCallable(() -> repository.save(task))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Task> update(Long id, Task task) {
        return Mono.fromCallable(() -> repository.update(id, task))
                .flatMap(Mono::justOrEmpty)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Task> get(Long id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .flatMap(Mono::justOrEmpty)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> delete(Long id) {
        return Mono.fromRunnable(() -> repository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}

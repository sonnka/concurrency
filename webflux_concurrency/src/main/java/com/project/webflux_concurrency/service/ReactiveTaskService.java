package com.project.webflux_concurrency.service;

import com.project.webflux_concurrency.model.Task;
import com.project.webflux_concurrency.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class ReactiveTaskService {

    private final TaskRepository repository;

    public ReactiveTaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Mono<Task> create(Task task) {
        return repository.save(task);
    }

    public Mono<Task> get(Long id) {
        return repository.findById(id);
    }

    public Mono<Task> update(Long id, Task updatedTask) {
        if (updatedTask == null) {
            return Mono.empty();
        }

        return repository.findById(id)
                .flatMap(existingTask -> {
                    if (updatedTask.getTitle() != null) {
                        existingTask.setTitle(updatedTask.getTitle());
                    }
                    if (updatedTask.getDescription() != null) {
                        existingTask.setDescription(updatedTask.getDescription());
                    }
                    return repository.save(existingTask);
                });
    }

    @Transactional
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }
}

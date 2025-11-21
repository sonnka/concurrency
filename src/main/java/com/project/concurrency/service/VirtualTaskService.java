package com.project.concurrency.service;

import com.project.concurrency.model.Task;
import com.project.concurrency.repository.jpa.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VirtualTaskService {

    private final TaskRepository repository;

    public VirtualTaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task create(Task task) {
        return repository.save(task);
    }

    public Optional<Task> update(Long id, Task updatedTask) {
        Task task = repository.findById(id).orElse(null);
        if(task != null && updatedTask != null) {
            if(updatedTask.getTitle() != null) {
                task.setTitle(updatedTask.getTitle());
            }
            if(updatedTask.getDescription() != null) {
                task.setDescription(updatedTask.getDescription());
            }
            return Optional.of(repository.save(task));
        }
        return Optional.empty();
    }

    public Optional<Task> get(Long id) {
        return repository.findById(id);
    }

    @Transactional("transactionManager")
    public void delete(Long id) {
        repository.deleteById(id);
    }
}


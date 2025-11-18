package com.project.concurrency.service;

import com.project.concurrency.model.Task;
import com.project.concurrency.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Optional<Task> update(Long id, Task task) {
        return repository.update(id, task);
    }

    public Optional<Task> get(Long id) {
        return repository.findById(id);
    }

    public List<Task> getAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}


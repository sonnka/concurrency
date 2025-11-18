package com.project.concurrency.repository;

import com.project.concurrency.model.Task;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TaskRepository {

    private final ConcurrentMap<Long, Task> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(seq.getAndIncrement());
        }
        storage.put(task.getId(), task);
        return task;
    }

    public Optional<Task> update(Long id, Task task) {
        Optional<Task> initialTask = Optional.ofNullable(storage.get(id));
        if (initialTask.isPresent()) {
            var updatedTask = initialTask.get();
            if (task.getTitle() != null && !task.getTitle().isEmpty()) {
                updatedTask.setTitle(task.getTitle());
            }
            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                updatedTask.setDescription(task.getDescription());
            }
            return Optional.ofNullable(storage.replace(id, updatedTask));
        }
        return initialTask;
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }
}


package com.project.webflux_concurrency.model;

import com.project.concurrency.grpc.TaskDto;

public class TaskMapper {

    public static TaskDto toDto(Task t) {
        return TaskDto.newBuilder()
                .setId(t.getId())
                .setTitle(t.getTitle())
                .setDescription(t.getDescription())
                .build();
    }
}

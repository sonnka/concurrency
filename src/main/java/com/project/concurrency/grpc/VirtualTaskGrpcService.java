package com.project.concurrency.grpc;

import com.google.protobuf.Empty;
import com.project.concurrency.model.Task;
import com.project.concurrency.model.TaskMapper;
import com.project.concurrency.service.VirtualTaskService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;

@GrpcService
public class VirtualTaskGrpcService extends VirtualTaskServiceGrpc.VirtualTaskServiceImplBase {

    private final VirtualTaskService service;

    public VirtualTaskGrpcService(VirtualTaskService service) {
        this.service = service;
    }

    @Override
    public void create(CreateTaskRequest request, StreamObserver<TaskDto> responseObserver) {
        Task saved = service.create(new Task(null, request.getTitle(), request.getDescription()));
        responseObserver.onNext(TaskMapper.toDto(saved));
        responseObserver.onCompleted();
    }

    @Override
    public void update(TaskDto request, StreamObserver<TaskDto> responseObserver) {
        Optional<Task> opt = service.update(request.getId(), new Task(request.getId(), request.getTitle(),
                request.getDescription()));
        if (opt.isPresent()) {
            responseObserver.onNext(TaskMapper.toDto(opt.get()));
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Task not found").asRuntimeException());
        }
    }

    @Override
    public void get(TaskRequest request, StreamObserver<TaskDto> responseObserver) {
        Optional<Task> opt = service.get(request.getId());
        if (opt.isPresent()) {
            responseObserver.onNext(TaskMapper.toDto(opt.get()));
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("Task not found").asRuntimeException());
        }
    }

    @Override
    public void list(Empty request, StreamObserver<TaskList> responseObserver) {
        List<Task> tasks = service.getAll();
        TaskList.Builder builder = TaskList.newBuilder();
        tasks.forEach(t -> builder.addTasks(TaskMapper.toDto(t)));
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}

package com.project.mvc_concurrency.grpc;

import com.google.protobuf.Empty;
import com.project.concurrency.grpc.CreateTaskRequest;
import com.project.concurrency.grpc.TaskDto;
import com.project.concurrency.grpc.TaskRequest;
import com.project.concurrency.grpc.WorkerTaskServiceGrpc;
import com.project.mvc_concurrency.model.Task;
import com.project.mvc_concurrency.model.TaskMapper;
import com.project.mvc_concurrency.service.WorkerTaskService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class WorkerTaskGrpcService extends WorkerTaskServiceGrpc.WorkerTaskServiceImplBase {

    private final WorkerTaskService service;

    public WorkerTaskGrpcService(WorkerTaskService service) {
        this.service = service;
    }

    @Override
    public void create(CreateTaskRequest request, StreamObserver<TaskDto> responseObserver) {
        Task task = new Task(null, request.getTitle(), request.getDescription());
        service.create(task)
                .thenAccept(saved -> {
                    TaskDto dto = TaskMapper.toDto(saved);
                    responseObserver.onNext(dto);
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> handleError(ex, responseObserver));
    }

    @Override
    public void update(TaskDto request, StreamObserver<TaskDto> responseObserver) {
        service.update(request.getId(), new Task(request.getId(), request.getTitle(), request.getDescription()))
                .thenAccept(opt -> {
                    if (opt.isPresent()) {
                        responseObserver.onNext(TaskMapper.toDto(opt.get()));
                        responseObserver.onCompleted();
                    } else {
                        responseObserver.onError(
                                Status.NOT_FOUND.withDescription("Task not found").asRuntimeException());
                    }
                })
                .exceptionally(ex -> handleError(ex, responseObserver));
    }

    @Override
    public void get(TaskRequest request, StreamObserver<TaskDto> responseObserver) {
        service.get(request.getId())
                .thenAccept(opt -> {
                    if (opt.isPresent()) {
                        responseObserver.onNext(TaskMapper.toDto(opt.get()));
                        responseObserver.onCompleted();
                    } else {
                        responseObserver.onError(
                                Status.NOT_FOUND.withDescription("Task not found").asRuntimeException());
                    }
                })
                .exceptionally(ex -> handleError(ex, responseObserver));
    }

    @Override
    public void delete(TaskRequest request, StreamObserver<Empty> responseObserver) {
        service.delete(request.getId())
                .thenRun(() -> {
                    responseObserver.onNext(Empty.getDefaultInstance());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> handleError(ex, responseObserver));
    }

    private <T> Void handleError(Throwable ex, StreamObserver<T> responseObserver) {
        responseObserver.onError(Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException());
        return null;
    }
}
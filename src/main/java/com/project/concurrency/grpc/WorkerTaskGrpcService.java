package com.project.concurrency.grpc;

import com.google.protobuf.Empty;
import com.project.concurrency.model.Task;
import com.project.concurrency.model.TaskMapper;
import com.project.concurrency.service.WorkerTaskService;
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
//                .exceptionally(ex -> {
//                    responseObserver.onError(
//                            Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException());
//                    return null;
//                })
        ;
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
                .exceptionally(ex -> {
                    responseObserver.onError(
                            Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException());
                    return null;
                });
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
                .exceptionally(ex -> {
                    responseObserver.onError(
                            Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException());
                    return null;
                });
    }

    @Override
    public void list(Empty request, StreamObserver<TaskList> responseObserver) {
        service.getAll()
                .thenAccept(list -> {
                    TaskList.Builder builder = TaskList.newBuilder();
                    for (Task t : list) {
                        builder.addTasks(TaskMapper.toDto(t));
                    }
                    responseObserver.onNext(builder.build());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    responseObserver.onError(
                            Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException());
                    return null;
                });
    }
}
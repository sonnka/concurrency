package com.project.concurrency.grpc;

import com.google.protobuf.Empty;
import com.project.concurrency.grpc.interceptor.RequestContext;
import com.project.concurrency.model.Task;
import com.project.concurrency.model.TaskMapper;
import com.project.concurrency.service.WorkerTaskService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class WorkerTaskGrpcService extends WorkerTaskServiceGrpc.WorkerTaskServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(WorkerTaskGrpcService.class);
    private final WorkerTaskService service;

    public WorkerTaskGrpcService(WorkerTaskService service) {
        this.service = service;
    }

    @Override
    public void create(CreateTaskRequest request, StreamObserver<TaskDto> responseObserver) {
        String userId = RequestContext.getUserId();

        log.info("gRPC IN: service={}, method=create, userId={}",
                WorkerTaskGrpcService.class.getSimpleName(),
                userId
        );

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
        String userId = RequestContext.getUserId();

        log.info("gRPC IN: service={}, method=update, userId={}",
                WorkerTaskGrpcService.class.getSimpleName(),
                userId
        );

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
        String userId = RequestContext.getUserId();

        log.info("gRPC IN: service={}, method=get, userId={}",
                WorkerTaskGrpcService.class.getSimpleName(),
                userId
        );

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
        String userId = RequestContext.getUserId();

        log.info("gRPC IN: service={}, method=delete, userId={}",
                WorkerTaskGrpcService.class.getSimpleName(),
                userId
        );

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
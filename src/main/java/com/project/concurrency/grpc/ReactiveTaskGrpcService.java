package com.project.concurrency.grpc;

import com.google.protobuf.Empty;
import com.project.concurrency.model.Task;
import com.project.concurrency.model.TaskMapper;
import com.project.concurrency.service.ReactiveTaskService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;

@GrpcService
public class ReactiveTaskGrpcService extends ReactiveTaskServiceGrpc.ReactiveTaskServiceImplBase {

    private final ReactiveTaskService service;

    public ReactiveTaskGrpcService(ReactiveTaskService service) {
        this.service = service;
    }

    @Override
    public void create(CreateTaskRequest request, StreamObserver<TaskDto> responseObserver) {
        Mono<Task> mono = service.create(new Task(null, request.getTitle(), request.getDescription()));
        mono.subscribe(
                task -> {
                    responseObserver.onNext(TaskMapper.toDto(task));
                    responseObserver.onCompleted();
                },
                ex -> responseObserver.onError(
                        Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException())
        );
    }

    @Override
    public void update(TaskDto request, StreamObserver<TaskDto> responseObserver) {
        Mono<Task> mono = service.update(request.getId(), new Task(request.getId(), request.getTitle(),
                request.getDescription()));
        mono.switchIfEmpty(Mono.error(Status.NOT_FOUND.withDescription("Task not found").asRuntimeException()))
                .subscribe(
                        task -> {
                            responseObserver.onNext(TaskMapper.toDto(task));
                            responseObserver.onCompleted();
                        },
                        responseObserver::onError
                );
    }

    @Override
    public void get(TaskRequest request, StreamObserver<TaskDto> responseObserver) {
        Mono<Task> mono = service.get(request.getId());
        mono.switchIfEmpty(Mono.error(Status.NOT_FOUND.withDescription("Task not found").asRuntimeException()))
                .subscribe(
                        task -> {
                            responseObserver.onNext(TaskMapper.toDto(task));
                            responseObserver.onCompleted();
                        },
                        responseObserver::onError
                );
    }

    @Override
    public void delete(TaskRequest request, StreamObserver<Empty> responseObserver) {
        service.delete(request.getId())
                .subscribe(
                        unused -> {},
                        ex -> responseObserver.onError(
                                Status.INTERNAL.withDescription(ex.getMessage()).asRuntimeException()
                        ),
                        () -> {
                            responseObserver.onNext(Empty.getDefaultInstance());
                            responseObserver.onCompleted();
                        }
                );
    }
}

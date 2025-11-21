package com.project.concurrency.grpc;

import com.google.protobuf.Empty;
import com.project.concurrency.grpc.interceptor.RequestContext;
import com.project.concurrency.model.Task;
import com.project.concurrency.model.TaskMapper;
import com.project.concurrency.service.VirtualTaskService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@GrpcService
public class VirtualTaskGrpcService extends VirtualTaskServiceGrpc.VirtualTaskServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(VirtualTaskGrpcService.class);
    private final VirtualTaskService service;

    public VirtualTaskGrpcService(VirtualTaskService service) {
        this.service = service;
    }

    @Override
    public void create(CreateTaskRequest request, StreamObserver<TaskDto> responseObserver) {
        String userId = RequestContext.getUserId();

        log.info("gRPC IN: service={}, method=create, userId={}",
                VirtualTaskGrpcService.class.getSimpleName(),
                userId
        );

        Task saved = service.create(new Task(null, request.getTitle(), request.getDescription()));
        responseObserver.onNext(TaskMapper.toDto(saved));
        responseObserver.onCompleted();
    }

    @Override
    public void update(TaskDto request, StreamObserver<TaskDto> responseObserver) {
        String userId = RequestContext.getUserId();

        log.info("gRPC IN: service={}, method=update, userId={}",
                VirtualTaskGrpcService.class.getSimpleName(),
                userId
        );

        Optional<Task> opt = service.update(request.getId(), new Task(request.getId(), request.getTitle(),
                request.getDescription()));
        if (opt.isPresent()) {
            responseObserver.onNext(TaskMapper.toDto(opt.get()));
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Task not found")
                    .asRuntimeException());
        }
    }

    @Override
    public void get(TaskRequest request, StreamObserver<TaskDto> responseObserver) {
        String userId = RequestContext.getUserId();

        log.info("gRPC IN: service={}, method=get, userId={}",
                VirtualTaskGrpcService.class.getSimpleName(),
                userId
        );

        Optional<Task> opt = service.get(request.getId());
        if (opt.isPresent()) {
            responseObserver.onNext(TaskMapper.toDto(opt.get()));
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Task not found")
                    .asRuntimeException());
        }
    }

    @Override
    public void delete(TaskRequest request, StreamObserver<Empty> responseObserver) {
        String userId = RequestContext.getUserId();

        log.info("gRPC IN: service={}, method=delete, userId={}",
                VirtualTaskGrpcService.class.getSimpleName(),
                userId
        );

        service.delete(request.getId());
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}

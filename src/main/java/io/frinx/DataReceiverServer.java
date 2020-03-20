package io.frinx;

import com.google.protobuf.Empty;
import io.frinx.datareceiver.DataRequest;
import io.frinx.datareceiver.DataReceiverGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import org.slf4j.LoggerFactory;

public class DataReceiverServer {

    private static final org.slf4j.Logger logger
        = LoggerFactory.getLogger(DataReceiverServer.class);
    private Server server;

    private void start(DbAccess da) throws IOException {
        server = ServerBuilder.forPort(Constants.PORT)
            .addService(new DataReceiverImpl(da))
            .intercept(new AuthorizationInterceptor(da))
            .build()
            .start();
        logger.info("Server started, listening on " + Constants.PORT);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                DataReceiverServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

   public static void startDataReceiver(DbAccess da) throws IOException, InterruptedException {
       final DataReceiverServer server = new DataReceiverServer();
       server.start(da);
       server.blockUntilShutdown();
   }

    static class DataReceiverImpl extends DataReceiverGrpc.DataReceiverImplBase {

        private final DbAccess da;

        public DataReceiverImpl(DbAccess da) {
            this.da = da;
        }

        @Override
        public void sendData(DataRequest req, StreamObserver<Empty> responseObserver) {
            responseObserver.onNext(Empty.getDefaultInstance());
            da.storeDeviceData(req.getDeviceName(), req.getDeviceData());
            responseObserver.onCompleted();
        }
    }
}

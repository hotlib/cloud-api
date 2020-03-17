package io.frinx;

import io.frinx.datareceiver.DataReceiverGrpc;
import io.frinx.datareceiver.DataRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataReceiverClient {
    private static final Logger logger = Logger.getLogger(DataReceiverClient.class.getName());

    private final ManagedChannel channel;
    private final DataReceiverGrpc.DataReceiverBlockingStub blockingStub;

    public DataReceiverClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(new AppendApiKeyInterceptor())
            .build());
    }

    DataReceiverClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = DataReceiverGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void sendData() {
        DataRequest request = DataRequest.newBuilder().setDeviceData("{\"bbb\":\"1112\"}").setDeviceName("{\"bbb\":\"11112\"}").build();
        try {
            blockingStub.sendData(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        DataReceiverClient client = new DataReceiverClient("localhost", 50051);
        try {
            client.sendData();
        } finally {
            client.shutdown();
        }
    }
}
